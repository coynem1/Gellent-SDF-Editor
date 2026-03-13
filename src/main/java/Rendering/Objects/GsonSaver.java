package Rendering.Objects;

import Jade.Scene;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class GsonSaver {
    private final String VERSION = "Version: 0.1";

    private Scene currentScene;
    private Gson gson;

    public GsonSaver(Scene currentScene) {
        this.currentScene = currentScene;
        this.gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    }

    public void save() {
        String serialised = gson.toJson(currentScene.getObjects());

        IO.println(serialised);
    }

    public void load() {
        String serialised = gson.toJson(currentScene.getObjects());

        IO.println(serialised);
    }
}
