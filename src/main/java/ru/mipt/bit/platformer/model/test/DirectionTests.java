package ru.mipt.bit.platformer.model.test;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.model.Direction;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DirectionTest {
    @Test
    void shouldMoveUp() {
        GridPoint2 result = Direction.UP.move(new GridPoint2(1, 1));

        assertEquals(new GridPoint2(1, 2), result);
    }

    @Test
    void shouldHaveCorrectVectors() {
        assertEquals(new GridPoint2(0, 1), Direction.UP.getVector());
        assertEquals(new GridPoint2(-1, 0), Direction.LEFT.getVector());
        assertEquals(new GridPoint2(0, -1), Direction.DOWN.getVector());
        assertEquals(new GridPoint2(1, 0), Direction.RIGHT.getVector());
    }

    @Test
    void shouldHaveCorrectRotations() {
        assertEquals(90f, Direction.UP.getRotation());
        assertEquals(-180f, Direction.LEFT.getRotation());
        assertEquals(-90f, Direction.DOWN.getRotation());
        assertEquals(0f, Direction.RIGHT.getRotation());
    }
}