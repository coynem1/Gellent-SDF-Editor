package Jade;

import Input.InputHandler;

public class SceneManager {
    private static SceneManager instance;
    private Scene currentScene;
    private static int currentMode;   // Editing, Playing or Debugging, etc.
    private static String[] sceneModes;
    private InputHandler inputHandler;

    public SceneManager() {
        sceneModes = new String[]{"Editing", "Playing", "Debugging"};
        currentMode = 0;
        setScene(new DemoScene("DemoScene"));

        // setScene(new SceneBase("World"));
        this.inputHandler = InputHandler.get();
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
        Scene scene = new Scene(name);
        setScene(scene);
        return scene;
    }

    // Add function for deleting scene
}
