package Jade;

import Rendering.Renderer;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.BufferUtils;
import Rendering.Shader;
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
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;


public class RenderSDF extends Renderer {
    private final String vertexShaderFilename = "assets/shaders/vertexDemo.glsl";
    private final String fragmentShaderFilename = "assets/shaders/fragmentDemo.glsl";

    // Begin shader setup
    public RenderSDF() {
        super();

        this.indexBuffer = new int[]{
                0, 1, 2,    // Top Left
                2, 3, 1     // Bottom Right
        };

        updateVertices();
        loadBuffers();  // VBO, VAO, EBO used for rendering

        // Open shader files, compile and link them
        useShaders(vertexShaderFilename, fragmentShaderFilename);
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
    private void useShaders(String vertexFilename, String fragFilename) {
        this.currentShader = new Shader();
        this.currentShader.init(vertexFilename, fragFilename);
        this.currentShader.compile();
        this.currentShader.run();
    }
}
