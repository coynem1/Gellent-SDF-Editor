package Saving;

import Input.Actions.ActionHandler;
import Input.InputSaving;
import Jade.Scene;
import Jade.SceneManager;
import Jade.Window;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class GsonSaver {
    public static final String VERSION = "0.1a";
    public static final String CONFIG_DIR = AppPaths.getConfigDir(Window.APP_NAME);

    private Scene currentScene;
    private final Gson gson;
    private AppSettings settings;
    private File currentFile;

    public GsonSaver(Scene currentScene) {
        this.currentScene = currentScene;
        this.gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
        this.settings = new AppSettings();

        // Create the config folder if it doesn't exist
        new java.io.File(CONFIG_DIR).mkdirs();
    }

    public void init() {
        settings.init();
    }

    public void save(boolean overwrite) {
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

    // TODO: Implement loading saved scenes
    public void load() {
        String extension = SaveDialog.GELLENT_FILE_EXTENSION;
        String pathStr;
        Path path;

        pathStr = SaveDialog.showOpenDialog(SaveDialog.OPEN_SCENE_TITLE, extension, SaveDialog.GELLENT);

        // File is valid
        if (pathStr != null && pathStr.endsWith("." + extension)) {
            path = Path.of(pathStr);
            currentFile = path.toFile();
            InputSaving.setSaveCallback(currentFile.getName());
        }
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
            settings.setCurrentFile(path);
            currentFile = path.toFile();
            return path;
        } else {
            // user hit cancel, do nothing
            return null;
        }
    }

    public File getCurrentFile() { return currentFile; }
}
