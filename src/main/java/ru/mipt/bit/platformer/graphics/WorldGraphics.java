package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.model.GameObject;
import ru.mipt.bit.platformer.model.LevelObserver;
import ru.mipt.bit.platformer.model.ObservableLevel;

import java.util.LinkedHashMap;
import java.util.Map;

public final class WorldGraphics implements LevelObserver, Renderable, Disposable {
    private final ObservableLevel level;
    private final GraphicsFactory factory;
    private final Map<GameObject, ObjectGraphics> graphics = new LinkedHashMap<>();

    public WorldGraphics(ObservableLevel level, GraphicsFactory factory) {
        this.level = level;
        this.factory = factory;
        level.addObserver(this);
    }

    @Override
    public void objectAdded(GameObject object) {
        if (!graphics.containsKey(object)) {
            graphics.put(object, factory.create(object));
        }
    }

    @Override
    public void objectRemoved(GameObject object) {
        ObjectGraphics removed = graphics.remove(object);
        if (removed != null) {
            removed.dispose();
        }
    }

    @Override
    public void render(Batch batch) {
        for (ObjectGraphics object : graphics.values()) {
            object.update();
            object.render(batch);
        }
    }

    @Override
    public void dispose() {
        level.removeObserver(this);
        for (ObjectGraphics object : graphics.values()) {
            object.dispose();
        }
        graphics.clear();
    }
}
