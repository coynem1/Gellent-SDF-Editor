package Jade;

import Demo.DemoScene;
import Input.InputStampShapes;

public class SceneManager {
    private static SceneManager instance;
    private Scene currentScene;
    private enum SceneMode {EDITING, PLAYING, DEBUGGING}
    private static int currentMode;   // Editing, Playing or Debugging, etc.
    private static String[] sceneModes;

    public SceneManager() {
        sceneModes = new String[]{"Editing", "Playing", "Debugging"};
        currentMode = 0;
        setScene(new DemoScene("DemoScene"));
        currentScene.start();

        // Input
        InputStampShapes inputStamper = new InputStampShapes(currentScene);

        // setScene(new SceneBase("World"));
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

    // Adds new scene to dict
    public Scene createScene(String name) {
        Scene scene = new SceneBase(name);
        setScene(scene);
        return scene;
    }
}
