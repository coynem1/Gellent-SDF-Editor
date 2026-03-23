package Saving;

import Jade.Window;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Properties;

public class AppSettings {
    private static final String SETTINGS_PATH = GsonSaver.CONFIG_DIR + File.separator + "settings.properties";
    private static int MAX_RECENT_FILES = 5;

    private Properties properties = new Properties();

    public void init() {
        File file = new File(SETTINGS_PATH);
        defaultSettings();

        // Create settings if not there
        if (!file.exists()) {
            try {
                save(properties);
            } catch (IOException e) {
                IO.println("Unable to save settings: " + e.getMessage());
                return;
            }
        }

        // Load settings
        try {
            load();
        } catch (IOException e) {
            IO.println("Unable to load settings: " + e.getMessage());
        }

        // IO.println("Settings loaded "+ properties);
    }

    // Sets default settings
    private void defaultSettings() {
        properties.setProperty("Version", GsonSaver.VERSION);
    }

    private void save(@NotNull Properties props) throws IOException {
        // Write to file
        try (FileOutputStream out = new FileOutputStream(SETTINGS_PATH)) {
            props.store(out, Window.APP_NAME + " settings");
        }
    }

    // Load properties into the settings file
    private Properties load() throws IOException {
        properties = new Properties();
        File f = new File(SETTINGS_PATH);

        if (f.exists()) {
            try (FileInputStream in = new FileInputStream(SETTINGS_PATH)) {
                properties.load(in);
            }
        }
        return properties;
    }

    public void setCurrentFile(Path path) { properties.setProperty("currentFile", path.toString()); }
    public String getCurrentFile() { return properties.getProperty("currentFile"); }
}
