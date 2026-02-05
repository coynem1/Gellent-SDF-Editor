package Rendering;

import Jade.Camera;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.IntStream;

import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL14.glMultiDrawArrays;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

// Displays an overlay for visualising scene elements
public class RenderDebugger extends Renderer {

    public RenderDebugger(Path vertexShaderPath, Path fragmentShaderPath, Camera camera) {
        super();

        this.camera = camera;

        // Larger initial capacity for drawing to reduce reallocation time
        this.bufferCapacity = 1024;
        this.indexBuffer = BufferUtils.createIntBuffer(this.bufferCapacity);

        // TODO: why cant I call this after loadbuffers?
        // createRect(1f, 2f, 60f, 50f);
        // createRect(102f, 52f, 60f, 23f);
        setShaderFiles(vertexShaderPath, fragmentShaderPath);

        // Open shader files, compile and link them
        useShaders();
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
        final int SIZE = 4;
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
        // glDrawArrays(GL_LINE_LOOP, 0, 4);
        drawRects();

    }
}
