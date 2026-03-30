package Rendering;

public class RenderBatch {
    // Format
    //
    //  Transform: Position, Rotation, Scale
    //  Vector3f, Vector3f, float
    //
    //  Shape, Type (Union/Difference/Intersection), Blend, Colour
    //  Byte, Byte, float, Vector3f

    private final int TRANSFORM_SIZE = Float.BYTES * 7;
    private final int SHAPE_SIZE = 1;   // Need to cast to byte
    private final int BLEND_SIZE = Float.BYTES;
    private final int TYPE_SIZE = 1;    // Need to cast to byte
    private final int COLOUR_SIZE = Float.BYTES * 3;

    private final int TRANSFORM_OFFSET = 0;
    private final int SHAPE_OFFSET = TRANSFORM_OFFSET + TRANSFORM_SIZE;
    private final int BLEND_OFFSET = SHAPE_OFFSET + SHAPE_SIZE;
    private final int TYPE_OFFSET = BLEND_OFFSET + BLEND_SIZE;
    private final int COLOUR_OFFSET = TYPE_OFFSET + TYPE_SIZE;



}
