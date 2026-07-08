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
        if (version == null) {
            String[] parts = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
            version = parts.length > 3 ? parts[3] : "";
        }
        return version;
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
        return getNMSClass(post1_16Prefix, name, name);
    }

    public static Class<?> getNMSClass(String post1_16Prefix, String name, String mojmapName) {
        // Try mojmap name first (Paper 1.20.5+/26.1+)
        Class<?> newNMSClass = getMojmapNMSClass(post1_16Prefix + "." + mojmapName);
        if (newNMSClass != null) {
            return newNMSClass;
        }

        // Try spigot-repackaged name (Paper 1.17-1.20.4)
        if (!mojmapName.equals(name)) {
            newNMSClass = getMojmapNMSClass(post1_16Prefix + "." + name);
            if (newNMSClass != null) {
                return newNMSClass;
            }
        }

        // Else Mojmap/post-1.17 is not in effect
        mojmap = false;
        String ver = getVersion();
        if (ver.isEmpty()) {
            return null;
        }
        try {
            return Class.forName("net.minecraft.server." + ver + "." + name);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}