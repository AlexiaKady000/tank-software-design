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

    @Test
    void shouldMoveInEveryDirectionWithoutChangingInput() {
        GridPoint2 initial = new GridPoint2(2, 2);
        GridPoint2[] expected = {new GridPoint2(2, 3), new GridPoint2(1, 2),
                new GridPoint2(2, 1), new GridPoint2(3, 2)};
        Direction[] directions = Direction.values();
        for (int i = 0; i < directions.length; i++) {
            directions[i].getVector().set(100, 100);
            assertEquals(expected[i], directions[i].move(initial));
        }
        assertEquals(new GridPoint2(2, 2), initial);
    }

}
