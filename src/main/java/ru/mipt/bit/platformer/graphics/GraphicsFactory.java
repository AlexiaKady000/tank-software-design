package ru.mipt.bit.platformer.graphics;

import ru.mipt.bit.platformer.model.GameObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class GraphicsFactory {
    private final Map<Class<?>, Function<GameObject, ObjectGraphics>> factories = new LinkedHashMap<>();

    public <T extends GameObject> void register(Class<T> type, Function<T, ObjectGraphics> factory) {
        factories.put(type, object -> factory.apply(type.cast(object)));
    }

    public ObjectGraphics create(GameObject object) {
        for (Map.Entry<Class<?>, Function<GameObject, ObjectGraphics>> entry : factories.entrySet()) {
            if (entry.getKey().isInstance(object)) {
                return entry.getValue().apply(object);
            }
        }
        throw new IllegalArgumentException("No graphics registered for " + object.getClass().getName());
    }
}
