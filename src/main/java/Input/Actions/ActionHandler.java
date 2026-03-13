package Input.Actions;

import Input.InputStampShapes;
import Jade.Scene;
import Jade.SceneManager;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public class ActionHandler {
    private boolean unsavedChanges = false;

    private InputStampShapes inputStamper;
    private Scene scene;
    private SceneManager sceneManager;

    // Double-ended queue
    private Deque<Action> undoStack = new ArrayDeque<Action>();
    private Deque<Action> redoStack = new ArrayDeque<Action>();

    public ActionHandler(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        this.inputStamper = new InputStampShapes(sceneManager);
    }

    public void init() {
        this.scene = sceneManager.getScene();
        this.inputStamper.init();
    }

    // Execute a new action and push it to the undo stack
    public void perform(Action action) {
        action.execute();
        undoStack.push(action);
        redoStack.clear();  // Redo history emptied
    }

    public void undo() {
        if (undoStack.isEmpty()) return;
        Action action = undoStack.pop();
        action.undo();
        redoStack.push(action);
    }

    public void redo() {
        if (redoStack.isEmpty()) return;
        Action action = redoStack.pop();
        action.execute();
        undoStack.push(action);
    }

    public Scene getScene() { return scene; }
    public InputStampShapes getInputStamper() { return inputStamper; }
}
