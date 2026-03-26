package Saving.Deserialisers;

import Rendering.Objects.Components.Component;
import Rendering.Objects.GameObject;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Objects;

public class DeserialiseGameObjects implements JsonDeserializer<GameObject>, JsonSerializer<GameObject> {
    private static final String NAME = "name";
    private static final String COMPONENTS = "components";

    @Override
    public GameObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String name = jsonObject.get(NAME).getAsString();
        JsonArray components = jsonObject.get(COMPONENTS).getAsJsonArray();

        GameObject obj = new GameObject();

        // Is the name default?
        if (!Objects.equals(name, GameObject.DEFAULT_NAME)) obj.setName(name);

        // Add all components
        for (JsonElement component : components) {
            Component c = context.deserialize(component, Component.class);
            obj.addComponent(c);
        }

        return obj;
    }

    @Override
    public JsonElement serialize(GameObject src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();

        // Add name if not default
        if (!Objects.equals(src.getName(), GameObject.DEFAULT_NAME)) result.add(NAME, new JsonPrimitive(src.getName()));
        result.add(COMPONENTS, context.serialize(src.getComponents()));

        return result;
    }
}
