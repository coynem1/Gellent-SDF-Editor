package Rendering;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glShaderSource;

// General purpose shader
public class Shader {
    protected int shaderProgramID;
    private String vertexShaderSource, fragmentShaderSource;
    private Path vertexPath, fragPath;
    protected boolean currentlyUsed;


    // Opens a shader file
    public Shader() {
    }

    // General init for inherit overriding
    public void init(Path vertexPath, Path fragPath) {
        Path currentShader = vertexPath;
        this.vertexPath = vertexPath;
        this.fragPath = fragPath;

        // Open files
        try {
            this.vertexShaderSource = new String(Files.readAllBytes(this.vertexPath));
            currentShader = fragPath;
            this.fragmentShaderSource = new String(Files.readAllBytes(this.fragPath));
        }
        catch (Exception e) {
            throw new RuntimeException("ERR: Could not load shader file " + currentShader.toString(), e);
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
            System.err.println("Error compiling " + type + " : " + glGetShaderInfoLog(shader, GL_FALSE));
            assert false : "";
        }
    }

    // Stops program if there's a shader link compiling error
    protected void compileShaderLink(int program, String type) {
        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            System.err.println("Error compiling " + type + " shader: " + glGetShaderInfoLog(shaderProgramID, GL_FALSE));
            assert false : "";
        }
    }

    // Switch to run shader, for different layers
    public void run() {
        glUseProgram(shaderProgramID);
    }

    // Posts new variable to shader
    public void uploadMat4(String varName, Matrix4f matrix) {
        final int FOUR_BY_FOUR = 16;
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        run();

        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(FOUR_BY_FOUR);
        matrix.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void uploadMat3(String varName, Matrix3f matrix) {
        final int THREE_BY_THREE = 9;
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        run();

        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(THREE_BY_THREE);
        matrix.get(matBuffer);
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }

    public void uploadVec4f(String varName, Vector4f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        run();
        glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
    }

    public void uploadVec3f(String varName, Vector3f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        run();
        glUniform3f(varLocation, vec.x, vec.y, vec.z);
    }

    public void uploadVec2f(String varName, Vector2f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        run();
        glUniform2f(varLocation, vec.x, vec.y);
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

    public void uploadInt(String varName, int val) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        run();
        glUniform1i(varLocation, val);
    }


}
