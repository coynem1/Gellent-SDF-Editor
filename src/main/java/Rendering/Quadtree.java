package Rendering;

import Rendering.Shaders.ComputeShader;

import java.nio.FloatBuffer;

public class Quadtree {
    private FloatBuffer QTData;
    private ComputeShader computeShader;

    private int resolution;
    private int[] boundary = {0, 0, 100, 100};

    private final String COMPUTE_SHADER_FILENAME = "assets/shaders/quadtree.glsl";

    public Quadtree() {
        resolution = 1024;

        computeShader = new ComputeShader();
        computeShader.init(COMPUTE_SHADER_FILENAME);
        computeShader.compile(resolution);

//        // For each node, store:
//        // - minX, minY, maxX, maxY (bounding box)
//        // - minDist, maxDist (distance bounds in this region)
//        // - subdivided flag or leaf indicator
//        QTData = BufferUtils.createFloatBuffer(nodeCount * 8);
//        for (Node node : quadtreeNodes) {
//            QTData.put(node.minX).put(node.minY);
//            QTData.put(node.maxX).put(node.maxY);
//            QTData.put(node.minDist).put(node.maxDist);
//            QTData.put(node.depth).put(node.hasChildren ? 1.0f : 0.0f);
//        }
//        QTData.flip();
    }


}
