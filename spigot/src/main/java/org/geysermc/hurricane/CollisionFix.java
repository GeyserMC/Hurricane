package org.geysermc.hurricane;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.*;

public final class CollisionFix implements Listener {
    private static final double EPSILON = 1.0E-6;

    /**
     * The collision boxes of pointed dripstone and sulfur spikes, in pixel units. Sulfur spikes (26.2+)
     * share their collision shapes with pointed dripstone, so clearing these fixes both blocks.
     */
    private static final double[][] SPELEOTHEM_SHAPES = {
            {5D, 0D, 5D, 11D, 16D, 11D}, // tip merge
            {5D, 0D, 5D, 11D, 11D, 11D}, // tip up
            {5D, 5D, 5D, 11D, 16D, 11D}, // tip down
            {4D, 0D, 4D, 12D, 16D, 12D}, // frustum
            {3D, 0D, 3D, 13D, 16D, 13D}, // middle
            {2D, 0D, 2D, 14D, 16D, 14D}, // base
    };

    private static final Material SULFUR_SPIKE = Material.matchMaterial("SULFUR_SPIKE");

    private final boolean bambooEnabled;
    private final BoundingBox originalBambooBoundingBox = box(6.5D, 0.0D, 6.5D, 9.5D, 16D, 9.5D);

    private final boolean pointedDripstoneEnabled;
    private final BoundingBox tipMergeDripstoneBox = box(5D, 0D, 5D, 11D, 16D, 11D);
    private final BoundingBox tipUpDripstoneBox = box(5D, 0D, 5D, 11D, 11D, 11D);
    private final BoundingBox tipDownDripstoneBox = box(5D, 5D, 5D, 11D, 16D, 11D);
    private final BoundingBox frustumDripstoneBox = box(4D, 0D, 4D, 12D, 16D, 12D);
    private final BoundingBox middleDripstoneBox = box(3D, 0D, 3D, 13D, 16D, 13D);
    private final BoundingBox baseDripstoneBox = box(2D, 0D, 2D, 14D, 16D, 14D);

    public CollisionFix(Plugin plugin, boolean bambooEnabled, boolean pointedDripstoneEnabled) {
        // Make any given block have zero collision. Lagback solved...!
        this.bambooEnabled = bambooEnabled;
        this.pointedDripstoneEnabled = pointedDripstoneEnabled;

        if (bambooEnabled) {
            applyBambooHack(plugin);
        }
        if (pointedDripstoneEnabled) {
            applySpeleothemHack(plugin);
        }
    }

    /**
     * Clears bamboo's collision. Field names change between Minecraft versions and mapping sets, so the
     * collision shape is matched by its bounds instead of relying on (obfuscated) field names.
     */
    private void applyBambooHack(final Plugin plugin) {
        final Class<?> bambooBlockClass = NMSReflection.getNMSClass("world.level.block", "BlockBamboo", "BambooStalkBlock");
        if (bambooBlockClass == null) {
            plugin.getLogger().warning("Could not find the bamboo block class - skipping the bamboo collision fix.");
            return;
        }

        Field shape = findShape(bambooBlockClass, "SHAPE_COLLISION", 6.5D, 0D, 6.5D, 9.5D, 16D, 9.5D);
        if (shape == null) {
            // Older versions have a single shape field (and no outline shapes to tell apart).
            Field onlyShape = null;
            int shapeFields = 0;
            for (final Field field : bambooBlockClass.getDeclaredFields()) {
                if (isShape(field)) {
                    shapeFields++;
                    onlyShape = field;
                }
            }
            if (shapeFields == 1) {
                shape = onlyShape;
            }
        }

        if (shape == null) {
            plugin.getLogger().warning("Could not find bamboo's collision shape - skipping the bamboo collision fix.");
        } else if (applyNoBoundingBox(shape)) {
            plugin.getLogger().info("Bamboo collision hack enabled.");
        } else {
            plugin.getLogger().warning("Could not clear bamboo's collision shape - skipping the bamboo collision fix.");
        }
    }

