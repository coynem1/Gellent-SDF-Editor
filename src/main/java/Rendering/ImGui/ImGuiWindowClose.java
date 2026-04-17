package Rendering.ImGui;

import Jade.SceneManager;
import Jade.Window;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;

import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

public class ImGuiWindowClose {
    private static final float DPI_SCALAR = ImGuiWindow.getDPIScalar();
    private boolean showUnsavedChangesPopup = true;
    private long window;
    private ImGuiWindow imguiWindow;

    public ImGuiWindowClose(ImGuiWindow imguiWindow) {
        this.imguiWindow = imguiWindow;
    }

    public void close() {
        if (showUnsavedChangesPopup) {
            ImGui.openPopup("Unsaved Changes");
        }
        window = Window.get().getWindow();

        // Center the popup on the window (optional but nice)
        ImVec2 center = ImGui.getMainViewport().getCenter();
        ImGui.setNextWindowPos(center.x, center.y, ImGuiCond.Appearing, 0.5f, 0.5f);

        if (ImGui.beginPopupModal("Unsaved Changes", ImGuiWindowFlags.AlwaysAutoResize)) {
            ImGui.text("You have unsaved changes.");
            ImGui.text("Do you want to save before closing?");
            ImGui.separator();
            ImGui.spacing();

            // Highlights button
            ImGui.pushStyleColor(ImGuiCol.Button,0.18f, 0.38f, 0.78f, 1.0f);
            if (ImGui.button("Save and Close", 0, 0)) {
                SceneManager.get().getGsonSaver().save(true);

                // Check if actually saved, could've been cancelled
                if (!SceneManager.get().getActionHandler().hasUnsavedChanges()) {
                    glfwSetWindowShouldClose(window, true);
                }
                imguiWindow.setWindowClosing(false);
                ImGui.closeCurrentPopup();
            }
            ImGui.popStyleColor(1);
            ImGui.sameLine();

            // Grey buttons
            ImGui.pushStyleColor(ImGuiCol.Button,0.18f, 0.18f, 0.18f, 1.0f);
            if (ImGui.button("Don't Save", 130 * DPI_SCALAR, 0)) {
                glfwSetWindowShouldClose(window, true);
                ImGui.closeCurrentPopup();
            }

            ImGui.sameLine();

            if (ImGui.button("Cancel", 80 * DPI_SCALAR, 0)) {
                imguiWindow.setWindowClosing(false);
                glfwSetWindowShouldClose(window, false);
                showUnsavedChangesPopup = false;
                ImGui.closeCurrentPopup();
            }
            ImGui.popStyleColor(1);

            ImGui.endPopup();
        }
    }
}
