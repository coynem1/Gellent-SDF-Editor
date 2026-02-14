package Jade;

import Input.InputKeyEvents;
import Input.InputMouseEvents;
import Rendering.ImGui.ImGuiWindow;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL32;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import util.Time;

import java.nio.IntBuffer;
import java.util.Objects;

import static java.sql.Types.NULL;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;

// Entire program, looping until closed
public class Window {
    private int width, height;
    private String title;
    private long glfwWindow;
    private ImGuiWindow imguiWindow;

    private static Window window;
    public static final String GLFW_VERSION = "#version 330";

    private Window() {
        // Default window size
        this.width = 1920;
        this.height = 1080;
        imguiWindow = new ImGuiWindow();

        this.title = "SDF Editor";
    }

    // Set shader version
    private void setGLFWVersion(String version) {
        String regex = "[,\\.\\s]"; // split by spaces
        String[] split;
        String major, minor;

        if (!version.contains("#version ") || version.length() < 12) {
            assert false : "GLFW version does not follow correct format";
        }
        split = version.split(regex);

        major = String.valueOf(split[1].charAt(0));
        minor = String.valueOf(split[1].charAt(1));

        try {
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, Integer.parseInt(major));
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, Integer.parseInt(minor));
            // IO.println("GLFW version set to " + major + "." + minor);
        }
        catch (NumberFormatException e) {
            assert false : "GLFW version is non-numeric: " + split[1];
        }
    }

    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }
        return Window.window;
    }

    public void run() {
        IO.println("Hello LWJGL " + Version.getVersion() + "!");

        // init(); // Run screen initializations
        initNew();
        loop();

        // loop(); // Refresh loop until windows closed

        destroy();
    }

    public void initNew() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!org.lwjgl.glfw.GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);

        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        glfwWindow = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);

        if (glfwWindow == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer pWidth = stack.mallocInt(1); // int*
            final IntBuffer pHeight = stack.mallocInt(1); // int*

            GLFW.glfwGetWindowSize(glfwWindow, pWidth, pHeight);
            final GLFWVidMode vidmode = Objects.requireNonNull(GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()));
            GLFW.glfwSetWindowPos(glfwWindow, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
        }

        GLFW.glfwMakeContextCurrent(glfwWindow);

        GL.createCapabilities();

        GLFW.glfwSwapInterval(GLFW.GLFW_TRUE);

        GLFW.glfwShowWindow(glfwWindow);

        imguiWindow.clearBuffer();
        renderBuffer();
    }

    // Render OpenGL buffer and poll events
    private void renderBuffer() {
        GLFW.glfwSwapBuffers(glfwWindow);
        GLFW.glfwPollEvents();
    }

    public void init() {
        // Error Callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Init GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Config GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Set GLFW version for shaders before windows created
        setGLFWVersion(GLFW_VERSION);

        // Creates Window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create the GLFW window");
        }

        // Bind inputs
        glfwSetCursorPosCallback(glfwWindow, InputMouseEvents::moveMouseCallback);  // Lambda bind
        glfwSetMouseButtonCallback(glfwWindow, InputMouseEvents::btnMouseCallback);
        glfwSetScrollCallback(glfwWindow, InputMouseEvents::scrollMouseCallback);
        glfwSetKeyCallback(glfwWindow, InputKeyEvents::keyCallback);


        // Make OpenGL current context
        glfwMakeContextCurrent(glfwWindow);

        // Enable VSync
        glfwSwapInterval(GLFW_TRUE);

        // Make window visible
        glfwShowWindow(glfwWindow);

        // Make OpenGL bindings available
        GL.createCapabilities();

        org.lwjgl.opengl.GLUtil.setupDebugMessageCallback();
        glEnable(org.lwjgl.opengl.GL43.GL_DEBUG_OUTPUT);
        glEnable(org.lwjgl.opengl.GL43.GL_DEBUG_OUTPUT_SYNCHRONOUS);

        // Update screen size variables automatically
        glfwSetWindowSizeCallback(glfwWindow, (_, newW, newH) -> {
            width = newW;
            height = newH;
        });

        // Attempt to set screen size at start, attempts fullscreen
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);

            glfwGetWindowSize(glfwWindow, w, h);
            width = w.get(0);
            height = h.get(0);
        }

        // Init ImGui
        imguiWindow.init();
    }

    public void loop() {
        float deltaTime = 0;
        Time.get().beginFrame();
        imguiWindow.init();

        while (!glfwWindowShouldClose(glfwWindow)) {
            // Poll Events
            glfwPollEvents();

            // Base colour
            glClearColor(0.05f, 0.05f, 0.1f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // // Send frame process to SceneManager
            // SceneManager.get().process(deltaTime);

            // ImGui
            imguiWindow.render();

            // Display
            renderBuffer();

            // Calculates elapsed frame time
            deltaTime = Time.get().endFrame();
            Time.get().beginFrame();


        }
    }

    // Free memory and terminate GLFW
    private void destroy() {
        // Destroy ImGui
        imguiWindow.destroy();

        // Free memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public long getWindow() {return glfwWindow;}
}
