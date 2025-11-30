package Jade;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import util.Time;

import static java.sql.Types.NULL;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;

public class Window {
    private int width, height;
    private String title;
    private long glfwWindow;

    private static Window window;

    private Window() {
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
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);


        // Make OpenGL current context
        glfwMakeContextCurrent(glfwWindow);

        // Enable VSync
        glfwSwapInterval(1);

        // Make window visible
        glfwShowWindow(glfwWindow);

        // Make OpenGL bindings available
        GL.createCapabilities();
    }

    public void loop() {
        float deltaTime = 0;
        Time.get().beginFrame();

        while (!glfwWindowShouldClose(glfwWindow)) {
            // Poll Events
            glfwPollEvents();

            glClearColor(0.05f, 0.05f, 0.1f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            // Send frame update to SceneManager
            SceneManager.get().process(deltaTime);

//            // Check inputs are working
//            if (KeyListener.isKeyPressed(GLFW_KEY_7)) {
//                IO.println("7");
//            }
//            if (MouseListener.isDragging()) {
//                IO.println("Dragging");
//            }
//            if (MouseListener.mouseBtnPress(GLFW_MOUSE_BUTTON_LEFT)) {
//                IO.println("Left Mouse");
//            }

            glfwSwapBuffers(glfwWindow);

            // Calculates elapsed frame time
            deltaTime = Time.get().endFrame();
            Time.get().beginFrame();
        }
    }
}
