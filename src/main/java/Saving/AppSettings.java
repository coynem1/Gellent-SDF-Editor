package Saving;

import Jade.Window;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class AppSettings {
    private static final String RECENT_FILES = "RecentFiles";
    private static final String VERSION = "Version";
    private static final String SETTINGS_PATH = GsonSaver.CONFIG_DIR + File.separator + "settings.properties";
    private static int MAX_RECENT_FILES = 5;

    private Properties properties = new Properties();
    private Properties defaultProperties = new Properties();
    private ArrayList<String> recentFiles = new ArrayList<>();

    public void init() {
        boolean updateSettings = false;
        File file = new File(SETTINGS_PATH);
        defaultSettings();

        // Create settings if not there
        if (!file.exists()) {
            try {
                save(defaultProperties);
                return;
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

        // Check if default properties are there
        for (var key : defaultProperties.keySet()) {
            if (!properties.containsKey(key)) {
                properties.setProperty(key.toString(), defaultProperties.getProperty(key.toString()));
                updateSettings = true;
            }
        }
        try {
            if (updateSettings) save(properties);
        } catch (IOException e) {
            IO.println("Unable to save settings: " + e.getMessage());
        }
    }

    // Sets default settings
    private void defaultSettings() {
        defaultProperties.setProperty(VERSION, GsonSaver.VERSION);
        defaultProperties.setProperty(RECENT_FILES, "");
    }

    // Save properties to the settings file
    private void save(@NotNull Properties props) throws IOException {
        // Write to file
        try (FileOutputStream out = new FileOutputStream(SETTINGS_PATH)) {
            props.store(out, Window.APP_NAME + " settings");
        }
    }

    // Load properties into the settings file
    private void load() throws IOException {
        File f = new File(SETTINGS_PATH);
        if (!f.exists()) return;

        try (FileInputStream in = new FileInputStream(SETTINGS_PATH)) {
            properties.load(in);
        }
    }

    // Add a file to the recent files list
    public void addRecentFiles(Path path) {
        boolean exists = false;

        // Get recent files
        String value = properties.getProperty(RECENT_FILES, "");
        recentFiles = value.isEmpty() ? new ArrayList<String>() : new ArrayList<String>(Arrays.asList(value.split(",")));

        // Check if the path is already in array
        for (int i = 0; i < recentFiles.size(); i++) {
            // Move to the front
            if (recentFiles.get(i).equals(path.toString())) {
                recentFiles.set(i, recentFiles.getFirst());
                recentFiles.set(0, path.toString());
                exists = true;
                break;
            }
        }
        if (!exists) recentFiles.add(0, path.toString());

        properties.setProperty(RECENT_FILES, String.join(",", recentFiles));

        try {
            save(properties);
        } catch (IOException e) {
            IO.println("Unable to save settings: " + e.getMessage());
        }
    }

    // Get recent files and update if empty
    public ArrayList<String> getRecentFiles() {
        if (!recentFiles.isEmpty()) return recentFiles;

        // Get recent files
        String value = properties.getProperty(RECENT_FILES, "");
        recentFiles = value.isEmpty() ? new ArrayList<String>() : new ArrayList<String>(Arrays.asList(value.split(",")));

        return recentFiles;
    }

    public Path getRecentFile(int index) {
        Path path = null;
        ArrayList<String> recentFiles = getRecentFiles();

        if (recentFiles.isEmpty()) return null;

        // Check if path is valid
        path = Path.of(recentFiles.get(index));
        if (GsonSaver.isValidGellentFile(path)) return path;
        else {
            IO.println("Invalid/missing save file: " + path.toString());
            return null;
        }
    }
}
