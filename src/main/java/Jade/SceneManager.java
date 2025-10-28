package Jade;

import java.util.Dictionary;
import java.util.Hashtable;

public class SceneManager {
    private static SceneManager instance;
    private Scene currentScene;
    private Dictionary<String, Scene> cachedScenes = new Hashtable<>();

    public SceneManager() {

    }

    // Singleton
    public static SceneManager get() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }


    public void setScene(Scene scene) {
        if (scene == null) {return;}
        currentScene = scene;
    }

    public Scene getScene() {
        return currentScene;
    }

    // Adds new scene to dict
    public Scene createScene(String name) {
        // Check if scene exists
        if (cachedScenes.get())

        return null;
    }
}
