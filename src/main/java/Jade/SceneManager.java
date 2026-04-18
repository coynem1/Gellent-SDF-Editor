package Jade;

import Input.Actions.ActionHandler;
import Observers.InputSaving;
import Input.InputShapes;
import Rendering.Objects.SculptObject;
import Rendering.Objects.Shape;
import Saving.GsonSaver;
import Saving.TitleWindow;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import Rendering.Objects.Components.Transform2D;

import java.nio.file.Path;

public class SceneManager {
    private static SceneManager instance;
    private Scene currentScene;
    private enum SceneMode {EDITING, PLAYING, DEBUGGING}
    private static SceneMode currentMode = SceneMode.EDITING;

    private GsonSaver gsonSaver = null;
    private ActionHandler actionHandler = null;
    private TitleWindow titleWindow = null;
    private Path path = null;

    public SceneManager() {}

    public void init() {
        // Input, Saving and Title
        actionHandler = new ActionHandler(this);
        gsonSaver = new GsonSaver();
        titleWindow = new TitleWindow();

        actionHandler.init();
        gsonSaver.init();

        // Notify only once that the scene has been opened
        path = gsonSaver.getRecentFile(0);
        loadScene(path);
        if (path != null) InputSaving.setOpenCallback(path.toString());

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
    public void loadScene(Path path) {
        setScene(new SceneBase());
        SceneManager.get().getActionHandler().clear();

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
        transform.setScale(Transform2D.DEFAULT_SCALE);

        Shape square = new Shape(InputShapes.SHAPES.BOX, new SculptObject());
        square.setShapeMode(InputShapes.MODES.UNION);
        square.setTransform(transform);

        currentScene.addObjectToScene(square);
        path = null;
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
