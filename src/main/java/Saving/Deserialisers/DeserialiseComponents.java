package Saving.Deserialisers;

import Rendering.Objects.Components.*;
import com.google.gson.*;
import org.joml.Vector2f;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class DeserialiseComponents implements JsonSerializer<Component>, JsonDeserializer<Component> {
    private static final String TYPE = "type";
    private static final String DATA = "data";
    private static final String POSITION = "position";
    private static final String ROTATION = "rotation";
    private static final String SCALE = "scale";
    private static final Map<String, Class<? extends Component>> COMPONENT_CLASSES = new HashMap<>();

    private static void registerComponent(Class<? extends Component> componentClass) {
        COMPONENT_CLASSES.put(componentClass.getSimpleName(), componentClass);
    }

    // Add all object components to the map
    static {
        registerComponent(ComponentRounded.class);
        registerComponent(Blending.class);
        registerComponent(ComponentRounded.class);
        registerComponent(Transform2D.class);
    }

    @Override
    public Component deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String type = jsonObject.get(TYPE).getAsString();
        JsonElement data = jsonObject.get(DATA);

        // Special case for transform
        if (type.equals(Transform2D.class.getSimpleName())) {
            return deserialiseTransform(context, data);
        }

        try {
            return context.deserialize(data, ComponentNames.getComponentClass(type));
        } catch (JsonParseException e) {
            throw new JsonParseException("Unknown element type loaded: " + type,e);
        }
    }
    @Override
    public JsonElement serialize(Component src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add(TYPE, new JsonPrimitive(src.getClass().getSimpleName()));
        result.add(DATA, context.serialize(src, src.getClass()));
        return result;
    }

    private Component deserialiseTransform(JsonDeserializationContext context, JsonElement json) {
        JsonObject jsonObject = json.getAsJsonObject();

        JsonObject positionObj = jsonObject.get(POSITION).getAsJsonObject();
        Vector2f position = new Vector2f(
                positionObj.get("x").getAsFloat(),
                positionObj.get("y").getAsFloat()
        );

        float rotation = jsonObject.get(ROTATION).getAsFloat();
        float scale = jsonObject.get(SCALE).getAsFloat();

        Transform2D<Vector2f> transform = Transform2D.createFloat();
        transform.setPosition(position);
        transform.setRotation(rotation);
        transform.setScale(scale);

        return transform;
    }
}
