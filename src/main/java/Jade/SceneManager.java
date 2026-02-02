package Jade;

import java.util.Dictionary;
import java.util.Hashtable;

public class SceneManager {
    private static SceneManager instance;
    private Scene currentScene;
    private static int currentMode;   // Editing, Playing or Debugging, etc.
    private static String[] sceneModes;

    public SceneManager() {
        sceneModes = new String[]{"Editing", "Playing", "Debugging"};
        currentMode = 0;
        setScene(new DemoScene("DemoScene"));

        // setScene(new SceneBase("World"));
    }

    // Singleton
    public static SceneManager get() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    // Pass Frame update to scene
    public void process(float delta) {
        get().currentScene.process(delta);
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
        Scene scene = new Scene(name);
        setScene(scene);
        return scene;
    }

    // Add function for deleting scene
}
