package Rendering.Objects.Components;

import java.util.HashMap;
import java.util.Map;

// Keeps track of all component classes because they are not known at run time
public abstract class ComponentNames {
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

    // Translates a string name to a class
    public static Class<? extends Component> getComponentClass(String name) {
        return COMPONENT_CLASSES.get(name);
    }
}
