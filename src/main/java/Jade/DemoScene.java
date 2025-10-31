package Jade;

import static org.lwjgl.opengl.GL20.*;

public class DemoScene extends Scene {
    private String vShaderSrc = "#version 330 core\n" +
            "layout (location=0) in vec3 aPos;\n" +
            "layout (location=1) in vec4 aColour;\n" +
            "\n" +
            "out vec4 fColour;\n" +
            "\n" +
            "void main() {\n" +
            "    fColour = aColour;\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}";
    private String fShaderSrc = "#version 330 core\n" +
            "\n" +
            "in vec4 fColour;\n" +
            "out vec4 colour;\n" +
            "\n" +
            "void main() {\n" +
            "    colour = fColour;\n" +
            "}";

    private int vertexID, fragmentID, shaderProgram;
    si
    private float[] vertices = {
            // Pos                  // Col
            -0.5f, 0.5f, 0.0f,      0.0f, 1.0f, 0.2f, 0.0f,      // Top Left
            0.5f, 0.5f, 0.0f,       1.0f, 1.0f, 0.0f, 0.0f ,     // Top Right
            -0.5f, -0.5f, 0.0f,     0.0f, 0.0f, 1.0f, 0.0f,      // Bottom Left
            0.5f, -0.5f, 0.0f,      0.0f, 1.0f, 0.2f, 0.0f,      // Bottom Right
    };

    private int[] elements = {
            0, 1, 2,    // Top Left
            2, 3, 1     // Bottom Right
    };




    private String name;
    private int currentDemo;
    private String[] demos;

    public DemoScene(String name) {
        this.name = name;
        this.currentDemo = 0;
        this.demos = new String[]{"Circle", "MultipleShapes", "BlendShapes", "Cutting"};
        super(name);

    }

    @Override
    public void process(float delta) {
        IO.println("Running at " + (1.0f / delta) + "FPS");
    }

    @Override
    public void init() {
        // Load and compile
        vertexID = glCreateShader(GL_VERTEX_SHADER);

        // Pass shader src to GPU
        glShaderSource(vertexID, vShaderSrc);
        glCompileShader(vertexID);

        // Check for errors
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            System.err.println("Error compiling Vertex shader: " + glGetShaderInfoLog(vertexID, GL_FALSE));
            assert false : "";
        }


        // Load and compile
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);

        // Pass shader src to GPU
        glShaderSource(fragmentID, fShaderSrc);
        glCompileShader(fragmentID);

        // Check for errors
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            System.err.println("Error compiling Fragment shader: " + glGetShaderInfoLog(fragmentID, GL_FALSE));
            assert false : "";
        }

        // Link shaders
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexID);
        glAttachShader(shaderProgram, fragmentID);
        glLinkProgram(shaderProgram);

        success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            // int len = glGetProgrami(shaderProgram, GL_LINK_STATUS);
            System.err.println("Error compiling link shader: " + glGetShaderInfoLog(shaderProgram, GL_FALSE));
            assert false : "";
        }
    }

    // Custom circle level
    private void circle() {
        IO.println("Hello World " + name + " " + demos[currentDemo]);
    }

}
