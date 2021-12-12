package net.camotoy.bamboocollisionfix;

import org.bukkit.Bukkit;

// From ViaRewind Legacy Support
public class NMSReflection {
    private static String version;
    /**
     * Cheap hack to allow different fields.
     */
    public static boolean mojmap = true;

    public static String getVersion() {
        return version == null ? version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] : version;
    }

    public static Class<?> getNMSClass(String post1_16Prefix, String name) {
        try {
            return Class.forName("net.minecraft." + post1_16Prefix + "." + name);
        } catch (ClassNotFoundException e) {
            mojmap = false;
            try {
                return Class.forName("net.minecraft.server." + getVersion() + "." + name);
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
}
