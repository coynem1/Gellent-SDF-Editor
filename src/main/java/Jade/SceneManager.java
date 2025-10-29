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
        get().currentScene = scene;
    }

    public Scene getScene() {
        return get().currentScene;
    }

    // Adds new scene to dict
    public Scene createScene(String name) {
        // Check if scene exists
        if (get().cachedScenes.get(name) != null) {
            Scene scene = new EditorScene(name);
            get().cachedScenes.put(name, scene);
        }

        return null;
    }
}
