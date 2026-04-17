package Rendering.ImGui;

import Input.Actions.ActionHandler;
import Jade.SceneManager;
import Jade.Window;
import Observers.InputImGui;
import Observers.SettingsEvents;
import Saving.AppSettings;
import Saving.GsonSaver;
import imgui.ImGui;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class ImGuiMenubar {
    private ActionHandler actionHandler;
    private GsonSaver gsonSaver;
    private SceneManager sceneManager;

    private ArrayList<String> recentFiles = new ArrayList<>();

    public void init() {
        bindObservers();
        sceneManager = SceneManager.get();

        actionHandler = sceneManager.getActionHandler();
        gsonSaver = sceneManager.getGsonSaver();
    }

    public void bindObservers() {
        SettingsEvents.onRecentFileChanged(recents -> {
            this.recentFiles = recents;
        });
        this.recentFiles = SettingsEvents.getRecentFiles();
    }

    private void printRecentFiles() {
        int limit = Math.min(recentFiles.size(), AppSettings.MAX_RECENT_FILES);

        for (int i = 0; i < limit; i++) {
            Path path = Paths.get(recentFiles.get(i));
            String name = path.getFileName().toString();

            if (name.isEmpty()) continue;

            if (ImGui.menuItem(name, null, false, true)) {
                gsonSaver.load(path);
            }
        }
    }

    public void render() {
        if (!ImGui.beginMainMenuBar()) {
            return;
        }
        boolean hasSelected = actionHandler.hasSelected();

        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("New", "Ctrl+N", false, true)) { sceneManager.newScene(); }
            if (ImGui.menuItem("Open", "Ctrl+O", false, true)) { gsonSaver.load(); }

            if (ImGui.beginMenu("Open Recent", recentFiles.size() > 0)) {
                printRecentFiles();
                ImGui.endMenu();
            }
            if (ImGui.menuItem("Save", "Ctrl+S", false, true)) { gsonSaver.save(true); }
            if (ImGui.menuItem("Save As...", null, false, true)) { gsonSaver.save(false); }
            if (ImGui.menuItem("Export as...", null, false, true)) { InputImGui.setImageExportCallback(true); }
            ImGui.separator();

            if (ImGui.menuItem("Quit", "Alt+F4", false, true)) { Window.get().destroy();}
            ImGui.endMenu();
        }

        if (ImGui.beginMenu("Edit")) {
            if (ImGui.menuItem("Copy", "Ctrl+C", false, hasSelected)) { actionHandler.copySelected(); }
            if (ImGui.menuItem("Paste", "Ctrl+V", false, hasSelected)) { actionHandler.pasteSelected(); }
            if (ImGui.menuItem("Cut", "Ctrl+X", false, hasSelected)) { actionHandler.cutSelected(); }
            if (ImGui.menuItem("Delete", "Delete", false, hasSelected)) { actionHandler.deleteSelected(); }
            ImGui.separator();
            if (ImGui.menuItem("Undo", "Ctrl+Z", false, true)) { actionHandler.undo(); }
            if (ImGui.menuItem("Redo", "Ctrl+Shift+Z", false, true)) { actionHandler.redo(); }
            ImGui.endMenu();
        }

        if (ImGui.beginMenu("Help")) {
            if (ImGui.menuItem("Controls", "F1", false, true)) { InputImGui.setHelpCallback(true); }
            ImGui.endMenu();
        }
        ImGui.endMainMenuBar();
    }

}
