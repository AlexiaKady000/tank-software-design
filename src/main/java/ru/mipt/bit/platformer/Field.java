package ru.mipt.bit.platformer;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;

import java.util.ArrayList;
import java.util.List;

import ru.mipt.bit.platformer.util.TileMovement;

import static ru.mipt.bit.platformer.util.GdxGameUtils.createSingleLayerMapRenderer;
import static ru.mipt.bit.platformer.util.GdxGameUtils.getSingleLayer;
import static ru.mipt.bit.platformer.util.GdxGameUtils.moveRectangleAtTileCenter;

public class Field {
    private final TiledMap map;
    private final TiledMapTileLayer groundLayer;
    private final MapRenderer renderer;
    private final TileMovement tileMovement;
    private final List<Tree> trees = new ArrayList<>();

    public Field(Batch batch) {
        map = new TmxMapLoader().load("level.tmx");
        renderer = createSingleLayerMapRenderer(map, batch);
        groundLayer = getSingleLayer(map);
        tileMovement = new TileMovement(groundLayer, Interpolation.smooth);
    }

    private boolean isInField(GridPoint2 coordinates) {
        return coordinates.x >= 0 && coordinates.x < groundLayer.getWidth() 
             && coordinates.y >= 0 && coordinates.y < groundLayer.getHeight();
    }

    public void placeTank(Tank tank) {
        moveRectangleAtTileCenter(groundLayer, tank.getRectangle(), tank.getCoordinates());
    }

    public void addTree(Tree tree) {
        trees.add(tree);
        moveRectangleAtTileCenter(groundLayer, tree.getRectangle(), tree.getCoordinates());
    }

    public boolean canMoveTo(GridPoint2 coordinates) {
        if (!isInsideField(coordinates)) {
            return false;
        }
        for (Tree tree : trees) {
            if (tree.getCoordinates().equals(coordinates)) {
                return false;
            }
        }
        return true;
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
