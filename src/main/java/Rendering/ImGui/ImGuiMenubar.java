package Rendering.ImGui;

import Jade.Window;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;

public class ImGuiMenubar {
    private static String[] recentFiles = new String[10];

    public ImGuiMenubar() {}

    public void render() {
        if (ImGui.beginMainMenuBar()) {

            if (ImGui.beginMenu("File")) {
                ImGui.menuItem("New", "Ctrl+N", false, true);
                ImGui.menuItem("Open", "Ctrl+O", false, true);

                if (ImGui.beginMenu("Open Recent", false)) {
                    ImGui.menuItem("File 1", null, false, true);
                    ImGui.menuItem("File 2", null, false, true);
                    ImGui.endMenu();
                }
                ImGui.menuItem("Save", "Ctrl+S", false, true);
                ImGui.menuItem("Save As...", null, false, true);
                ImGui.separator();

                if (ImGui.menuItem("Quit", "Alt+F4", false, true)) { Window.get().destroy();};
                ImGui.endMenu();
            }

            if (ImGui.beginMenu("Edit")) {
                ImGui.menuItem("Copy", "Ctrl+C", false, false);
                ImGui.menuItem("Paste", "Ctrl+V", false, false);
                ImGui.menuItem("Cut", "Ctrl+X", false, false);
                ImGui.menuItem("Delete", "Delete", false, false);
                ImGui.separator();
                ImGui.menuItem("Undo", "Ctrl+Z", false, false);
                ImGui.menuItem("Redo", "Ctrl+Y", false, false);
                ImGui.endMenu();
            }

            ImGui.endMainMenuBar();
        }
    }

}
