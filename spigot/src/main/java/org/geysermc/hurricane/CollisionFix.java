package org.geysermc.hurricane;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.PointedDripstone;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.*;
import java.util.Arrays;

public final class CollisionFix implements Listener {
    private final boolean bambooEnabled;
    private final BoundingBox originalBambooBoundingBox = box(6.5D, 0.0D, 6.5D, 9.5D, 16D, 9.5D);

    private final boolean pointedDripstoneEnabled;
    private final BoundingBox tipMergeDripstoneBox = box(5D, 0D, 5D, 11D, 16D, 11D);
    private final BoundingBox tipUpDripstoneBox = box(5D, 0D, 5D, 11D, 11D, 11D);
    private final BoundingBox tipDownDripstoneBox = box(5D, 5D, 5D, 11D, 16D, 11D);
    private final BoundingBox frustumDripstoneBox = box(4D, 0D, 4D, 12D, 16D, 12D);
    private final BoundingBox middleDripstoneBox = box(3D, 0D, 3D, 13D, 16D, 13D);
    private final BoundingBox baseDripstoneBox = box(2D, 0D, 2D, 14D, 16D, 14D);

    public CollisionFix(Plugin plugin, boolean bambooEnabled, boolean pointedDripstoneEnabled, boolean turtleEggEnabled, boolean copperBarsEnabled) {
        // Make any given block have zero collision. Lagback solved...!
        this.bambooEnabled = bambooEnabled;
        this.pointedDripstoneEnabled = pointedDripstoneEnabled;

        if (bambooEnabled) {
            try {
                final Class<?> bambooBlockClass = NMSReflection.getNMSClass("world.level.block", "BlockBamboo", "BambooStalkBlock");
                final boolean newerThanOrEqualTo1170 = NMSReflection.mojmap;
                // Codec field being first bumps all fields - as of 1.20.4
                final boolean newerThanOrEqualTo1204 = Arrays.stream(bambooBlockClass.getFields()).anyMatch(field -> field.getType().getSimpleName().equals("MapCodec"));
                final boolean newerThanOrEqualTo1215 = NMSReflection.getNMSClass("world.level.block", "LeafLitterBlock") != null;
                // On mojmap (26.1+) the field has its real name; on Spigot mappings it's obfuscated
                String fieldName;
                if (bambooBlockClass.getName().contains("BambooStalkBlock")) {
                    fieldName = "SHAPE_COLLISION"; // mojmap name
                } else {
                    fieldName = newerThanOrEqualTo1215 ? "S" : newerThanOrEqualTo1204 ? "g" : newerThanOrEqualTo1170 ? "f" : "c";
                }
                final Field bambooBoundingBox = ReflectionAPI.getFieldAccessible(bambooBlockClass, fieldName);
                applyNoBoundingBox(bambooBoundingBox);
                plugin.getLogger().info("Bamboo collision hack enabled.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (pointedDripstoneEnabled) {
            // We need to disable all dripstone collision, and there's six...
            try {
                final Class<?> dripstoneBlockClass = NMSReflection.getMojmapNMSClass("world.level.block.PointedDripstoneBlock");
                // The method names change between versions, but there's always six next to each other.
                // There is one we do not need to touch (1.18+) because it doesn't deal with collision.
                boolean foundBoundingBoxes = false;
                int boundingBoxCount = 0;
                for (Field field : dripstoneBlockClass.getDeclaredFields()) {
                    if (boundingBoxCount >= 6) {
                        // Don't apply more than necessary
                        break;
                    }
                    if (Modifier.isStatic(field.getModifiers()) && field.getType().getSimpleName().equals("VoxelShape")) {
                        foundBoundingBoxes = true;
                        boundingBoxCount++;
                        applyNoBoundingBox(field);
                    } else if (foundBoundingBoxes) {
                        break;
                    }
                }
                plugin.getLogger().info("Dripstone collision hack enabled.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (turtleEggEnabled) {
            try {
                final Class<?> turtleEggBlockClass = NMSReflection.getNMSClass("world.level.block", "BlockTurtleEgg", "TurtleEggBlock");
                final Field singleEggShape = getStaticVoxelShapeField(turtleEggBlockClass, 0);
                final Field multipleEggsShape = getStaticVoxelShapeField(turtleEggBlockClass, 1);
                if (singleEggShape == null || multipleEggsShape == null) {
                    plugin.getLogger().warning("Could not find both turtle egg shapes - skipping the turtle egg fix.");
                } else {
                    applyNoBoundingBox(singleEggShape);
                    applyNoBoundingBox(multipleEggsShape);
                    plugin.getLogger().info("Turtle egg collision hack enabled.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (copperBarsEnabled) {
            try {
                applyCopperBarsFix(plugin);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Replaces copper bars' server collision with the Bedrock shape: each connected side runs its arm
     * only to the block centre (0.5) and drops the far post half, so a free end sits at 0.5 instead of
     * Java's 0.5625. That lets the Bedrock client walk its extra 0.0625 into the end without the server
     * yanking it back, while the bar still blocks like a bar (unlike a full zero-collision hack).
     *
     * Newer NMS stores the collision as a Function<BlockState, VoxelShape> (field "collisionShapes"),
     * so we swap in a function that returns the Bedrock shape for the state's N/E/S/W.
     */
    private void applyCopperBarsFix(Plugin plugin) throws Exception {
        final Class<?> shapesClass = NMSReflection.getNMSClass("world.phys.shapes", "VoxelShapes", "Shapes");
        final Class<?> crossCollisionBlockClass = NMSReflection.getMojmapNMSClass("world.level.block.CrossCollisionBlock");
        if (shapesClass == null || crossCollisionBlockClass == null) {
            plugin.getLogger().warning("Could not resolve Shapes/CrossCollisionBlock - skipping the copper bars fix.");
            return;
        }
        final Object emptyShape = createEmptyShape(shapesClass);

        // Precompute the 16 Bedrock shapes by N/E/S/W combination (bit0=N, 1=E, 2=S, 3=W).
        final Object[] shapeByCombo = new Object[16];
        for (int i = 0; i < 16; i++) {
            shapeByCombo[i] = buildBedrockBarsShape(shapesClass, i);
        }

        // NORTH/EAST/SOUTH/WEST BooleanProperty live on CrossCollisionBlock; read them off a state.
        final Object north = ReflectionAPI.getFieldAccessible(crossCollisionBlockClass, "NORTH").get(null);
        final Object east = ReflectionAPI.getFieldAccessible(crossCollisionBlockClass, "EAST").get(null);
        final Object south = ReflectionAPI.getFieldAccessible(crossCollisionBlockClass, "SOUTH").get(null);
        final Object west = ReflectionAPI.getFieldAccessible(crossCollisionBlockClass, "WEST").get(null);
        final Class<?> blockStateClass = NMSReflection.getMojmapNMSClass("world.level.block.state.BlockState");
        final Class<?> propertyClass = NMSReflection.getMojmapNMSClass("world.level.block.state.properties.Property");
        final Method getValue = ReflectionAPI.getMethod(blockStateClass, "getValue", propertyClass);

        final java.util.function.Function<Object, Object> bedrockShapeFn = state -> {
            try {
                int idx = 0;
                if ((Boolean) getValue.invoke(state, north)) idx |= 1;
                if ((Boolean) getValue.invoke(state, east)) idx |= 2;
                if ((Boolean) getValue.invoke(state, south)) idx |= 4;
                if ((Boolean) getValue.invoke(state, west)) idx |= 8;
                return shapeByCombo[idx];
            } catch (Exception e) {
                return emptyShape;
            }
        };

        final Field collisionFn = findFunctionField(crossCollisionBlockClass, "collisionShapes");
        if (collisionFn == null) {
            plugin.getLogger().warning("Could not find CrossCollisionBlock.collisionShapes - skipping the copper bars fix.");
            return;
        }
        collisionFn.setAccessible(true);

        final Class<?> registriesClass = NMSReflection.getMojmapNMSClass("core.registries.BuiltInRegistries");
        final Object blockRegistry = registriesClass.getField("BLOCK").get(null);
        final Method getKey = ReflectionAPI.getMethod(blockRegistry.getClass(), "getKey", Object.class);

        int patched = 0;
        for (Object block : (Iterable<?>) blockRegistry) {
            final Object key = getKey.invoke(blockRegistry, block);
            if (key == null || !key.toString().contains("copper_bars")) {
                continue;
            }
            ReflectionAPI.setFinalValue(block, collisionFn, bedrockShapeFn);
            plugin.getLogger().info("Copper bars collision -> Bedrock shape: " + key);
            patched++;
        }
        plugin.getLogger().info("Copper bars collision hack enabled (" + patched + " variants).");
    }

    /** Bedrock thin-bar shape: an arm from each connected edge to the centre, or the post if none. */
    private Object buildBedrockBarsShape(Class<?> shapesClass, int index) throws Exception {
        final boolean north = (index & 1) != 0;
        final boolean east = (index & 2) != 0;
        final boolean south = (index & 4) != 0;
        final boolean west = (index & 8) != 0;

        if (!north && !east && !south && !west) {
            return createVoxelShape(shapesClass, 7, 0, 7, 9, 16, 9); // centre post
        }
        Object shape = null;
        if (north) shape = orShape(shapesClass, shape, createVoxelShape(shapesClass, 7, 0, 0, 9, 16, 8));
        if (south) shape = orShape(shapesClass, shape, createVoxelShape(shapesClass, 7, 0, 8, 9, 16, 16));
        if (west)  shape = orShape(shapesClass, shape, createVoxelShape(shapesClass, 0, 0, 7, 8, 16, 9));
        if (east)  shape = orShape(shapesClass, shape, createVoxelShape(shapesClass, 8, 0, 7, 16, 16, 9));
        return shape;
    }

    private Object orShape(Class<?> shapesClass, Object a, Object b) throws Exception {
        if (a == null) {
            return b;
        }
        final Class<?> voxelShapeClass = NMSReflection.getNMSClass("world.phys.shapes", "VoxelShape");
        final Object varargs = Array.newInstance(voxelShapeClass, 1);
        Array.set(varargs, 0, b);
        final Method or = ReflectionAPI.getMethod(shapesClass, "or", voxelShapeClass, varargs.getClass());
        return or.invoke(null, a, varargs);
    }

    /** Builds a VoxelShape from pixel (0-16) coordinates via NMS Shapes. */
    private Object createVoxelShape(Class<?> shapesClass, double x1, double y1, double z1, double x2, double y2, double z2) throws Exception {
        Method box;
        try {
            box = ReflectionAPI.getMethod(shapesClass, "box",
                    double.class, double.class, double.class, double.class, double.class, double.class);
        } catch (NoSuchMethodException e) {
            box = ReflectionAPI.getMethod(shapesClass, "b",
                    double.class, double.class, double.class, double.class, double.class, double.class);
        }
        return box.invoke(null, x1 / 16D, y1 / 16D, z1 / 16D, x2 / 16D, y2 / 16D, z2 / 16D);
    }

    /** Function field, preferring the named one, searching up the class hierarchy. */
    private static Field findFunctionField(Class<?> clazz, String preferredName) {
        for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
            for (Field field : c.getDeclaredFields()) {
                if (field.getType() == java.util.function.Function.class && field.getName().equals(preferredName)) {
                    return field;
                }
            }
        }
        return null;
    }

    private Object createEmptyShape(Class<?> shapesClass) throws Exception {
        Method empty;
        try {
            empty = ReflectionAPI.getMethod(shapesClass, "empty");
        } catch (NoSuchMethodException e) {
            empty = ReflectionAPI.getMethod(shapesClass, "a");
        }
        return empty.invoke(null);
    }


    /**
     * Finds the nth static VoxelShape field of a class, counting in declaration order.
     */
    private static Field getStaticVoxelShapeField(final Class<?> clazz, final int index) {
        if (clazz == null) {
            return null;
        }
        int found = 0;
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && field.getType().getSimpleName().equals("VoxelShape")) {
                if (found++ == index) {
                    field.setAccessible(true);
                    return field;
                }
            }
        }
        return null;
    }

    /**
     * Because the "fixed" blocks have an empty bounding box, they can be placed inside players... prevent that to the best of
     * our ability.
     */
    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Block placed = event.getBlockPlaced();
        final Material material = placed.getType();
        if (this.bambooEnabled && material.equals(Material.BAMBOO)) {
            testIfCanBuild(event, this.originalBambooBoundingBox);
        } else if (this.pointedDripstoneEnabled && material.equals(Material.POINTED_DRIPSTONE)) {
            final PointedDripstone data = (PointedDripstone) placed.getBlockData();
            final BoundingBox boundingBox;
            switch (data.getThickness()) {
                case TIP:
                    boundingBox = data.getVerticalDirection() == BlockFace.DOWN ? tipDownDripstoneBox : tipUpDripstoneBox;
                    break;
                case TIP_MERGE:
                    boundingBox = tipMergeDripstoneBox;
                    break;
                case FRUSTUM:
                    boundingBox = frustumDripstoneBox;
                    break;
                case MIDDLE:
                    boundingBox = middleDripstoneBox;
                    break;
                case BASE:
                default:
                    boundingBox = baseDripstoneBox;
                    break;
            }
            testIfCanBuild(event, boundingBox);
        }
    }

    private void testIfCanBuild(final BlockPlaceEvent event, final BoundingBox box) {
        final BoundingBox currentBoundingBox = box.clone().shift(event.getBlockPlaced().getLocation());
        if (event.getPlayer().getBoundingBox().overlaps(currentBoundingBox)) {
            // Don't place this block as it intersects
            event.setBuild(false);
        }
    }

    /**
     * Emulates NMS Block#box
     */
    @Contract("_, _, _, _, _, _-> new")
    private BoundingBox box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return new BoundingBox(minX / 16D, minY / 16D, minZ / 16D, maxX / 16D, maxY / 16D, maxZ / 16D);
    }

    private static void applyNoBoundingBox(Field field) throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        final double x1 = 0, y1 = 0, z1 = 0, x2 = 0, y2 = 0, z2 = 0;
        if (field.getType().getSimpleName().equals("AxisAlignedBB")) {
            Class<?> boundingBoxClass = field.getType();
            Constructor<?> boundingBoxConstructor = boundingBoxClass.getConstructor(double.class, double.class, double.class,
                    double.class, double.class, double.class);
            Object boundingBox = boundingBoxConstructor.newInstance(x1, y1, z1, x2, y2, z2);
            ReflectionAPI.setFinalValue(field, boundingBox);
        } else if (field.getType().getSimpleName().equals("VoxelShape")) {
            Method createVoxelShape;
            try {
                // 1.18+ - obfuscated methods
                Class<?> shapesClass = NMSReflection.getNMSClass("world.phys.shapes", "VoxelShapes", "Shapes");
                createVoxelShape = ReflectionAPI.getMethod(shapesClass, "b",
                        double.class, double.class, double.class, double.class, double.class, double.class);
            } catch (NoSuchMethodException e) {
                Class<?> shapesClass = NMSReflection.getNMSClass("world.phys.shapes", "VoxelShapes", "Shapes");
                try {
                    createVoxelShape = ReflectionAPI.getMethod(shapesClass, "box",
                            double.class, double.class, double.class, double.class, double.class, double.class);
                } catch (NoSuchMethodException e2) {
                    createVoxelShape = ReflectionAPI.getMethod(shapesClass, "create",
                            double.class, double.class, double.class, double.class, double.class, double.class);
                }
            }
            Object boundingBox = ReflectionAPI.invokeMethod(createVoxelShape, x1, y1, z1, x2, y2, z2);
            ReflectionAPI.setFinalValue(field, boundingBox);
        } else {
            throw new IllegalStateException();
        }
    }
}
