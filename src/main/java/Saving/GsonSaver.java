package Saving;

import Input.InputSaving;
import Jade.Scene;
import Jade.SceneManager;
import Jade.Window;
import Rendering.Objects.Components.Component;
import Rendering.Objects.GameObject;
import Rendering.Objects.Shape;
import Saving.Deserialisers.DeserialiseComponents;
import Saving.Deserialisers.DeserialiseShapes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import util.GameClock;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class GsonSaver {
    public static final String VERSION = "0.1a";
    public static final String CONFIG_DIR = AppPaths.getConfigDir(Window.APP_NAME);

    private final Gson gson;
    private AppSettings settings;
    private File currentFile;

    public GsonSaver() {
        this.gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Component.class, new DeserialiseComponents())
            .registerTypeAdapter(Shape.class, new DeserialiseShapes())
            .create();
        this.settings = new AppSettings();

        // Create the config folder if it doesn't exist
        new java.io.File(CONFIG_DIR).mkdirs();
    }

    public void init() {
        settings.init();

        InputSaving.onOpened((filepath) ->{
            currentFile = new File(filepath);
            if (!currentFile.exists()) currentFile = null;
        });
    }

    public void save(boolean overwrite) {
        Scene currentScene = SceneManager.get().getScene();
        String serialised = gson.toJson(currentScene.getObjects());
        Path file = saveSceneFile(overwrite);

        // Save the scene
        if (file != null) {
            try {
                Files.writeString(file, serialised);
                InputSaving.setSaveCallback(currentFile.getName());
            } catch (Exception e) {
                IO.println("Failed to save scene data: " + e.getMessage());
            }
        }
    }

    // Opens a dialog to open a file
    public void load() {
        String extension = SaveDialog.GELLENT_FILE_EXTENSION;
        String pathStr;
        Path path;

        // Open a file
        pathStr = SaveDialog.showOpenDialog(SaveDialog.OPEN_SCENE_TITLE, extension, SaveDialog.GELLENT);
        if (pathStr == null) return;
        path = Path.of(pathStr);

        // File is not valid
        if (!isValidGellentFile(path)) return;

        currentFile = path.toFile();
        addRecentFiles(path);
        InputSaving.setOpenCallback(path.toString());
    }

    // Save the scene to a file
    private Path saveSceneFile(boolean overwrite) {
        String pathStr = null;
        Path path;

        // Path already saved
        if (overwrite && (currentFile != null && currentFile.exists())) {
            pathStr = currentFile.getAbsolutePath();
        } else {
            pathStr = SaveDialog.saveDialog(SaveDialog.SAVE_SCENE_TITLE, SaveDialog.GELLENT_FILE_EXTENSION, SaveDialog.GELLENT);
        }

        if (pathStr != null) {
            // user picked a location, now actually write your data to it
            path = Path.of(pathStr);
            settings.addRecentFiles(path);
            currentFile = path.toFile();
            return path;
        } else {
            // user hit cancel, do nothing
            return null;
        }
    }

    // Add a file to the recent files list
    public void addRecentFiles(Path path) { settings.addRecentFiles(path); }

    public File getCurrentFile() { return currentFile; }
    public Path getRecentFile(int index) { return settings.getRecentFile(index); }

    // Check if a file is a valid Gellent file
    public static boolean isValidGellentFile(Path path) {
        String pathStr = path.toString();

        if (!Files.exists(path)) return false;
        if (!pathStr.endsWith("." + SaveDialog.GELLENT_FILE_EXTENSION)) return false;

        return true;
    }
}
