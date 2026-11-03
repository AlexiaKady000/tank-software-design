package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.Bullet;

import static ru.mipt.bit.platformer.util.GdxGameUtils.moveRectangleAtTileCenter;

public class BulletGraphics implements ObjectGraphics {
    private final Bullet bullet;
    private final TiledMapTileLayer tileLayer;
    private final Texture texture;
    private final Rectangle rectangle = new Rectangle(0, 0, 12, 12);

    public BulletGraphics(Bullet bullet, TiledMapTileLayer tileLayer, Texture texture) {
        this.bullet = bullet;
        this.tileLayer = tileLayer;
        this.texture = texture;
    }

    @Override
    public void update() {
        moveRectangleAtTileCenter(tileLayer, rectangle, bullet.getCoordinates());
    }

    @Override
    public void render(Batch batch) {
        batch.draw(texture, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    @Override
    public void dispose() {
    }
}
