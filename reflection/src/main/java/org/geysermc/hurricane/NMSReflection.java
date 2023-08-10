package org.geysermc.hurricane;

import org.bukkit.Bukkit;

// From ViaRewind Legacy Support
public final class NMSReflection {
    private static String version;
    /**
     * Cheap hack to allow different fields.
     */
    public static boolean mojmap = true;

    public static String getVersion() {
        return version == null ? version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] : version;
    }

    /**
     * 1.17+
     */
    public static Class<?> getMojmapNMSClass(String name) {
        try {
            return Class.forName("net.minecraft." + name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Class<?> getNMSClass(String post1_16Prefix, String name) {
        Class<?> newNMSClass = getMojmapNMSClass(post1_16Prefix + "." + name);
        if (newNMSClass != null) {
            return newNMSClass;
        }

        // Else Mojmap/post-1.17 is not in effect
        mojmap = false;
        try {
            return Class.forName("net.minecraft.server." + getVersion() + "." + name);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
