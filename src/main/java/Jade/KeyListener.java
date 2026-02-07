package Jade;

import Input.InputKeyEvents;

public class KeyListener extends InputKeyEvents {
    private static KeyListener instance;

    private KeyListener() {}

    // Singleton
    public static KeyListener get() {
        if (KeyListener.instance == null) {
            KeyListener.instance = new KeyListener();
        }
        return KeyListener.instance;
    }

}
