package Jade;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_FALSE;
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
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;


public class ScreenRender {
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
    private String[] shaderNames;

    private float[] vertices = {
            // Pos                      // Col
            -1.0f,  1.0f,   0.0f,       0.0f, 1.0f, 0.2f, 0.0f,      // Top Left
            1.0f,   1.0f,   0.0f,       1.0f, 1.0f, 0.0f, 0.0f ,     // Top Right
            -1.0f,  -1.0f,  0.0f,       0.0f, 0.0f, 1.0f, 0.0f,      // Bottom Left
            1.0f,   -1.0f,  0.0f,       0.0f, 1.0f, 0.2f, 0.0f,      // Bottom Right
    };

    private int[] screenBox = {
            0, 1, 2,    // Top Left
            2, 3, 1     // Bottom Right
    };

    private int vaoID, vboID, eboID;


    // Begin shader setup
    public ScreenRender() {
        // Load and compile
        vertexID = glCreateShader(GL_VERTEX_SHADER);

        // Pass shader src to GPU
        glShaderSource(vertexID, vShaderSrc);
        glCompileShader(vertexID);

        // Check for errors
        compileShader(vertexID, "Vertex");

        // Load and compile
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);

        // Pass shader src to GPU
        glShaderSource(fragmentID, fShaderSrc);
        glCompileShader(fragmentID);

        // Check for errors
        compileShader(fragmentID, "Fragment");

        // Link shaders
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexID);
        glAttachShader(shaderProgram, fragmentID);
        glLinkProgram(shaderProgram);

        // Check for Shader errors
        compileShaderLink(shaderProgram);

        // Generate VAO, VBO, and EBO buffer objects for GPU
        // Create VAO
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();

        // Create VBO
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        // Create indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(screenBox.length);
        elementBuffer.put(screenBox).flip();

        // Create EBO
        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, screenBox, GL_STATIC_DRAW);

        // Position & Color attributes
        int posSize = 3;
        int colSize = 4;
        glVertexAttribPointer(0, posSize, GL_FLOAT, false, (posSize + colSize) * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colSize, GL_FLOAT, false, (posSize + colSize) * Float.BYTES, posSize * Float.BYTES);
        glEnableVertexAttribArray(1);

    }

    // Renders shaders every frame
    public void process(float delta) {
        // Bind shader
        glUseProgram(shaderProgram);

        // Bind VAO
        glBindVertexArray(vaoID);

        // Enable vertex pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, screenBox.length, GL_UNSIGNED_INT, 0);

        // Unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);
        glUseProgram(0);
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
            System.err.println("Error compiling link shader: " + glGetShaderInfoLog(shaderProgram, GL_FALSE));
            assert false : "";
        }
    }
}
