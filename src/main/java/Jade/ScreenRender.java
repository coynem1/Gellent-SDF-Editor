package Jade;

import org.joml.Vector2f;
import org.joml.Vector2i;
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
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL43C.GL_SHADER_STORAGE_BUFFER;


public class ScreenRender {
    private Shader currentShader;
    private String vertexShaderFilename = "assets/shaders/vertex.glsl";
    private String fragmentShaderFilename = "assets/shaders/fragment.glsl";
    private Camera camera;

    // TODO: Remove these for final version
    private int demoScene = 0;
    private float blend;
    private int toggleRender = 0;

    private float[] vertices = {};

    // Rectangle draw order
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


        // CPU side
//        int ssbo = glGenBuffers();
//        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
//        glBufferData(GL_SHADER_STORAGE_BUFFER, quadtreeData, GL_DYNAMIC_DRAW);
//        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, ssbo);
    }

    // Vertices fix to screen aspect ratio
    private void updateVertices() {
        float viewHeight = camera.getViewHeight();
        float viewWidth = camera.getViewWidth();

        vertices = new float[] {
                // Pos
                -viewWidth  / 2.0f, viewHeight  / 2.0f, 0.0f,   // Top Left
                viewWidth   / 2.0f, viewHeight  / 2.0f, 0.0f,   // Top Right
                -viewWidth  / 2.0f, -viewHeight / 2.0f, 0.0f,   // Bottom Left
                viewWidth   / 2.0f, -viewHeight / 2.0f, 0.0f    // Bottom Right
        };
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

        // Aspect ratio dependent, repositions screen vertices
        updateVertices();

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

        currentShader.run();
    }

    public void setDemoScene(int demoScene) {
        this.demoScene = demoScene;
    }
    public void setDemoBlend(float blend) {
        this.blend = blend;
    }
    public void setToggleRender(int val) {
        this.toggleRender = val;
    }

    // Renders every frame
    public void process(float delta) {
        // IO.println("Running at " + (1.0f / delta) + "FPS");

        // camera.setPosition(new Vector2f(camera.getPosition().x + delta * -50.0f, camera.getPosition().y + delta * -50.0f));
        // IO.println("Camera position: " + camera.getPosition());


        // Upload matrices for camera
        currentShader.uploadMat4("uProjection", camera.getProjectionMat());
        currentShader.uploadMat4("uView", camera.getViewMat());
        currentShader.uploadFloat("uTime", Time.getTime());
        currentShader.uploadInt("uDemoScene", demoScene);
        currentShader.uploadInt("uToggleRender", toggleRender);
        currentShader.uploadVec2i("uMouse", Window.get().toScreenSpace(MouseListener.getXY()));
        // IO.println(MouseListener.get().getXY().x);
//        IO.println("demoScene: " + Window.get().toScreenSpace(MouseListener.getXY()).x);


        glDrawElements(GL_TRIANGLES, screenBox.length, GL_UNSIGNED_INT, 0);
    }
}
