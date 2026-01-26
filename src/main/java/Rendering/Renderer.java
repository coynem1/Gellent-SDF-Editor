package Rendering;

import Jade.Camera;
import Jade.MouseListener;
import Jade.Window;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import util.Time;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

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
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public abstract class Renderer {
    protected int vertexArray;

    private Shader currentShader;
    private float[] vertices = {};

    protected Camera camera;

    // Vertex draw order
    protected int[] indexBuffer;

    protected int vaoID, vboID, eboID;

    // // Open shader files, compile, and link them
    // private void useShaders(String vertexFilename, String fragFilename) {
    //     currentShader = new Shader();
    //     currentShader.init(vertexFilename, fragFilename);
    //     currentShader.compile();
    // }


    protected abstract void init(); {}

    // Buffers for OpenGL
    protected void loadBuffers() {
        // Aspect ratio dependent, repositions screen vertices
        updateVertices();

        createVAO();
        createVBO();
        createEBO();

        // Position attribute
        int posSize = 3;
        glVertexAttribPointer(vertexArray, posSize, GL_FLOAT, false, posSize * Float.BYTES, 0);
        glEnableVertexAttribArray(vertexArray);

        // currentShader.run();
        run();
    }

    protected abstract void run(); {
        // currentShader = new Shader();
        // currentShader.init(vertexFilename, fragFilename);
        // currentShader.compile();
        // currentShader.run();
    }

    // Vertices fix to screen aspect ratio
    protected void updateVertices() {
        // Create float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();
    }

    protected void createVAO() {
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);
    }

    protected void createVBO() {
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
    }

    protected void createEBO() {
        // Create indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(indexBuffer.length);
        elementBuffer.put(indexBuffer).flip();
        
        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
    }


    // Renders every frame
    public void process(float delta) {
        glDrawElements(GL_TRIANGLES, indexBuffer.length, GL_UNSIGNED_INT, vertexArray);
    }
}

