package Jade;

import Rendering.Renderer;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.BufferUtils;
import Rendering.Shader;
import util.Time;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;

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


public class RenderSDF extends Renderer {


    // Begin shader setup
    public RenderSDF(Path vertexShaderPath, Path fragmentShaderPath) {
        super();

        // this.indexBuffer = new int[]{
        //         0, 1, 2,    // Top Left
        //         2, 3, 1     // Bottom Right
        // };
        this.indexBuffer.put(0).put(1).put(2);  // Top Left
        this.indexBuffer.put(2).put(3).put(1);  // Bottom Right
        IO.println(indexBuffer.toString());
        // this.indexBuffer.flip();

        updateVertices();
        loadBuffers();  // VBO, VAO, EBO used for rendering
        setShaderFiles(vertexShaderPath, fragmentShaderPath);

        // Open shader files, compile and link them
        useShaders();
    }

    // Vertices fix to screen aspect ratio
    protected void updateVertices() {
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
    private void useShaders() {
        this.currentShader = new Shader();
        this.currentShader.init(this.vertexShaderPath, this.fragmentShaderPath);
        this.currentShader.compile();
        this.currentShader.run();
    }
}
