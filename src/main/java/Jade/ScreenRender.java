package Jade;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import util.Shader;
import util.Time;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;


public class ScreenRender {
    private Shader currentShader;
    private String vertexShaderFilename = "assets/shaders/vertex.glsl";
    private String fragmentShaderFilename = "assets/shaders/fragment.glsl";
    private Camera camera;
    private int demoScene = 0;

    private float[] vertices = {
            // Pos
            0.0f,   672.0f,   0.0f,   // Top Left
            1280.0f,   672.0f,   0.0f,   // Top Right
            0.0f,  0.0f,  0.0f,   // Bottom Left
            1280.0f,   0.0f,  0.0f,   // Bottom Right
    };

    private int[] screenBox = {
            0, 1, 2,    // Top Left
            2, 3, 1     // Bottom Right
    };

    private int vaoID, vboID, eboID;


    // Begin shader setup
    public ScreenRender() {
        // Open shader files, compile and link them
        useShaders(vertexShaderFilename, fragmentShaderFilename);

        // Camera declare
        this.camera = new Camera(new Vector2f());   // set to 0,0

        loadBuffers();  // VBO, VAO, EBO used for rendering
    }

    // Open shader files, compile, and link them
    private void useShaders(String vertexFilename, String fragFilename) {
        currentShader = new Shader(vertexFilename, fragFilename);
        currentShader.compile();
    }

    // Buffers for OpenGL
    private void loadBuffers() {
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();

        // Create VBO
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        // Create indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(screenBox.length);
        elementBuffer.put(screenBox).flip();

        // Create EBO
        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, screenBox, GL_STATIC_DRAW);

        // Position attribute
        int posSize = 3;
        glVertexAttribPointer(0, posSize, GL_FLOAT, false, posSize * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
    }

    public void setDemoScene(int demoScene) {
        this.demoScene = demoScene;
    }

    // Renders every frame
    public void process(float delta) {
        // IO.println("Running at " + (1.0f / delta) + "FPS");
        currentShader.run();
        // camera.setPosition(new Vector2f(camera.getPosition().x + delta * -50.0f, camera.getPosition().y + delta * -50.0f));
        // IO.println("Camera position: " + camera.getPosition());


        // Upload matrices for camera
        currentShader.uploadMat4("uProjection", camera.getProjectionMat());
        currentShader.uploadMat4("uView", camera.getViewMat());
        currentShader.uploadFloat("uTime", Time.getTime());
        currentShader.uploadInt("uDemoScene", demoScene);
        // IO.println("demoScene: " + demoScene);

        glDrawElements(GL_TRIANGLES, screenBox.length, GL_UNSIGNED_INT, 0);
    }
}
