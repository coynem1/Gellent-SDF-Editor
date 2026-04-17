package Rendering;

import Jade.Camera;
import Observers.WindowEvents;
import Rendering.Shaders.Shader;

import java.io.InputStream;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;


public class RenderSDF extends Renderer {
    // Begin shader setup
    public RenderSDF(InputStream vertexShaderPath, InputStream fragmentShaderPath, Camera camera) {
        super();
        this.camera = camera;

        this.indexBuffer.put(0).put(1).put(2);  // Top Left
        this.indexBuffer.put(2).put(3).put(1);  // Bottom Right

        updateVertices();
        loadBuffers(false);  // VBO, VAO, EBO used for rendering
        setShaderFiles(vertexShaderPath, fragmentShaderPath);

        // Open shader files, compile and link them
        useShaders();
        bindObservers();
    }

    // TODO: Update vertices after resize
    private void bindObservers() {
        // WindowEvents.onScreenResized( (_, _) -> updateVertices());
    }

    // Vertices fix to the screen aspect ratio
    protected void updateVertices() {
        float viewHeight = camera.getViewHeight();
        float viewWidth = camera.getViewWidth();

        vertexBuffer.clear();
        vertexBuffer.put(-viewWidth / 2.0f).put( viewHeight / 2.0f).put(0.0f);  // Top Left
        vertexBuffer.put( viewWidth / 2.0f).put( viewHeight / 2.0f).put(0.0f);  // Top Right
        vertexBuffer.put(-viewWidth / 2.0f).put(-viewHeight / 2.0f).put(0.0f);  // Bottom Left
        vertexBuffer.put( viewWidth / 2.0f).put(-viewHeight / 2.0f).put(0.0f);  // Bottom Right
    }

    // Open shader files, compile, and link them
    private void useShaders() {
        this.currentShader = new Shader();
        this.currentShader.init(this.vertexShaderPath, this.fragmentShaderPath);
        this.currentShader.compile();
        this.currentShader.run();
    }
}
