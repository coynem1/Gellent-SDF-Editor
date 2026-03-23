package Saving;

public abstract class AppPaths {

    // Needed for saving app data
    public static String getConfigDir(String appName) {
        String os = System.getProperty("os.name").toLowerCase();
        String home = System.getProperty("user.home");

        // Paths depend on OS
        if (os.contains("win")) {
            // Windows
            return System.getenv("APPDATA") + "\\" + appName;   // C:\Users\<name>\AppData\Roaming\<app>

        } else if (os.contains("mac")) {
            // Mac
            return home + "/Library/Application Support/" + appName;

        } else {
            // Linux
            return home + "/.config/" + appName;
        }
    }
}
