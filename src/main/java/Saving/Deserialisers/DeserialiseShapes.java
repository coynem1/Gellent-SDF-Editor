package Saving.Deserialisers;

import Input.InputShapes;
import Rendering.Objects.Components.Component;
import Rendering.Objects.Shape;
import com.google.gson.*;

import java.lang.reflect.Type;

public class DeserialiseShapes implements JsonDeserializer<Shape> {
    private static final String COLOUR = "colour";
    private static final String TRANSFORM = "transform";
    private static final String NAME = "name";
    private static final String SHAPE_TYPE = "shapeType";
    private static final String COMPONENTS = "components";

    @Override
    public Shape deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // Vector3f colour = jsonObject.get(COLOUR).getAsJsonArray();
        String name = jsonObject.get(NAME).getAsString();
        InputShapes.SHAPES shapeType = InputShapes.SHAPES.valueOf(jsonObject.get(SHAPE_TYPE).getAsString());
        JsonArray components = jsonObject.get(COMPONENTS).getAsJsonArray();

        Shape obj = new Shape(shapeType, null);
        obj.setName(name);
        for (JsonElement component : components) {
            Component c = context.deserialize(component, Component.class);
            obj.addComponent(c);
        }

        return obj;
    }
}
