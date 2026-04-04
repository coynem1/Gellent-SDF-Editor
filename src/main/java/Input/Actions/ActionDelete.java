package Input.Actions;

import Jade.Scene;
import Jade.SceneManager;
import Rendering.Objects.Components.Blending;
import Rendering.Objects.GameObject;

public class ActionDelete implements Action {
    GameObject object;
    int index = -1;

    public ActionDelete(GameObject object) {
        this.object = object;
    }

    @Override public void execute() {
        Scene scene = SceneManager.get().getScene();
        this.index = scene.getObjects().indexOf(object);
        scene.removeObjectFromScene(object);
    }
    @Override public void undo() {
        if (index == -1) { return; }
        SceneManager.get().getScene().addObjectToScene(object, index);
    }
}
