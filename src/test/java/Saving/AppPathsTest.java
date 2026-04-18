package Saving;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppPathsTest {
    @Test
    void getConfigDirFunctionReturnsCorrectPath() {
        String os = System.getProperty("os.name").toLowerCase();
        String home = System.getProperty("user.home");
        String appName = "TestApp";
        String testDir = AppPaths.getConfigDir(appName);
        String errorMessage = "App directory path is not the same as expected";

        // Paths depend on OS
        if (os.contains("win")) {
            // Windows
            assertEquals(System.getenv("APPDATA") + "\\" + appName, testDir, errorMessage);

        } else if (os.contains("mac")) {
            // Mac
            assertEquals(home + "/Library/Application Support/" + appName, testDir, errorMessage);

        } else {
            // Linux
            assertEquals(home + "/.config/" + appName, testDir, errorMessage);
        }

    }
}