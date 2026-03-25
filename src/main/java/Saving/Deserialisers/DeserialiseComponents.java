package Saving.Deserialisers;

import Rendering.Objects.Components.Component;
import com.google.gson.*;
import java.lang.reflect.Type;

public class DeserialiseComponents implements JsonSerializer<Component>, JsonDeserializer<Component> {
    private static final String TYPE = "type";
    private static final String DATA = "data";

    @Override
    public Component deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String type = jsonObject.get(TYPE).getAsString();
        JsonElement data = jsonObject.get(DATA);

        try {
            return context.deserialize(data, Class.forName(type));
        } catch (ClassNotFoundException e) {
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
