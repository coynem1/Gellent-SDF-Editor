package Rendering;

import Jade.Camera;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

// Parent to draw and store buffers
public abstract class Renderer {
    protected Path vertexShaderPath;
    protected Path fragmentShaderPath;

    protected Shader currentShader;
    protected int bufferCapacity = 1024;  // Default initial capacity
    protected int vaoID, vboID, eboID;

    protected final int OFFSET_EBO = 0;

    protected Camera camera;

    // Vertex draw order
    protected IntBuffer indexBuffer;
    protected FloatBuffer vertexBuffer;

    public Renderer() {
        this.indexBuffer = BufferUtils.createIntBuffer(bufferCapacity);
        this.vertexBuffer = BufferUtils.createFloatBuffer(bufferCapacity);
    }

    // Buffers for OpenGL
    protected void loadBuffers(boolean dynamic) {
        createVAO();
        createVBO(dynamic);
        createEBO(dynamic);

        // Vertex dimension size
        int posSize = 3;
        int attributeIndex = 0;
        glVertexAttribPointer(attributeIndex, posSize, GL_FLOAT, false, posSize * Float.BYTES, 0); // Zero for beginning of VBO
        glEnableVertexAttribArray(attributeIndex);
    }

    protected void createVAO() {
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);
    }

    protected void createVBO(boolean dynamic) {
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        vertexBuffer.flip();

        if (dynamic) {
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_DYNAMIC_DRAW);
        }
        else {
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        }

    }

    protected void createEBO(boolean dynamic) {
        // Create indices and upload to GPU
        indexBuffer.flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);

        if (dynamic) {
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_DYNAMIC_DRAW);
        }
        else {
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
        }
    }

    // Renders every frame
    public void process(float delta) {
        glBindVertexArray(vaoID);
        glDrawElements(GL_TRIANGLES, indexBuffer.limit(), GL_UNSIGNED_INT, OFFSET_EBO);
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

