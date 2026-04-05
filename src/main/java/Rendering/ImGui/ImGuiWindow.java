package Rendering.ImGui;

import Jade.Window;
import Observers.InputImGui;
import Observers.InputKeyEvents;
import imgui.*;
import imgui.app.Color;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

import static imgui.ImGui.getIO;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F1;
import static org.lwjgl.opengl.GL11.*;

public class ImGuiWindow {
    private static final int FONT_SIZE = 25;
    public static final int ICON_SIZE = 19;

    protected ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    protected ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    private String glslVersion = null;
    private long glfwWindow;

    private boolean windowClosing = false;
    private ImGuiEditor editor;
    private ImGuiWindowClose windowClose;
    private final Color colorBg = new Color(.5f, .5f, .5f, 1);  // TODO: Remove
    private boolean showHelp = false;


    public ImGuiWindow() {
        this.glslVersion = Window.GLFW_VERSION;
        this.editor = new ImGuiEditor();
        this.windowClose = new ImGuiWindowClose(this);
    }

    public void init() {
        this.glfwWindow = Window.get().getWindow();
        if (this.glfwWindow == 0L) {
            throw new IllegalStateException("GLFW window handle is 0. Did you create the window before ImGui start?");
        }

        if (this.glslVersion == null || this.glslVersion.isBlank()) {
            throw new IllegalStateException("glslVersion is not set. Expected something like \"#version 330\".");
        }
        if (this.editor == null) {
            throw new IllegalStateException("ImGuiEditor was not created (editor == null).");
        }

        // start
        ImGui.createContext();
        final ImGuiIO io = ImGui.getIO();
        io.setIniFilename(null);                                // Don't save .ini file
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);  // Enable Keyboard Controls
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);      // Enable Docking
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);    // Enable Multi-Viewport / Platform Windows
        io.setConfigViewportsNoTaskBarIcon(true);

        setupFont(io);

        imGuiGlfw.init(glfwWindow, true);
        imGuiGl3.init(glslVersion);

        this.editor.init();
        bindInputs();
    }

    private void bindInputs() {
        InputImGui.onHelpChanged((open) -> {
            showHelp = open;
            ImGuiHelp.open();
        });

        InputKeyEvents.onKeyPressed((key, _, _) -> {
            switch (key) {
                case GLFW_KEY_F1:
                    showHelp = true;
                    ImGuiHelp.open();
                    break;
            }
        });
    }

    // Creates font atlas and merges it with the default font
    private void setupFont(final ImGuiIO io) {
        final float DPI_STANDARD = 96f;
        final float DPI_SCALAR = (float) Toolkit.getDefaultToolkit().getScreenResolution() / DPI_STANDARD;
        final ImFontAtlas atlas = io.getFonts();    // Sprite sheet atlas
        final ImFontConfig baseConfig = new ImFontConfig(), iconConfig= new ImFontConfig(); // Character/icon types
        final ImFontGlyphRangesBuilder rangesBuilder = new ImFontGlyphRangesBuilder(); // Glyphs ranges provide
        final ImFont defaultFont;

        // Enable FreeType font renderer
        atlas.setFreeTypeRenderer(true);

        // Load new default font
        baseConfig.setMergeMode(false);
        baseConfig.setPixelSnapH(true);
        baseConfig.setGlyphRanges(atlas.getGlyphRangesDefault());

        defaultFont = atlas.addFontFromFileTTF("assets/fonts/calibri.ttf", (int) FONT_SIZE * DPI_SCALAR, baseConfig);
        baseConfig.destroy();

        // Add default font
        io.setFontDefault(defaultFont);

        // Icons ranges
        rangesBuilder.addRanges(FontAwesomeIcons._IconRange);
        final short[] glyphRanges = rangesBuilder.buildRanges();

        iconConfig.setMergeMode(true);  // Enable merge for icons with the default font
        iconConfig.setPixelSnapH(true);

        // Add icons and compile
        atlas.addFontFromFileTTF("assets/fonts/fa-regular-400.ttf", (int) ICON_SIZE * DPI_SCALAR, iconConfig, glyphRanges); // font awesome
        atlas.addFontFromFileTTF("assets/fonts/fa-solid-900.ttf", (int) ICON_SIZE * DPI_SCALAR, iconConfig, glyphRanges); // font awesome
        atlas.build();

        iconConfig.destroy();
    }

    // Render new frame
    public void render() {
        imGuiGl3.newFrame();
        imGuiGlfw.newFrame();
        ImGui.newFrame();

        // Render UI here
        editor.render();
        if (windowClosing) windowClose.close();
        if (showHelp) ImGuiHelp.draw();

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
    }

    // // Clear OpenGL buffer
    // public void clearBuffer() {
    //     glClearColor(colorBg.getRed(), colorBg.getGreen(), colorBg.getBlue(), colorBg.getAlpha());
    //     glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    // }

    public void destroy() {
        imGuiGl3.shutdown();
        imGuiGlfw.shutdown();
        ImGui.destroyContext();
    }

    public void setWindowClosing(boolean windowClosing) { this.windowClosing = windowClosing;}

}
