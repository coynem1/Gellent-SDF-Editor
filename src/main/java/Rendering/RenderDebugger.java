package Rendering;

import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.file.Path;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBufferData;

public class RenderDebugger extends Renderer {
    private int shaderProgramID;

    public RenderDebugger(Path vertexShaderPath, Path fragmentShaderPath) {
        super();

        // Larger initial capacity for drawing to reduce reallocation time
        this.indexBufferCapacity = 1024;
        this.indexBuffer = BufferUtils.createIntBuffer(this.indexBufferCapacity);


        // this.indexBuffer = new int[]{
        //         0, 1, 2    // Top Left
        //         // 2, 3, 1     // Bottom Right
        // };

        // Draw triangle
        this.indexBuffer.put(0).put(1).put(2);
        //         0, 1, 2    // Top Left
        //         // 2, 3, 1     // Bottom Right
        // });

        // updateVertices();
        loadBuffers();  // VBO, VAO, EBO used for rendering
        setShaderFiles(vertexShaderPath, fragmentShaderPath);

        // Open shader files, compile and link them
        useShaders();
    }

    // Changed to dynamic draw for optimised screen updating
    @Override
    protected void createVBO() {
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);
    }

    // Open shader files, compile, and link them
    private void useShaders() {
        this.currentShader = new Shader();
        this.currentShader.init(this.vertexShaderPath, this.fragmentShaderPath);
        this.currentShader.compile();
        this.currentShader.run();
    }

    public void drawRect(float x, float y, float width, float height, Vector4f color) {
        // // Rectangle outline as line loop
        // float[] vertices = {
        //         x, y,
        //         x + width, y,
        //         x + width, y + height,
        //         x, y + height
        // };

        this.indexBuffer[] = 0;

        // glUseProgram(shaderProgramID);
        //
        // // Set color
        // int colorLoc = glGetUniformLocation(shaderProgramID, "color");
        // glUniform3f(colorLoc, color.x, color.y, color.z);
        //
        // // Set projection (orthographic for screen space)
        // int projLoc = glGetUniformLocation(shaderProgramID, "projection");
        // // Assuming screen coords 0-width, 0-height
        // glUniformMatrix4fv(projLoc, false, createOrthoMatrix());
        //
        // // Upload vertices
        // glBindVertexArray(vao);
        // glBindBuffer(GL_ARRAY_BUFFER, vbo);
        // glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);
        //
        // // Draw as line loop
        // glDrawArrays(GL_LINE_LOOP, 0, 4);
    }
}


// CPU side
//        int ssbo = glGenBuffers();
//        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
//        glBufferData(GL_SHADER_STORAGE_BUFFER, quadtreeData, GL_DYNAMIC_DRAW);
//        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, ssbo);
