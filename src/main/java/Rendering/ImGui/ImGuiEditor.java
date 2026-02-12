package Rendering.ImGui;

import imgui.ImGui;

public class ImGuiEditor {
    private boolean showText = true;

    public ImGuiEditor() {
        ImGui.begin("Text Editor");
        ImGui.text("Hello, world!");
        ImGui.end();
    }

}
