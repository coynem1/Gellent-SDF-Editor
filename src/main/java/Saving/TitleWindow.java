package Saving;

import Input.InputSaving;
import Jade.Window;

import java.nio.file.Path;
import java.nio.file.Paths;

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
        InputSaving.onSaved((name) -> {
            fileName = name;
            unsavedChanges = false;
            updateWindowTitle();
        });
        InputSaving.onNewFile(() -> {
            fileName = null;
            unsavedChanges = false;
            updateWindowTitle();
        });
        InputSaving.onOpened((filePath) -> {
            fileName = Paths.get(filePath).getFileName().toString();
            unsavedChanges = false;
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
