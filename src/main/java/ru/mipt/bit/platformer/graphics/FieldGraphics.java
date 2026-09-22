package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Interpolation;
import ru.mipt.bit.platformer.util.TileMovement;
import static ru.mipt.bit.platformer.util.GdxGameUtils.createSingleLayerMapRenderer;
import static ru.mipt.bit.platformer.util.GdxGameUtils.getSingleLayer;

public class FieldGraphics {
    private final TiledMap map;
    private final TiledMapTileLayer groundLayer;
    private final MapRenderer renderer;
    private final TileMovement tileMovement;

    public FieldGraphics(Batch batch) {
        map = new TmxMapLoader().load("level.tmx");
        renderer = createSingleLayerMapRenderer(map, batch);
        groundLayer = getSingleLayer(map);
        tileMovement = new TileMovement(groundLayer, Interpolation.smooth);
    }

    public TiledMapTileLayer getGroundLayer() {
        return groundLayer;
    }

    public TileMovement getTileMovement() {
        return tileMovement;
    }

    public void render() {
        renderer.render();
    }

    public void dispose() {
        map.dispose();
    }
}