package util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glShaderSource;

public class Shader {
    private int shaderProgramID;
    private String vertexShader, fragmentShader;
    private String vertexFilename;
    private String fragFilename;


    // Opens a shader file
    public Shader(String vertexFilename, String fragFilename) {
        String filename = vertexFilename;
        this.vertexFilename = vertexFilename;
        this.fragFilename = fragFilename;

        // Open files
        try {
            this.vertexShader = openFile(filename);
            filename = fragFilename;
            this.fragmentShader = openFile(filename);
//            System.err.println("ERR: Could not load shader file " + filename);
//            this.vertexShader = new String(Files.readAllBytes(Paths.get(filename)));
//            filename = fragFilename;
//            this.fragmentShader = new String(Files.readAllBytes(Paths.get(filename)));
//            System.err.println("ERR: Could not load shader file " + filename);
////            e.printStackTrace();
//            throw new RuntimeException("ERR: Could not load shader file " + filename);
        }
        catch (Exception e) {
//            throw new RuntimeException("ERR: Could not load shader file " + filename, e);
            System.err.println("ERR: Could not load shader file " + filename);
            e.printStackTrace();
            throw new RuntimeException("ERR: Could not load shader file " + filename, e);

        }

//        IO.println(this.vertexShader);
//        IO.println(this.fragmentShader);
    }

    // Loads
    private String openFile(String filename) {
        try {
            File file = new File(filename);
            assert file.exists(): "ERR: Failed to open file, shader: " + filename + " does not exist";

            return new String(Files.readAllBytes(Paths.get(filename)));
        }
        catch (Exception e) {
            System.err.println("ERR: Could not load shader file " + filename);
            e.printStackTrace();
            throw new RuntimeException("ERR: Could not load shader file " + filename, e);
        }
    }

    // Compile and link shaders
    public void compile() {
        int vertexID, fragmentID;

        // Load and compile
        vertexID = glCreateShader(GL_VERTEX_SHADER);

        // Pass shader src to GPU
        glShaderSource(vertexID, vertexShader);
        glCompileShader(vertexID);

        // Check for errors
        compileShader(vertexID, "Vertex");

        // Load and compile
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);

        // Pass shader src to GPU
        glShaderSource(fragmentID, fragmentShader);
        glCompileShader(fragmentID);

        // Check for errors
        compileShader(fragmentID, "Fragment");

        // Link shaders
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        // Check for Shader errors
        compileShaderLink(shaderProgramID);
    }

    // Stops program if there's a shader compiling error
    private void compileShader(int shader, String type) {
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Error compiling " + type + " : " + glGetShaderInfoLog(shader, GL_FALSE));
            assert false : "";
        }
    }

    // Stops program if there's a shader link compiling error
    private void compileShaderLink(int program) {
        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            System.err.println("Error compiling link shader: " + glGetShaderInfoLog(shaderProgramID, GL_FALSE));
            assert false : "";
        }
    }

    // Bind shader
    public void run() {
        glUseProgram(shaderProgramID);
    }

    // 
    public void uploadMat4(String varName, Matrix4f mat4) {
        final int FOUR_BY_FOUR = 16;

        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(FOUR_BY_FOUR);
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }


//    public void detachShader() {
//        glUseProgram(0);
//    }

}
