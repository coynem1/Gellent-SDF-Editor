package Rendering;

import org.joml.Vector4f;

public class RenderDebugger extends Renderer {
    private int shaderProgramID;

    public RenderDebugger() {}

    public void drawRect(float x, float y, float width, float height, Vector4f color) {
        // Rectangle outline as line loop
        float[] vertices = {
                x, y,
                x + width, y,
                x + width, y + height,
                x, y + height
        };

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
