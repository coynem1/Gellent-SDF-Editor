package Rendering.ImGui;

import Jade.SceneManager;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

public abstract class ImGuiHelp {
    private static final float WINDOW_WIDTH = 400f;
    private static final float DPI_SCALAR = ImGuiWindow.getDPIScalar();
    private static final ImBoolean open = new ImBoolean(false);

    public static void open() { open.set(true); }

    private static void shortcutRow(String action, String keys) {
        ImGui.text(action);
        ImGui.sameLine(WINDOW_WIDTH * 0.6f * DPI_SCALAR);
        ImGui.textDisabled(keys);
    }

    public static void draw() {
        if (!open.get()) return;

        ImGui.setNextWindowSizeConstraints(WINDOW_WIDTH * DPI_SCALAR, 0, Float.MAX_VALUE, Float.MAX_VALUE);
        if (ImGui.begin("Controls & Shortcuts  " + FontAwesomeIcons.Info, open)) {

            // Shape editing
            ImGui.separatorText("Shape Editing  " + FontAwesomeIcons.Shapes);
            shortcutRow("Stamp Shape",         "Left Click");
            shortcutRow("Add/Cut Shape",       "Backspace");
            shortcutRow("Move Shape",         "Left Click + Drag");
            shortcutRow("Rotate Shape",       "R + Drag");
            shortcutRow("Scale Shape",        "S + Drag");
            shortcutRow("Shape Blend",    "B + Drag");
            shortcutRow("Reset Blend",    "B");
            shortcutRow("Shape Round",       "F + Drag");
            shortcutRow("Reset Round",       "F");
            shortcutRow("Delete",       "Del");

            ImGui.spacing();

            // General
            ImGui.separatorText("Navigating  " + FontAwesomeIcons.MapMarked);
            shortcutRow("Zoom",         "Mouse Scroll");
            shortcutRow("Pan Camera",         "Middle Click + Drag");
            shortcutRow("Stamp/Shape Mode",         "Tab");
            shortcutRow("Switch Colours",          "Space");
            shortcutRow("Stamp Mode",        "Editor Shape Buttons");

            ImGui.spacing();

            // General
            ImGui.separatorText("General  " + FontAwesomeIcons.Keyboard);
            shortcutRow("Copy",         "Ctrl + C");
            shortcutRow("Cut",          "Ctrl + X");
            shortcutRow("Paste",        "Ctrl + V");
            shortcutRow("Undo",         "Ctrl + Z");
            shortcutRow("Redo",         "Ctrl + Y");
            shortcutRow("Save File",   "Ctrl + S");
            shortcutRow("Load File",   "Ctrl + O");
            shortcutRow("Help",   "F1");

        }
        ImGui.end();
    }
}
