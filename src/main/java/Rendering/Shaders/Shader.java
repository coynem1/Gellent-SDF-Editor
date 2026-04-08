package Rendering.Shaders;

import org.jetbrains.annotations.NotNull;
import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glShaderSource;

// General purpose shader
public class Shader {
    protected int shaderProgramID;
    private String vertexShaderSource, fragmentShaderSource;

    // Opens a shader file
    public Shader() {}

    // General start for inherited overriding
    public void init(InputStream vertexPath, InputStream fragPath) {
        // Read files
        try {
            this.vertexShaderSource = new String(vertexPath.readAllBytes());
            this.fragmentShaderSource = new String(fragPath.readAllBytes());
        }
        catch (Exception e) {
            throw new RuntimeException("ERR: Could not load shader data into string", e);
        }
    }

    // Compile and link shaders
    public void compile() {
        int vertexID, fragmentID;

        // Load and compile
        vertexID = glCreateShader(GL_VERTEX_SHADER);

        // Pass shader src to GPU
        glShaderSource(vertexID, vertexShaderSource);
        glCompileShader(vertexID);

        // Check for errors
        compileShader(vertexID, "Vertex");

        // Load and compile
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);

        // Pass shader src to GPU
        glShaderSource(fragmentID, fragmentShaderSource);
        glCompileShader(fragmentID);

        // Check for errors
        compileShader(fragmentID, "Fragment");

        // Link shaders
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        // Check for Shader errors
        compileShaderLink(shaderProgramID, "Link");
    }

    // Stops program if there's a shader compiling error
    protected void compileShader(int shader, String type) {
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException("Error compiling " + type + " : " + glGetShaderInfoLog(shader, GL_FALSE));
        }
    }

    // Stops program if there's a shader link compiling error
    protected void compileShaderLink(int program, String type) {
        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            throw new RuntimeException("Error linking " + type + " : " + glGetShaderInfoLog(shaderProgramID, GL_FALSE));
        }
    }

    // Switch to run shader, for different layers
    public void run() {
        glUseProgram(shaderProgramID);
    }

    // Posts new variable to shader
    public void uploadMat4(String varName, @NotNull Matrix4f matrix) {
        final int FOUR_BY_FOUR = 16;
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        run();

        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(FOUR_BY_FOUR);
        matrix.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void uploadMat3(String varName, @NotNull Matrix3f matrix) {
        final int THREE_BY_THREE = 9;
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        run();

        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(THREE_BY_THREE);
        matrix.get(matBuffer);
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }

    public void uploadVec4f(String varName, @NotNull Vector4f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        run();
        glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
    }

    public void uploadVec3f(String varName, @NotNull Vector3f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        run();
        glUniform3f(varLocation, vec.x, vec.y, vec.z);
    }
    public void uploadVec3f(String varName, @NotNull Vector3f[] vecs) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        run();

        // Flatten into a float[] buffer
        float[] flat = new float[vecs.length * 3];
        for (int i = 0; i < vecs.length; i++) {
            flat[i * 3]     = vecs[i].x;
            flat[i * 3 + 1] = vecs[i].y;
            flat[i * 3 + 2] = vecs[i].z;
        }

        glUniform2fv(varLocation, flat);
    }

    public void uploadVec2f(String varName, @NotNull Vector2f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        run();
        glUniform2f(varLocation, vec.x, vec.y);
    }
    public void uploadVec2f(String varName, @NotNull Vector2f[] vecs, int count) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        run();

        // Flatten into a float[] buffer
        float[] flat = new float[count * 2];
        for (int i = 0; i < count; i++) {
            flat[i * 2]     = vecs[i].x;
            flat[i * 2 + 1] = vecs[i].y;
        }

        glUniform2fv(varLocation, flat);
    }

    public void uploadVec2i(String varName, Vector2i vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        run();
        glUniform2i(varLocation, vec.x, vec.y);
    }

    public void uploadFloat(String varName, float val) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        run();
        glUniform1f(varLocation, val);
    }
    public void uploadFloat(String varName, float[] vals) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        run();
        glUniform1fv(varLocation, vals);
    }

    public void uploadInt(String varName, int val) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        run();
        glUniform1i(varLocation, val);
    }
    public void uploadInt(String varName, int[] vals) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        run();
        glUniform1iv(varLocation, vals);
    }


}
