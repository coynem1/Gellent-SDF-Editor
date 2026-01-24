package Rendering;

import org.lwjgl.BufferUtils;
import org.w3c.dom.Node;

import java.nio.FloatBuffer;

public class Quadtree {
    FloatBuffer QTData;

    public Quadtree() {

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
