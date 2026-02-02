package Rendering;

import Jade.Camera;
import Jade.MouseListener;
import Jade.Window;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import util.Time;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

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
    protected Path vertexShaderPath;
    protected Path fragmentShaderPath;

    protected Shader currentShader;
    protected int vertexArray;
    protected float[] vertices = {};
    protected int vaoID, vboID, eboID;
    protected int indexBufferCapacity = 6;  // Default initial capacity

    protected Camera camera;

    // Vertex draw order
    protected IntBuffer indexBuffer;
    // protected ArrayList<Integer> indexBuffer;

    public Renderer() {
        // Camera declare
        this.camera = new Camera(new Vector2f());   // set to 0,0
        this.indexBuffer = BufferUtils.createIntBuffer(indexBufferCapacity);
    }

    // Buffers for OpenGL
    protected void loadBuffers() {
        createVAO();
        createVBO();
        createEBO();

        // Vertex dimension size
        int posSize = 3;
        glVertexAttribPointer(vertexArray, posSize, GL_FLOAT, false, posSize * Float.BYTES, 0);
        glEnableVertexAttribArray(vertexArray);
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

    public Camera getCamera() {
        return this.camera;
    }

    public Shader getShader() {
        return currentShader;
    }

    public void setShaderFiles(Path vertexShaderPath, Path fragmentShaderPath) {
        if (!(Files.exists(vertexShaderPath) && Files.exists(fragmentShaderPath))) {
            throw new FileSystemNotFoundException("Vertex shader and/or fragment shader files do not exist");
        }
        this.vertexShaderPath = vertexShaderPath;
        this.fragmentShaderPath = fragmentShaderPath;
    }

}

