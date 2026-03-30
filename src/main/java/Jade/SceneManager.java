package Jade;

import Demo.DemoScene;
import Input.Actions.ActionHandler;
import Input.Actions.ActionStamp;
import Input.InputSaving;
import Input.InputShapes;
import Rendering.Objects.SculptObject;
import Rendering.Objects.Shape;
import Saving.GsonSaver;
import Saving.TitleWindow;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import util.Transform2D;

import java.io.File;
import java.nio.file.Path;

public class SceneManager {
    private static SceneManager instance;
    private Scene currentScene;
    private enum SceneMode {EDITING, PLAYING, DEBUGGING}
    private static int currentMode;   // Editing, Playing or Debugging, etc.
    private static String[] sceneModes;

    private GsonSaver gsonSaver = null;
    private ActionHandler actionHandler = null;
    private TitleWindow titleWindow = null;

    public SceneManager() {
        sceneModes = new String[]{"Editing", "Playing", "Debugging"};
        currentMode = 0;
    }

    public void init() {
        // Input, Saving and Title
        actionHandler = new ActionHandler(this);
        gsonSaver = new GsonSaver();
        titleWindow = new TitleWindow();

        actionHandler.init();
        gsonSaver.init();

        // Notify only once that the scene has been opened
        Path path = gsonSaver.getRecentFile(0);
        loadScene(path);
        if (path != null) InputSaving.setOpenCallback(path.getFileName().toString());

        // Ensures that the scene is in the settings file
        InputSaving.onOpened((filePath) -> {
            loadScene(Path.of(filePath));
        });
    }

    public void newScene() {
        setScene(new SceneBase());
        blankScene();
        InputSaving.newFileCallback();
        currentScene.start();
    }

    // Load the most recent scene from settings, if not, load a blank scene
    private void loadScene(Path path) {
        setScene(new SceneBase());

        if (path != null) {
            currentScene.loadSceneFromFile(path);
        }
        else {
            blankScene();
        }

        currentScene.start();
    }

    // Add a single square to the blank scene
    private void blankScene() {
        Transform2D<Vector2f> transform = Transform2D.createFloat();
        transform.setPosition(new Vector2f(0,0));
        transform.setScale(10f);

        Shape square = new Shape(InputShapes.SHAPES.BOX, new SculptObject());
        square.setShapeMode(InputShapes.MODES.UNION);
        square.setTransform(transform);

        currentScene.addObjectToScene(square);
    }

    // Singleton
    public static SceneManager get() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    // Pass Frame process to the current scene
    public void process(float delta) {
        currentScene.process(delta);
    }

    public void setScene(@NotNull Scene scene) {
        currentScene = scene;
    }

    public Scene getScene() { return currentScene; }
    public Camera getCamera() { return currentScene.getCamera(); }
    public ActionHandler getActionHandler() {return actionHandler;}
    public GsonSaver getGsonSaver() {return gsonSaver;}
}
