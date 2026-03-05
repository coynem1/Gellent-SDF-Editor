package Rendering.Objects;

import Rendering.Objects.Components.Component;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
    public static final String DEFAULT_NAME = "Object";
    private String name = DEFAULT_NAME;
    private Vector3f position;
    private List<Component> components = new ArrayList<>();


    public GameObject() {
        name = DEFAULT_NAME;
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c : components) {
            if (componentClass.isInstance(c)) {
                return componentClass.cast(c);  // This is guaranteed safe, no try-catch needed
            }
        }
        return null;
    }

    public <T extends Component> void removeComponents(Class<T> componentClass) {
        // Avoids index shifting and concurrent modification
        for (int i = components.size() - 1; i >= 0; i--) {
            Component c = components.get(i);
            if (!componentClass.isInstance(c)) {
                continue;
            }
            components.remove(i);
            return;
        }
    }

    public void addComponent(Component c) {
        components.add(c);
        c.gameObject = this;
    }

    public void update(float delta) {
        // Avoids index shifting and concurrent modification
        for (int i = components.size() - 1; i >= 0; i--) {
            components.get(i).update(delta);
        }
    }

    public void start() {
        // Avoids index shifting and concurrent modification
        for (int i = components.size() - 1; i >= 0; i--) {
            components.get(i).start();
        }

    }




}
