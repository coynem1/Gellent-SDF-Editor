package Saving;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.nio.ByteBuffer;

public class SaveDialog {
    public static final String GELLENT_FILE_EXTENSION = "glnt";
    public static final String GELLENT = "Gellent";
    public static final String DEFAULT_FILE_NAME = "untitled";
    public static final String SAVE_SCENE_TITLE = "Save Scene";


    // Show a popup for the user to choose file-name and location
    public static String saveDialog(String title, String extension, String fileDescription) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            org.lwjgl.PointerBuffer filters = stack.mallocPointer(1);
            filters.put(0, stack.UTF8("*." + extension));

            String result = TinyFileDialogs.tinyfd_saveFileDialog(
                    title,  // dialog title
                    DEFAULT_FILE_NAME,  // default filename
                    filters,    // filter pattern
                    fileDescription + " Files"  // file description
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



    public static String showOpenDialog(String extension, String description) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            org.lwjgl.PointerBuffer filters = stack.mallocPointer(1);
            filters.put(0, stack.UTF8("*." + extension));

            return TinyFileDialogs.tinyfd_openFileDialog(
                    "Open File",
                    "",
                    filters,
                    description,
                    false  // false = single file, true = multi select
            );
        }
    }

}

