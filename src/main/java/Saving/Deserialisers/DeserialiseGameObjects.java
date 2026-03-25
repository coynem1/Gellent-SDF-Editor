package Saving.Deserialisers;

import Rendering.Objects.Components.Component;
import Rendering.Objects.GameObject;
import com.google.gson.*;

import java.lang.reflect.Type;

public class DeserialiseGameObjects implements JsonDeserializer<GameObject> {
    private static final String NAME = "name";
    private static final String COMPONENTS = "components";

    @Override
    public GameObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String name = jsonObject.get(NAME).getAsString();
        JsonArray components = jsonObject.get(COMPONENTS).getAsJsonArray();

        GameObject obj = new GameObject();
        obj.setName(name);
        for (JsonElement component : components) {
            Component c = context.deserialize(component, Component.class);
            obj.addComponent(c);
        }

        return obj;
    }
}
