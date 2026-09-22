package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.Tree;

import static ru.mipt.bit.platformer.util.GdxGameUtils.createBoundingRectangle;
import static ru.mipt.bit.platformer.util.GdxGameUtils.drawTextureRegionUnscaled;
import static ru.mipt.bit.platformer.util.GdxGameUtils.moveRectangleAtTileCenter;

public class TreeGraphics {
    private final Texture texture;
    private final TextureRegion graphics;
    private final Rectangle rectangle;

    public TreeGraphics(Tree tree, TiledMapTileLayer tileLayer) {
        texture = new Texture("images/greenTree.png");
        graphics = new TextureRegion(texture);
        rectangle = createBoundingRectangle(graphics);

        moveRectangleAtTileCenter(
                tileLayer,
                rectangle,
                tree.getCoordinates()
        );
    }

    public void render(Batch batch) {
        drawTextureRegionUnscaled(batch, graphics, rectangle, 0f);
    }

    public void dispose() {
        texture.dispose();
    }
}