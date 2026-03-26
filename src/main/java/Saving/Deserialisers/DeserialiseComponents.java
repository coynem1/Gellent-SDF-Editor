package Saving.Deserialisers;

import Rendering.Objects.Components.Blending;
import Rendering.Objects.Components.Component;
import Rendering.Objects.Components.ComponentRounded;
import com.google.gson.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class DeserialiseComponents implements JsonSerializer<Component>, JsonDeserializer<Component> {
    private static final String TYPE = "type";
    private static final String DATA = "data";
    private static final Map<String, Class<? extends Component>> COMPONENT_CLASSES = new HashMap<>();

    private static void registerComponent(Class<? extends Component> componentClass) {
        COMPONENT_CLASSES.put(componentClass.getSimpleName(), componentClass);
    }

    // Add all object components to the map
    static {
        registerComponent(ComponentRounded.class);
        registerComponent(Blending.class);
        registerComponent(ComponentRounded.class);
    }

    @Override
    public Component deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String type = jsonObject.get(TYPE).getAsString();
        JsonElement data = jsonObject.get(DATA);

        try {
            return context.deserialize(data, COMPONENT_CLASSES.get(type));
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
}
