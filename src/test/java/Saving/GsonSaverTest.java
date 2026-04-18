package Saving;

import Jade.Window;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class GsonSaverTest {

    @Test
    void checkConfigDirectoryExistsAfterCreation() {
        GsonSaver gsonSaver = new GsonSaver();
        String appName = Window.APP_NAME;

        assertTrue(Files.exists(Paths.get(AppPaths.getConfigDir(appName))), "Settings directory does not exist");
    }

    @Test
    void initWorks() {
        GsonSaver gsonSaver = new GsonSaver();

        try {
            gsonSaver.init();
        }
        catch (Exception e) {
            fail("init() threw an exception");
        }
    }

    @Test
    void checkIfFileIsAValidGellentFile() {
        Path file = Path.of("test." + SaveDialog.GELLENT_FILE_EXTENSION);
        assertFalse(GsonSaver.isValidGellentFile(Path.of("test.jpg")), "Test jpg was mistaken for a Gellent file");

        if (Files.exists(file)) assertTrue(GsonSaver.isValidGellentFile(file), "Test Gellent file was not recognised");
        else assertFalse(GsonSaver.isValidGellentFile(file), "Test Gellent file was mistaken to exist when it doesnt");
    }
}