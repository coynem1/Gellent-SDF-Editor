package Saving;

import Input.Actions.ActionHandler;
import Input.InputSaving;
import Jade.SceneManager;
import Jade.Window;
import org.jetbrains.annotations.NotNull;

public class TitleWindow {
    private static final String DELIMITER = " - ";
    private static final String UNSAVED = "*";

    private String title;
    private String fileName;
    private boolean unsavedChanges = false;


    public TitleWindow() {
        this.title = Window.APP_NAME;
        bindObservers();
    }

    private void bindObservers() {
        InputSaving.onSaved((file) -> {
            fileName = file;
            updateWindowTitle();
        });
        InputSaving.onActionChanged((unsaved) -> {
            unsavedChanges = unsaved;
            updateWindowTitle();
        });
    }


    // Update the window title to possibly include file name and unsaved changes
    public void updateWindowTitle() {
        this.title = Window.APP_NAME;

        if (fileName != null) this.title += DELIMITER + fileName;
        if (unsavedChanges) this.title += UNSAVED;

        Window.get().setTitle(this.title);
    }

}
