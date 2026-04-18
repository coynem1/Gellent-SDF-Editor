package Observers;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InputSaving {
    @FunctionalInterface    // Single method interface
    public interface boolHandler {
        void handle(boolean index);
    }
    @FunctionalInterface    // Single method interface
    public interface stringHandler {
        void handle(String name);
    }
    @FunctionalInterface    // Single method interface
    public interface Handler {
        void handle();
    }

    // List of handlers
    private static final List<boolHandler> onActionChanged = new CopyOnWriteArrayList<>();
    private static final List<stringHandler> onSaved = new CopyOnWriteArrayList<>();
    private static final List<stringHandler> onOpened = new CopyOnWriteArrayList<>();
    private static final List<Handler> onNewFile = new CopyOnWriteArrayList<>();

    public static void onSaved(stringHandler handler) { onSaved.add(handler); }
    public static void onOpened(stringHandler handler) { onOpened.add(handler); }
    public static void onNewFile(Handler handler) { onNewFile.add(handler); }
    public static void onActionChanged(boolHandler handler) { onActionChanged.add(handler); }

    // Update all observers
    public static void setSaveCallback(String filename) { for (var h : onSaved) { h.handle(filename); }}
    public static void setOpenCallback(String filepath) { for (var h : onOpened) { h.handle(filepath); }}
    public static void newFileCallback() { for (var h : onNewFile) { h.handle(); }}
    public static void setActionCallback(boolean unsaved) { for (var h : onActionChanged) { h.handle(unsaved); }}
}
