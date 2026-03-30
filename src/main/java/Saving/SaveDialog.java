package Saving;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public class SaveDialog {
    public static final String GELLENT_FILE_EXTENSION = "glnt";
    public static final String GELLENT = "Gellent";
    public static final String DEFAULT_FILE_NAME = "untitled";
    public static final String SAVE_SCENE_TITLE = "Save Scene";
    public static final String OPEN_SCENE_TITLE = "Open Scene";


    // Show a popup for the user to choose file-name and location
    public static String saveDialog(String title, String extension, String description) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            org.lwjgl.PointerBuffer filters = stack.mallocPointer(1);
            filters.put(0, stack.UTF8("*." + extension));

            String result = TinyFileDialogs.tinyfd_saveFileDialog(
                    title,  // dialog title
                    DEFAULT_FILE_NAME,  // default filename
                    filters,    // filter pattern
                    description + " Files"  // file description
            );

            // Cancelled
            if (result == null) return null;

            // Strip whatever extension the user typed and force yours
            if (result.contains(".")) {
                result = result.substring(0, result.lastIndexOf("."));
            }
            return result + "." + extension;
        }
    }

    // Show a popup for the user to choose a file
    public static String showOpenDialog(String title, String extension, String description) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            org.lwjgl.PointerBuffer filters = stack.mallocPointer(1);
            filters.put(0, stack.UTF8("*." + extension));

            return TinyFileDialogs.tinyfd_openFileDialog(
                    title,
                    "",
                    filters,
                    description,
                    false   // multi select
            );
        }
    }

}

