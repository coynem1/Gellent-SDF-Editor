package Rendering.ImGui;

import Input.Actions.ActionHandler;
import Jade.SceneManager;
import Jade.Window;
import Saving.GsonSaver;
import imgui.ImGui;

public class ImGuiMenubar {
    private static String[] recentFiles = new String[10];
    private ActionHandler actionHandler;
    private GsonSaver gsonSaver;
    private SceneManager sceneManager;

    public ImGuiMenubar() {
        // actionHandler = SceneManager.get().getActionHandler();
    }

    public void init() {
        sceneManager = SceneManager.get();

        actionHandler = sceneManager.getActionHandler();
        gsonSaver = sceneManager.getGsonSaver();
    }

    public void render() {
        if (!ImGui.beginMainMenuBar()) {
            return;
        }

        boolean hasSelected = actionHandler.hasSelected();

        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("New", "Ctrl+N", false, true)) { sceneManager.newScene(); }
            if (ImGui.menuItem("Open", "Ctrl+O", false, true)) { gsonSaver.load(); }

            if (ImGui.beginMenu("Open Recent", false)) {
                ImGui.menuItem("File 1", null, false, true);
                ImGui.menuItem("File 2", null, false, true);
                ImGui.endMenu();
            }
            if (ImGui.menuItem("Save", "Ctrl+S", false, true)) { gsonSaver.save(true); }
            if (ImGui.menuItem("Save As...", null, false, true)) { gsonSaver.save(false); }
            ImGui.separator();

            if (ImGui.menuItem("Quit", "Alt+F4", false, true)) { Window.get().destroy();}
            ImGui.endMenu();
        }

        if (ImGui.beginMenu("Edit")) {
            ImGui.menuItem("Copy", "Ctrl+C", false, hasSelected);
            ImGui.menuItem("Paste", "Ctrl+V", false, hasSelected);
            ImGui.menuItem("Cut", "Ctrl+X", false, hasSelected);
            if (ImGui.menuItem("Delete", "Delete", false, hasSelected)) { actionHandler.deleteSelected(); }
            ImGui.separator();
            if (ImGui.menuItem("Undo", "Ctrl+Z", false, true)) { actionHandler.undo(); }
            if (ImGui.menuItem("Redo", "Ctrl+Shift+Z", false, true)) { actionHandler.redo(); }
            ImGui.endMenu();
        }

        ImGui.endMainMenuBar();
    }

}
