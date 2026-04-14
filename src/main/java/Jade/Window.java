package Jade;

import Observers.InputKeyEvents;
import Observers.InputMouseEvents;
import Observers.WindowEvents;
import Rendering.ImGui.ImGuiWindow;
import Rendering.ImGui.ImGuiWindowClose;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import util.GameClock;
import util.Time;
import util.WindowIcon;

import java.nio.IntBuffer;

import static java.sql.Types.NULL;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

// Entire program, looping until closed
public class Window {
    public static final String APP_NAME = "Gellent";

    private int width, height;
    private String title;
    private long glfwWindow;
    private ImGuiWindow imguiWindow;
    private GameClock physicsClock = GameClock.get();
    private SceneManager sceneManager = SceneManager.get();
    private boolean awaitFrame = false;

    private static Window window;
    public static final String GLFW_VERSION = "#version 330";

    private Window() {
        // Default window size
        this.width = 1920;
        this.height = 1080;
        imguiWindow = new ImGuiWindow();

        this.title = APP_NAME;
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

        init(); // Run screen initializations
        loop(); // Refresh loop until windows closed

        destroy();
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

        // Make the window visible
        glfwShowWindow(glfwWindow);

        // Make OpenGL bindings available
        GL.createCapabilities();

        // org.lwjgl.opengl.GLUtil.setupDebugMessageCallback();
        // glEnable(org.lwjgl.opengl.GL43.GL_DEBUG_OUTPUT);
        // glEnable(org.lwjgl.opengl.GL43.GL_DEBUG_OUTPUT_SYNCHRONOUS);

        // Update screen size variables automatically
        glfwSetWindowSizeCallback(glfwWindow, (_, newW, newH) -> {
            width = newW;
            height = newH;
            WindowEvents.setScreenResizedCallback(newW, newH);
        });

        // Attempt to set screen size at the start, attempts fullscreen
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);

            glfwGetWindowSize(glfwWindow, w, h);
            width = w.get(0);
            height = h.get(0);
        }

        // Set the window icon
        WindowIcon.setWindowIcon(glfwWindow);

        // imguiWindow.clearBuffer();

        glfwSetWindowCloseCallback(glfwWindow, windowHandle -> {
            // Unsaved changes?
            if (sceneManager.getActionHandler().hasUnsavedChanges()) {
                // Prevent the window from closing immediately
                glfwSetWindowShouldClose(windowHandle, false);
                imguiWindow.setWindowClosing(true);
            }
        });

        renderBuffer();

    }

    public void loop() {
        float deltaTime = 0;
        // SceneManager sceneManager = SceneManager.get();
        sceneManager.init();

        Time.get().beginFrame();
        imguiWindow.init();
        physicsClock.startThread();

        while (!glfwWindowShouldClose(glfwWindow)) {
            // Poll Events
            glfwPollEvents();

            // Base colour
            glBindFramebuffer(GL_FRAMEBUFFER,0);
            glClearColor(0.05f, 0.05f, 0.1f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Send a frame process to SceneManager
            sceneManager.process(deltaTime);

            // If awaiting a frame render, send a signal before UI (E.g. Image export)
            if (awaitFrame) {
                glFinish(); // Block CPU until GPU is done
                WindowEvents.setFrameRenderedCallback(true);
                awaitFrame = false;
            }

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
    public void destroy() {
        // Destroy ImGui
        imguiWindow.destroy();
        physicsClock.stopThread();

        // Free memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW, stop the program and free the error callback
        glfwSetErrorCallback(null).free();
        glfwTerminate();
        System.exit(0);
    }

    // Sends signals to observers awaiting a frame render
    public void awaitNextFrame() { this.awaitFrame = true; }

    public int getWidth() { return width; }
    public int getHeight() {
        return height;
    }
    public long getWindow() {return glfwWindow;}

    public void setTitle(String title) {
        this.title = title;
        if (glfwWindow == NULL) return;
        glfwSetWindowTitle(glfwWindow, title);
    }
}
