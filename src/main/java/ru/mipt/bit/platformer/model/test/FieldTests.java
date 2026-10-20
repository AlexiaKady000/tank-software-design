package ru.mipt.bit.platformer.model.test;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.model.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FieldTest {
    @Test
    void shouldRejectCellsOutsideField() {
        Field field = new Field(10, 8);

        assertFalse(field.canMoveTo(new GridPoint2(-1, 0)));
        assertFalse(field.canMoveTo(new GridPoint2(0, -1)));
        assertFalse(field.canMoveTo(new GridPoint2(10, 0)));
        assertFalse(field.canMoveTo(new GridPoint2(0, 8)));
        assertTrue(field.canMoveTo(new GridPoint2(9, 7)));
    }

    @Test
    void shouldSupportObstaclesOccupyingSeveralCells() {
        Field field = new Field(10, 8);
        field.addObstacle(coordinates -> coordinates.y == 3
                && coordinates.x >= 2 && coordinates.x <= 4);

        assertFalse(field.canMoveTo(new GridPoint2(2, 3)));
        assertFalse(field.canMoveTo(new GridPoint2(3, 3)));
        assertFalse(field.canMoveTo(new GridPoint2(4, 3)));
        assertTrue(field.canMoveTo(new GridPoint2(5, 3)));
        assertTrue(field.canMoveTo(new GridPoint2(3, 2)));
    }

    @Test
    void shouldSupportOtherObstacles() {
        Field field = new Field(10, 8);
        field.addObstacle(coordinates -> coordinates.equals(new GridPoint2(2, 3)));

        assertFalse(field.canMoveTo(new GridPoint2(2, 3)));
        assertTrue(field.canMoveTo(new GridPoint2(3, 3)));
    }
}
