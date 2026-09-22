package ru.mipt.bit.platformer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;

import ru.mipt.bit.platformer.util.TileMovement;

import static com.badlogic.gdx.math.MathUtils.isEqual;
import static ru.mipt.bit.platformer.util.GdxGameUtils.continueProgress;
import static ru.mipt.bit.platformer.util.GdxGameUtils.createBoundingRectangle;
import static ru.mipt.bit.platformer.util.GdxGameUtils.drawTextureRegionUnscaled;

public class Tank {
    private static final float MOVEMENT_SPEED = 0.4f;
    private final Texture texture;
    private final TextureRegion graphics;
    private final Rectangle rectangle;
    private final GridPoint2 coordinates;
    private final GridPoint2 destinationCoordinates;
    private float movementProgress = 1f;
    private Direction direction = Direction.RIGHT;

    public Tank(String texturePath, GridPoint2 initialCoordinates) {
        texture = new Texture(texturePath);
        graphics = new TextureRegion(texture);
        rectangle = createBoundingRectangle(graphics);
        coordinates = new GridPoint2(initialCoordinates);
        destinationCoordinates = new GridPoint2(initialCoordinates);
    }

    public boolean isMoving() {
        return !isEqual(movementProgress, 1f);
    }

    public void move(Direction direction, Field field) {
        if (isMoving()) {
            return;
        }
        this.direction = direction;
        GridPoint2 newDestination = direction.move(coordinates);
        if (field.canMoveTo(newDestination)) {
            destinationCoordinates.set(newDestination);
            movementProgress = 0f;
        }
    }

    public GridPoint2 getCoordinates() {
        return coordinates;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void update(float deltaTime, TileMovement tileMovement) {
        tileMovement.moveRectangleBetweenTileCenters(rectangle, coordinates, destinationCoordinates, movementProgress);
        movementProgress = continueProgress(movementProgress, deltaTime, MOVEMENT_SPEED);
        if (isEqual(movementProgress, 1f)) {
            coordinates.set(destinationCoordinates);
        }
    }

    public void render(Batch batch) {
        drawTextureRegionUnscaled(batch, graphics, rectangle, direction.getRotation());
    }

    public void dispose() {
        texture.dispose();
    }
}
