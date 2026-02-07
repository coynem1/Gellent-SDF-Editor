package Jade;

import Input.InputEvents;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.lwjgl.glfw.GLFW.*;

public class KeyListener {
    @FunctionalInterface    // Single method interface
    public interface KeyHandler {
        void handle(int key, int scancode, int mods);
    }

    // List of key handlers
    private static final List<InputEvents.KeyHandler> onKeyPressed = new CopyOnWriteArrayList<>();
    private static final List<InputEvents.KeyHandler> onKeyReleased = new CopyOnWriteArrayList<>();

    private static KeyListener instance;
    private boolean keyPressed[] = new boolean[GLFW_KEY_LAST + 1];    // every number of supported keys

    private KeyListener() {}

    // Singleton
    public static KeyListener get() {
        if (KeyListener.instance == null) {
            KeyListener.instance = new KeyListener();
        }
        return KeyListener.instance;
    }

    // public static void keyCallback(long window, int key, int scancode, int action, int mods) {
    //     if (action == GLFW_PRESS) {
    //         get().keyPressed[key] = true;
    //     }
    //     else if (action == GLFW_RELEASE) {
    //         get().keyPressed[key] = false;
    //     }
    // }

    public static void onKeyPressed(InputEvents.KeyHandler handler) {
        onKeyPressed.add(handler);
    }

    public static void onKeyReleased(InputEvents.KeyHandler handler) {
        onKeyReleased.add(handler);
    }

    // Call this from your GLFW key callback
    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        boolean[] keyPressed = get().keyPressed;

        // Evoke if inputs pressed or released
        if (action == GLFW_PRESS) {
            for (var h : onKeyPressed) {
                // Already true?
                if (keyPressed[key]) { return; }

                h.handle(key, scancode, mods);
                keyPressed[key] = true;
            }
        } else if (action == GLFW_RELEASE) {
            for (var h : onKeyReleased) {
                // Already false?
                if (!keyPressed[key]) { return; }

                h.handle(key, scancode, mods);
                keyPressed[key] = false;
            }
        }
    }


    // Returns if certain keys pressed or not
    public static boolean isKeyPressed(int keyCode) {
        if (keyCode < get().keyPressed.length) {    // Valid?
            return get().keyPressed[keyCode];
        }
        IO.println("Unknown keycode entered");
        return false;
    }

}
