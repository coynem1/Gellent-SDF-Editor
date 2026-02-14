package Rendering.ImGui;

import Jade.Window;
import imgui.ImFontAtlas;
import imgui.ImFontConfig;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.app.Color;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImInt;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GLCapabilities;

import static imgui.ImGui.getIO;

public class ImGuiWindow {
    protected ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    protected ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    private String glslVersion = null;
    private long glfwWindow;

    private ImGuiEditor editor;
    private final Color colorBg = new Color(.5f, .5f, .5f, 1);
    // private GLCapabilities glCapabilities;


    public ImGuiWindow() {
        // IO.println("ImGuiWindow created " + Window.GLFW_VERSION);
        this.glslVersion = Window.GLFW_VERSION;
        this.editor = new ImGuiEditor();
    }

    public void init() {
        this.glfwWindow = Window.get().getWindow();
        if (this.glfwWindow == 0L) {
            throw new IllegalStateException("GLFW window handle is 0. Did you create the window before ImGui init?");
        }

        // GLFW.glfwMakeContextCurrent(glfwWindow);

        if (this.glslVersion == null || this.glslVersion.isBlank()) {
            throw new IllegalStateException("glslVersion is not set. Expected something like \"#version 330\".");
        }
        if (this.editor == null) {
            throw new IllegalStateException("ImGuiEditor was not created (editor == null).");
        }
        // this.glCapabilities = GL.getCapabilities();


        // init
        ImGui.createContext();
        final ImGuiIO io = ImGui.getIO();
        io.setIniFilename(null);                                // We don't want to save .ini file
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);  // Enable Keyboard Controls
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);      // Enable Docking
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);    // Enable Multi-Viewport / Platform Windows
        io.setConfigViewportsNoTaskBarIcon(true);

        setupFont(io);



        imGuiGlfw.init(glfwWindow, true);
        imGuiGl3.init(glslVersion);

        // setupFont();



        // // Setup IO
        // final ImGuiIO io = ImGui.getIO();
        // io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
        // io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        // io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
    }

    private void setupFont(final ImGuiIO io) {
        // Sprite sheet atlas
        final ImFontAtlas fontAtlas = io.getFonts();
        final ImFontConfig fontConfig = new ImFontConfig(); // Natively allocated. Should be explicitly destroyed

        // Character type
        fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());

        // fontAtlas.addFontDefault();

        // Merge font
        fontConfig.setPixelSnapH(true);
        fontAtlas.addFontFromFileTTF("assets/fonts/calibri.ttf", 32, fontConfig);

        fontAtlas.build();
        fontConfig.destroy();
    }

    // Render new frame
    public void render() {
        // GL32.glClearColor(colorBg.getRed(), colorBg.getGreen(), colorBg.getBlue(), colorBg.getAlpha());
        // GL32.glClear(GL32.GL_COLOR_BUFFER_BIT | GL32.GL_DEPTH_BUFFER_BIT);

        imGuiGl3.newFrame();
        imGuiGlfw.newFrame();
        ImGui.newFrame();

        // Render UI here
        editor.render();

        endFrame();
    }

    // Finish rendering frame
    private void endFrame() {
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        if (getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupCurrentContext = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(backupCurrentContext);
        }

        // renderBuffer();
    }

    // Render OpenGL buffer and poll events
    private void renderBuffer() {
        GLFW.glfwSwapBuffers(glfwWindow);
        GLFW.glfwPollEvents();
    }

    // Clear OpenGL buffer
    public void clearBuffer() {
        GL32.glClearColor(colorBg.getRed(), colorBg.getGreen(), colorBg.getBlue(), colorBg.getAlpha());
        GL32.glClear(GL32.GL_COLOR_BUFFER_BIT | GL32.GL_DEPTH_BUFFER_BIT);
    }

    // // Render OpenGL buffer and poll events
    // public void renderBuffer() {
    //     GLFW.glfwSwapBuffers(glfwWindow);
    //     GLFW.glfwPollEvents();
    // }

    public void destroy() {
        imGuiGl3.shutdown();
        imGuiGlfw.shutdown();
        ImGui.destroyContext();
    }

}
