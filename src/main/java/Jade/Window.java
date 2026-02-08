package Jade;

import Input.InputKeyEvents;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import util.Time;

import java.awt.*;
import java.nio.IntBuffer;

import static java.lang.Math.abs;
import static java.sql.Types.NULL;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.glfw.GLFWWindowSizeCallback.get;

// Entire program, looping until closed
public class Window {
    private int width, height;
    private String title;
    private long glfwWindow;

    private static Window window;

    private Window() {
        // Default window size
        this.width = 1920;
        this.height = 1080;

        this.title = "SDF Editor";
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

        // Free memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
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

        // Creates Window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create the GLFW window");
        }

        // Bind inputs
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);  // Lambda bind
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::btnCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::scrollCallback);
        glfwSetKeyCallback(glfwWindow, InputKeyEvents::keyCallback);


        // Make OpenGL current context
        glfwMakeContextCurrent(glfwWindow);

        // Enable VSync
        glfwSwapInterval(1);

        // Make window visible
        glfwShowWindow(glfwWindow);

        // Make OpenGL bindings available
        GL.createCapabilities();

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
    }

    public void loop() {
        float deltaTime = 0;
        Time.get().beginFrame();

        while (!glfwWindowShouldClose(glfwWindow)) {
            // Poll Events
            glfwPollEvents();

            // Base colour
            glClearColor(0.05f, 0.05f, 0.1f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            // Send frame process to SceneManager
            SceneManager.get().process(deltaTime);

            // Display
            glfwSwapBuffers(glfwWindow);

            // Calculates elapsed frame time
            deltaTime = Time.get().endFrame();
            Time.get().beginFrame();
        }
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
}
