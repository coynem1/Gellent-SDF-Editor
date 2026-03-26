package Saving.Deserialisers;

import Input.InputShapes;
import Rendering.Objects.Components.Component;
import Rendering.Objects.SculptObject;
import Rendering.Objects.Shape;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.joml.Vector2f;
import org.joml.Vector3f;
import util.Transform2D;

import java.lang.reflect.Type;

public class DeserialiseShapes implements JsonDeserializer<Shape> {
    private static final String SCULPT_OBJECT = "sculptObject";
    private static final String COLOUR = "colour";
    private static final String TRANSFORM = "transform";
    private static final String NAME = "name";
    private static final String SHAPE_TYPE = "shapeType";
    private static final String SHAPE_MODES = "shapeModes";
    private static final String COMPONENTS = "components";

    @Override
    public Shape deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        SculptObject sculpt = context.deserialize(jsonObject.get(SCULPT_OBJECT), SculptObject.class);
        Vector3f colour = context.deserialize(jsonObject.get(COLOUR), Vector3f.class);
        //Transform2D<Vector2f> transform = context.deserialize(jsonObject.get(TRANSFORM), Transform2D.class);
        Type transformType = new TypeToken<Transform2D<Vector2f>>(){}.getType();
        Transform2D<Vector2f> transform = context.deserialize(jsonObject.get(TRANSFORM), transformType);

        InputShapes.SHAPES shapeType = InputShapes.SHAPES.valueOf(jsonObject.get(SHAPE_TYPE).getAsString());
        InputShapes.MODES shapeModes = InputShapes.MODES.valueOf(jsonObject.get(SHAPE_MODES).getAsString());
        String name = jsonObject.get(NAME).getAsString();
        JsonArray components = jsonObject.get(COMPONENTS).getAsJsonArray();

        Shape obj = new Shape(shapeType, sculpt);
        obj.setColour(colour);
        obj.setTransform(transform);
        obj.setShapeMode(shapeModes);
        obj.setName(name);
        for (JsonElement component : components) {
            Component T = context.deserialize(component, Component.class);
            obj.addComponent(T);
        }

        return obj;
    }
}
