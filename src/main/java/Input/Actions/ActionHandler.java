package Input.Actions;

import Input.InputSaving;
import Input.InputShapes;
import Input.InputStampShapes;
import Jade.Scene;
import Jade.SceneManager;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public class ActionHandler {
    private int lastSavedAction = 0;

    // private InputStampShapes inputStamper;
    private InputShapes inputShapes;
    private Scene scene;
    private SceneManager sceneManager;

    // Double-ended queue
    private Deque<Action> undoStack = new ArrayDeque<Action>();
    private Deque<Action> redoStack = new ArrayDeque<Action>();

    public ActionHandler(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        this.inputShapes = new InputShapes(sceneManager);
        bindObservers();
    }

    // Unsaved changes update
    private void bindObservers() {
        InputSaving.onSaved((_) -> {
            lastSavedAction = undoStack.size();
            InputSaving.setActionCallback(false);
        });
    }

    public void init() {
        this.scene = sceneManager.getScene();
        this.inputShapes.init();
    }

    // Execute a new action and push it to the undo stack
    public void perform(Action action) {
        action.execute();
        undoStack.push(action);
        redoStack.clear();  // Redo history emptied
        InputSaving.setActionCallback(true);
    }

    public void undo() {
        if (undoStack.isEmpty()) return;
        Action action = undoStack.pop();
        action.undo();
        redoStack.push(action);
        InputSaving.setActionCallback(lastSavedAction != undoStack.size());
    }

    public void redo() {
        if (redoStack.isEmpty()) return;
        Action action = redoStack.pop();
        action.execute();
        undoStack.push(action);
        InputSaving.setActionCallback(lastSavedAction != undoStack.size());
    }

    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }

    // InputShapes
    public void deleteSelected() { inputShapes.deleteSelected(); }
    public void copySelected() { inputShapes.copySelected(); }
    public void cutSelected() { inputShapes.cutSelected(); }
    public void pasteSelected() { inputShapes.pasteObject(); }
    public boolean hasSelected() { return inputShapes.hasSelected(); }

    public Scene getScene() { return scene; }
    public InputShapes getInputShapes() { return inputShapes; }
}
