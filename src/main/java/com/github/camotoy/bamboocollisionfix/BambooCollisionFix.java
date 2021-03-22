package com.github.camotoy.bamboocollisionfix;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class BambooCollisionFix extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Plugin enabled. Modifying bamboo bounding box...");
        // Make the bamboo block have the same collision as a scaffolding block
        try {
            Class bambooBlockClass = NMSReflection.getNMSClass("BlockBamboo");
            Field bambooBoundingBox = ReflectionAPI.getFieldAccessible(bambooBlockClass, "c"); // Bounding box for "no leaves", according to Yarn.
            //setBoundingBox(bambooBoundingBox, 0, 0, 0, 1, 2D / 16D, 1);
            setBoundingBox(bambooBoundingBox, 0, 0, 0, 0, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("Done! Remember that this plugin modifies internals.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private static void setBoundingBox(Field field, double x1, double y1, double z1, double x2, double y2, double z2)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (field.getType().getSimpleName().equals("AxisAlignedBB")) {
            Class boundingBoxClass = field.getType();
            Constructor boundingBoxConstructor = boundingBoxClass.getConstructor(double.class, double.class, double.class,
                    double.class, double.class, double.class);
            Object boundingBox = boundingBoxConstructor.newInstance(x1, y1, z1, x2, y2, z2);
            ReflectionAPI.setFinalValue(field, boundingBox);
        } else if (field.getType().getSimpleName().equals("VoxelShape")) {
            Method createVoxelShape = ReflectionAPI.getMethod(NMSReflection.getNMSClass("VoxelShapes"), "create",
                    double.class, double.class, double.class, double.class, double.class, double.class);
            Object boundingBox = ReflectionAPI.invokeMethod(createVoxelShape, x1, y1, z1, x2, y2, z2);
            ReflectionAPI.setFinalValue(field, boundingBox);
        } else {
            throw new IllegalStateException();
        }
    }
}
