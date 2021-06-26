package com.github.camotoy.bamboocollisionfix;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BoundingBox;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class BambooCollisionFix extends JavaPlugin implements Listener {
    private final BoundingBox originalBambooBoundingBox = new BoundingBox(6.5D / 16D, 0.0D, 6.5D / 16.0D,
            9.5D / 16.0D, 1D, 9.5D / 16.0D);

    @Override
    public void onEnable() {
        getLogger().info("Plugin enabled. Modifying bamboo bounding box...");
        // Make the bamboo block have zero collision. Lagback solved...!
        try {
            Class<?> bambooBlockClass = NMSReflection.getNMSClass("world.level.block", "BlockBamboo");
            Field bambooBoundingBox = ReflectionAPI.getFieldAccessible(bambooBlockClass, NMSReflection.mojmap ? "f" : "c"); // Bounding box for "no leaves", according to Yarn.
            setBoundingBox(bambooBoundingBox, 0, 0, 0, 0, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("Done! Remember that this plugin modifies internals.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /**
     * Because the bamboo has an empty bounding box, it can be placed inside players... prevent that to the best of
     * our ability.
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getBlockData().getMaterial().equals(Material.BAMBOO)) {
            BoundingBox currentBambooBoundingBox = originalBambooBoundingBox.clone().shift(event.getBlockPlaced().getLocation());
            if (event.getPlayer().getBoundingBox().overlaps(currentBambooBoundingBox)) {
                // Don't place the bamboo as it intersects
                event.setBuild(false);
            }
        }
    }

    private static void setBoundingBox(Field field, double x1, double y1, double z1, double x2, double y2, double z2)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (field.getType().getSimpleName().equals("AxisAlignedBB")) {
            Class<?> boundingBoxClass = field.getType();
            Constructor<?> boundingBoxConstructor = boundingBoxClass.getConstructor(double.class, double.class, double.class,
                    double.class, double.class, double.class);
            Object boundingBox = boundingBoxConstructor.newInstance(x1, y1, z1, x2, y2, z2);
            ReflectionAPI.setFinalValue(field, boundingBox);
        } else if (field.getType().getSimpleName().equals("VoxelShape")) {
            Method createVoxelShape = ReflectionAPI.getMethod(NMSReflection.getNMSClass("world.phys.shapes", "VoxelShapes"), "create",
                    double.class, double.class, double.class, double.class, double.class, double.class);
            Object boundingBox = ReflectionAPI.invokeMethod(createVoxelShape, x1, y1, z1, x2, y2, z2);
            ReflectionAPI.setFinalValue(field, boundingBox);
        } else {
            throw new IllegalStateException();
        }
    }
}
