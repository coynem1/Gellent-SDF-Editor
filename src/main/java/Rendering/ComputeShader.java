package Rendering;

import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL42.glMemoryBarrier;
import static org.lwjgl.opengl.GL43.*;

public class ComputeShader extends Shader {
    private static final int LOCAL_SIZE_2D = 16;  // 16x16 threads
    private static final int LOCAL_SIZE_2D_Z = 1; // Single process

    private int computeShader;
    private String computeShaderSource;
    private String computeFilename;
    // private boolean currentlyUsed;
    // private int shaderProgramID;

    // Buffers
    private int ssbo;

    // GPU work partitioning
    private int workGroupsX, workGroupsY, workGroupsZ;
    private int localSizeX, localSizeY, localSizeZ;


    public ComputeShader() {
        super();
        this.currentlyUsed = false;
        this.ssbo = glGenBuffers();
    }

    public void init(String filename) {
        this.computeFilename = filename;

        // Open file
        try {
            this.computeShaderSource = new String(Files.readAllBytes(Paths.get(this.computeFilename)));
        }
        catch (Exception e) {
            System.err.println("ERR: Could not load shader file " + this.computeFilename);
            e.printStackTrace();
            throw new RuntimeException("ERR: Could not load shader file " + this.computeFilename, e);
        }
    }

    // Sets optimised number of workgroups and local size
    private void workGroups(int resolution) {
        // Maximum threads per workgroup
        int maxInvocations = glGetInteger(GL_MAX_COMPUTE_WORK_GROUP_INVOCATIONS);

        localSizeX = LOCAL_SIZE_2D;
        localSizeY = LOCAL_SIZE_2D;
        localSizeZ = LOCAL_SIZE_2D_Z;

        // Check if invocation limits have been exceeded
        if (localSizeX * localSizeY * localSizeZ > maxInvocations) {
            // Set to single threaded
            IO.println("WARNING: Max threads exceeded for GPU workgroups, switching to single threaded workgroups.");
            localSizeX = LOCAL_SIZE_2D_Z;
            localSizeY = LOCAL_SIZE_2D_Z;
        }

        // Faster than using ceil for int division
        workGroupsX = (resolution + localSizeX - 1) / localSizeX;
        workGroupsY = (resolution + localSizeY - 1) / localSizeY;
        workGroupsZ = localSizeZ;

        // Dispatch workgroups
        glDispatchCompute(workGroupsX, workGroupsY, localSizeZ);
        glMemoryBarrier(GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
    }


    public void compile(int resolution) {
        // Puts GLSL into String
        // init();

        // 1. Create and compile compute shader
        computeShader = glCreateShader(GL_COMPUTE_SHADER);
        glShaderSource(computeShader, computeShaderSource);
        glCompileShader(computeShader);

        // 2. Create program and link
        this.shaderProgramID = glCreateProgram();
        glAttachShader(this.shaderProgramID, computeShader);
        glLinkProgram(this.shaderProgramID);

        // 3. Use it
        run();  // glUseProgram(this.shaderProgramID);
        workGroups(resolution);

        // Check errors linking
        compileShaderLink(this.shaderProgramID, "Link Compute Shader");
    }

    public void readBuffer() {
        // 4. Read back results
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        FloatBuffer results = glMapBuffer(GL_SHADER_STORAGE_BUFFER, GL_READ_ONLY).asFloatBuffer();

        // Read data
        float value = results.get(0);

        glUnmapBuffer(GL_SHADER_STORAGE_BUFFER);
    }


}
