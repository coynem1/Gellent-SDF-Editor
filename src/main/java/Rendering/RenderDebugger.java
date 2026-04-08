package Rendering;

import Jade.Camera;
import Rendering.Shaders.Shader;
import org.lwjgl.BufferUtils;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.IntStream;

import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL14.glMultiDrawArrays;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

// Displays an overlay for visualising scene elements
public class RenderDebugger extends Renderer {

    public RenderDebugger(InputStream vertexShaderPath, InputStream fragmentShaderPath, Camera camera) {
        super();

        this.camera = camera;

        // Larger initial capacity for drawing to reduce reallocation time
        this.bufferCapacity = 1024;
        this.indexBuffer = BufferUtils.createIntBuffer(this.bufferCapacity);

        setShaderFiles(vertexShaderPath, fragmentShaderPath);
        useShaders();   // Open shader files, compile and link them
    }

    // Initialises the buffers to draw
    public void render() {
        if (vertexBuffer.position() == 0) {
            throw new IllegalStateException("Buffers must be loaded before drawing. Create shapes first before loading.");
        }

        loadBuffers(true);  // VBO, VAO, EBO used for rendering
    }

    // Open shader files, compile, and link them
    private void useShaders() {
        this.currentShader = new Shader();
        this.currentShader.init(this.vertexShaderPath, this.fragmentShaderPath);
        this.currentShader.compile();
        this.currentShader.run();
    }

    // Draws every rectangle as an outline
    public void drawRects() {
        final int SIZE = 4; // Corners of a rectangle
        final int OFFSET = 0;
        int[] count = new int[vertexBuffer.limit() / SIZE];
        int[] first = new int[vertexBuffer.limit() / SIZE];

        // Fill arrays for multi-draw
        Arrays.fill(count, SIZE);
        IntStream.range(OFFSET, OFFSET + 2).forEach(i -> first[i] = i * SIZE);    // Map array to increments

        glMultiDrawArrays(GL_LINE_LOOP, first, count);
    }


    public void createRect(float x, float y, float width, float height) {
        // Add vertices to the buffer
        vertexBuffer.put(x).put(y).put(0);                        // Top left
        vertexBuffer.put(x + width).put(y).put(0);             // Top right
        vertexBuffer.put(x + width).put(y + height).put(0); // Bottom left
        vertexBuffer.put(x).put(y + height).put(0);            // Bottom right
    }

    @Override
    public void process(float delta) {
        glBindVertexArray(vaoID);
        drawRects();
    }

    // private final int CIRCLE_SEGMENTS = 32;
    //// Draws every circle as an outline
    // public void drawCircles() {
    //     final int OFFSET = 0;
    //     int[] count = new int[circleVertexBuffer.limit() / CIRCLE_SEGMENTS];
    //     int[] first = new int[circleVertexBuffer.limit() / CIRCLE_SEGMENTS];
    //
    //     // Fill arrays for multi-draw
    //     Arrays.fill(count, CIRCLE_SEGMENTS);
    //     IntStream.range(OFFSET, OFFSET + 2).forEach(i -> first[i] = i * CIRCLE_SEGMENTS);    // Map array to increments
    //
    //     glMultiDrawArrays(GL_LINE_LOOP, first, count);
    // }

    // public void createCircle(float x, float y, float radius) {
    //     for (int i = 0; i < CIRCLE_SEGMENTS; i++) {
    //         double a = (i * 2.0 * Math.PI) / CIRCLE_SEGMENTS;
    //         float px = x + (float) (Math.cos(a) * radius);
    //         float py = y + (float) (Math.sin(a) * radius);
    //
    //         circleVertexBuffer.put(px).put(py).put(0.0f);
    //     }
    // }

}
