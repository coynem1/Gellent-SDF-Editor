package Jade;

import Demo.DemoScene;
import Input.Actions.ActionHandler;
import Input.InputStampShapes;
import Rendering.Objects.GsonSaver;

public class SceneManager {
    private static SceneManager instance;
    private Scene currentScene;
    private enum SceneMode {EDITING, PLAYING, DEBUGGING}
    private static int currentMode;   // Editing, Playing or Debugging, etc.
    private static String[] sceneModes;

    private GsonSaver gsonSaver;
    private ActionHandler actionHandler;

    public SceneManager() {
        sceneModes = new String[]{"Editing", "Playing", "Debugging"};
        currentMode = 0;

        // setScene(new SceneBase("World"));
    }

    public void init() {
        setScene(new DemoScene("DemoScene"));
        currentScene.start();

        // Input
        actionHandler = new ActionHandler(this);
        gsonSaver = new GsonSaver(currentScene);

        actionHandler.init();
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

    public void setScene(Scene scene) {
        if (scene == null) {return;}
        currentScene = scene;
    }

    public Scene getScene() {
        return get().currentScene;
    }
    public InputStampShapes getInputStamper() {return actionHandler.getInputStamper();}
    public ActionHandler getActionHandler() {return actionHandler;}
    public GsonSaver getGsonSaver() {return gsonSaver;}

    // Adds new scene to dict
    public Scene createScene(String name) {
        Scene scene = new SceneBase(name);
        setScene(scene);
        return scene;
    }
}
