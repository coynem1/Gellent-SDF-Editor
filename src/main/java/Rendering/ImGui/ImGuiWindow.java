package Rendering.ImGui;

import Jade.Window;
import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;

public class ImGuiWindow {
    protected ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    protected ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    private String glslVersion = null;
    private long glfwWindow;


    public void ImGuiWindow() {
        this.glfwWindow = Window.get().getWindow();
        this.glslVersion = Window.GLFW_VERSION;
    }

    public void init() {
        imGuiGlfw.init(glfwWindow, true);
        imGuiGl3.init(glslVersion);
        ImGui.createContext();
    }

    public void newFrame() {
        imGuiGlfw.newFrame();
        ImGui.newFrame();
    }

    public void render() {
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());
    }

    private void endFrame() {
        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupCurrentContext = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(backupCurrentContext);
        }

        GLFW.glfwSwapBuffers(glfwWindow);
        GLFW.glfwPollEvents();
    }

    public void destroy() {
        imGuiGl3.shutdown();
        imGuiGlfw.shutdown();
        ImGui.destroyContext();
    }

}