    /**
     * Clears the collision of pointed dripstone, which also clears sulfur spike collision on 26.2+ because
     * both blocks share their collision shapes there. Shapes are matched by their bounds, so unrelated
     * shapes (like pointed dripstone's drip check volume) are left alone.
     */
    private void applySpeleothemHack(final Plugin plugin) {
        final Class<?> speleothemBlockClass = NMSReflection.getNMSClass("world.level.block", "PointedDripstoneBlock", "SpeleothemBlock");
        if (speleothemBlockClass == null) {
            plugin.getLogger().warning("Could not find the pointed dripstone block class - skipping the pointed dripstone collision fix.");
            return;
        }

        int applied = 0;
        for (final Field field : speleothemBlockClass.getDeclaredFields()) {
            if (isShape(field) && isSpeleothemShape(field) && applyNoBoundingBox(field)) {
                applied++;
            }
        }
        if (applied < SPELEOTHEM_SHAPES.length) {
            // Fall back to the first six shape fields in a row, for shape implementations we cannot read back.
            boolean started = false;
            for (final Field field : speleothemBlockClass.getDeclaredFields()) {
                if (applied >= SPELEOTHEM_SHAPES.length) {
                    break;
                }
                if (isShape(field)) {
                    started = true;
                    if (applyNoBoundingBox(field)) {
                        applied++;
                    }
                } else if (started) {
                    break;
                }
            }
        }

        if (applied >= SPELEOTHEM_SHAPES.length) {
            plugin.getLogger().info("Dripstone collision hack enabled (also covers sulfur spikes on 26.2+).");
        } else if (applied > 0) {
            plugin.getLogger().warning("Could only clear " + applied + " of " + SPELEOTHEM_SHAPES.length + " dripstone collision shapes - the fix may be incomplete.");
        } else {
            plugin.getLogger().warning("Could not find pointed dripstone's collision shapes - skipping the pointed dripstone collision fix.");
        }
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
        } else if (this.pointedDripstoneEnabled && (material.equals(Material.POINTED_DRIPSTONE) || material.equals(SULFUR_SPIKE))) {
            final BoundingBox boundingBox = speleothemBoundingBox(placed);
            if (boundingBox != null) {
                testIfCanBuild(event, boundingBox);
            }
        }
    }

    /**
     * Reads the collision box of a placed pointed dripstone or sulfur spike from its block data.
     */
    private BoundingBox speleothemBoundingBox(final Block block) {
        try {
            final Object data = block.getBlockData();
            final Object thickness = data.getClass().getMethod("getThickness").invoke(data);
            final Object direction = data.getClass().getMethod("getVerticalDirection").invoke(data);
            switch (((Enum<?>) thickness).name()) {
                case "TIP":
                    return "DOWN".equals(((Enum<?>) direction).name()) ? tipDownDripstoneBox : tipUpDripstoneBox;
                case "TIP_MERGE":
                    return tipMergeDripstoneBox;
                case "FRUSTUM":
                    return frustumDripstoneBox;
                case "MIDDLE":
                    return middleDripstoneBox;
                case "BASE":
                default:
                    return baseDripstoneBox;
            }
        } catch (Throwable ignored) {
            return null;
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

    private static boolean isShape(final Field field) {
        if (!Modifier.isStatic(field.getModifiers())) {
            return false;
        }
        final String typeName = field.getType().getSimpleName();
        return typeName.equals("VoxelShape") || typeName.equals("AxisAlignedBB");
    }

    /**
     * Finds a static shape field by its (mojmap) name, or by matching the expected bounds in pixels.
     */
    private static Field findShape(final Class<?> clazz, final String preferredName, final double minX, final double minY,
                                   final double minZ, final double maxX, final double maxY, final double maxZ) {
        try {
            final Field preferred = clazz.getDeclaredField(preferredName);
            if (isShape(preferred) && matches(preferred, minX, minY, minZ, maxX, maxY, maxZ)) {
                preferred.setAccessible(true);
                return preferred;
            }
        } catch (NoSuchFieldException ignored) {
        }

        for (final Field field : clazz.getDeclaredFields()) {
            if (isShape(field) && matches(field, minX, minY, minZ, maxX, maxY, maxZ)) {
                field.setAccessible(true);
                return field;
            }
        }
        return null;
    }

    private static boolean isSpeleothemShape(final Field field) {
        final double[] bounds = shapeBounds(field);
        if (bounds == null) {
            return false;
        }
        for (final double[] shape : SPELEOTHEM_SHAPES) {
            if (matches(bounds, shape)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matches(final Field field, final double minX, final double minY, final double minZ,
                                   final double maxX, final double maxY, final double maxZ) {
        final double[] bounds = shapeBounds(field);
        return bounds != null && matches(bounds, minX, minY, minZ, maxX, maxY, maxZ);
    }

    private static boolean matches(final double[] bounds, final double[] pixels) {
        return matches(bounds, pixels[0], pixels[1], pixels[2], pixels[3], pixels[4], pixels[5]);
    }

    private static boolean matches(final double[] bounds, final double minX, final double minY, final double minZ,
                                   final double maxX, final double maxY, final double maxZ) {
        return Math.abs(bounds[0] - minX / 16D) < EPSILON && Math.abs(bounds[1] - minY / 16D) < EPSILON
                && Math.abs(bounds[2] - minZ / 16D) < EPSILON && Math.abs(bounds[3] - maxX / 16D) < EPSILON
                && Math.abs(bounds[4] - maxY / 16D) < EPSILON && Math.abs(bounds[5] - maxZ / 16D) < EPSILON;
    }

    /**
     * Reads the bounds (minX, minY, minZ, maxX, maxY, maxZ, in block units) of a static shape field.
     */
    private static double[] shapeBounds(final Field field) {
        final Object value;
        try {
            field.setAccessible(true);
            value = field.get(null);
        } catch (Throwable ignored) {
            return null;
        }
        if (value == null) {
            return null;
        }

        final String typeName = value.getClass().getSimpleName();
        if (typeName.equals("AxisAlignedBB") || typeName.equals("AABB")) {
            return readBounds(value);
        }

        final Method boundsMethod = findBoundsMethod(value.getClass());
        if (boundsMethod == null) {
            return null;
        }
        try {
            final Object aabb = boundsMethod.invoke(value);
            return aabb == null ? null : readBounds(aabb);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static Method findBoundsMethod(final Class<?> clazz) {
        Method fallback = null;
        for (Class<?> current = clazz; current != null && current != Object.class; current = current.getSuperclass()) {
            for (final Method method : current.getDeclaredMethods()) {
                if (method.getParameterCount() != 0) {
                    continue;
                }
                final String returnName = method.getReturnType().getSimpleName();
                if (!returnName.equals("AABB") && !returnName.equals("AxisAlignedBB")) {
                    continue;
                }
                method.setAccessible(true);
                if (method.getName().equals("bounds")) { // mojmap name
                    return method;
                }
                if (fallback == null && (method.getName().equals("a") || !method.getName().contains("$"))) {
                    fallback = method;
                }
            }
        }
        return fallback;
    }

    private static double[] readBounds(final Object box) {
        final String[] mojmapNames = {"minX", "minY", "minZ", "maxX", "maxY", "maxZ"};
        final String[] obfuscatedNames = {"a", "b", "c", "d", "e", "f"};
        final double[] bounds = new double[6];
        try {
            for (int i = 0; i < bounds.length; i++) {
                Field field = findField(box.getClass(), mojmapNames[i]);
                if (field == null) {
                    field = findField(box.getClass(), obfuscatedNames[i]);
                }
                if (field == null) {
                    return null;
                }
                field.setAccessible(true);
                bounds[i] = field.getDouble(box);
            }
        } catch (Throwable ignored) {
            return null;
        }
        return bounds;
    }

    private static Field findField(final Class<?> clazz, final String name) {
        for (Class<?> current = clazz; current != null && current != Object.class; current = current.getSuperclass()) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
            }
        }
        return null;
    }

    /**
     * Replaces the given static shape field with an empty shape, without ever throwing.
     *
     * @return whether the shape was cleared
     */
    private static boolean applyNoBoundingBox(final Field field) {
        try {
            final String typeName = field.getType().getSimpleName();
            if (typeName.equals("AxisAlignedBB")) {
                final Constructor<?> constructor = field.getType().getConstructor(double.class, double.class, double.class,
                        double.class, double.class, double.class);
                constructor.setAccessible(true);
                ReflectionAPI.setFinalValue(field, constructor.newInstance(0D, 0D, 0D, 0D, 0D, 0D));
                return true;
            }
            if (typeName.equals("VoxelShape")) {
                final Class<?> shapesClass = NMSReflection.getNMSClass("world.phys.shapes", "VoxelShapes", "Shapes");
                if (shapesClass == null) {
                    return false;
                }
                final Method boxMethod = findBoxMethod(shapesClass);
                if (boxMethod == null) {
                    return false;
                }
                ReflectionAPI.setFinalValue(field, boxMethod.invoke(null, 0D, 0D, 0D, 0D, 0D, 0D));
                return true;
            }
        } catch (Throwable ignored) {
            // The caller reports the failure - nothing to do here.
        }
        return false;
    }

    private static Method findBoxMethod(final Class<?> shapesClass) {
        for (final String name : new String[]{"box", "b", "create", "a"}) {
            try {
                final Method method = shapesClass.getDeclaredMethod(name, double.class, double.class, double.class,
                        double.class, double.class, double.class);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException ignored) {
            }
        }
        return null;
    }
}
