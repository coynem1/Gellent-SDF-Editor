package Observers;

import Input.InputShapes;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SettingsEvents {
    @FunctionalInterface
    public interface ArrayHandler {
        void handle(ArrayList<String> value);
    }

    private static ArrayList<String> recentFiles = new ArrayList<>();
    public static ArrayList<String> getRecentFiles() { return recentFiles; }

    // List of handlers
    private static final List<ArrayHandler> onRecentFileChanged = new CopyOnWriteArrayList<>();

    public static void onRecentFileChanged(ArrayHandler handler) { onRecentFileChanged.add(handler); }

    public static void setRecentFileCallback(ArrayList<String> recent) {
        for (var h : onRecentFileChanged) { h.handle(new ArrayList<>(recent)); }
        recentFiles = new ArrayList<>(recent);
    }

}
