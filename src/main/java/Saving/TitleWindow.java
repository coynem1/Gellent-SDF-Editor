package Saving;

import Observers.InputSavingEvents;
import Jade.Window;

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
        InputSavingEvents.onSaved((name) -> {
            fileName = name;
            unsavedChanges = false;
            updateWindowTitle();
        });
        InputSavingEvents.onNewFile(() -> {
            fileName = null;
            unsavedChanges = false;
            updateWindowTitle();
        });
        InputSavingEvents.onOpened((filePath) -> {
            fileName = Paths.get(filePath).getFileName().toString();
            unsavedChanges = false;
            updateWindowTitle();
        });
        InputSavingEvents.onActionChanged((unsaved) -> {
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
