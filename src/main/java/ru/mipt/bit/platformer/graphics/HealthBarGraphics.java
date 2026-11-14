package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.Health;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import static com.badlogic.gdx.math.MathUtils.clamp;

public class HealthBarGraphics implements ObjectGraphics {
    private static final float BAR_HEIGHT = 6f;
    private static final float BAR_MARGIN = 4f;

    private final ObjectGraphics graphics;
    private final Health health;
    private final Supplier<Rectangle> bounds;
    private final BooleanSupplier visible;
    private final Texture texture;

    public HealthBarGraphics(ObjectGraphics graphics, Health health, Supplier<Rectangle> bounds,
                             BooleanSupplier visible, Texture texture) {
        this.graphics = graphics;
        this.health = health;
        this.bounds = bounds;
        this.visible = visible;
        this.texture = texture;
    }

    @Override
    public void update() {
        graphics.update();
    }

    @Override
    public void render(Batch batch) {
        graphics.render(batch);
        if (!visible.getAsBoolean()) {
            return;
        }
        Rectangle rectangle = bounds.get();
        float width = rectangle.width * 0.6f;
        float x = rectangle.x + (rectangle.width - width) / 2f;
        float y = rectangle.y + rectangle.height - BAR_HEIGHT - BAR_MARGIN;
        float fraction = clamp(health.getHealth() / (float) health.getMaxHealth(), 0f, 1f);
        float color = batch.getPackedColor();
        batch.setColor(0.5f, 0.1f, 0.1f, 1f);
        batch.draw(texture, x, y, width, BAR_HEIGHT);
        batch.setColor(0.1f, 0.8f, 0.1f, 1f);
        batch.draw(texture, x, y, width * fraction, BAR_HEIGHT);
        batch.setPackedColor(color);
    }

    @Override
    public void dispose() {
        graphics.dispose();
    }
}
