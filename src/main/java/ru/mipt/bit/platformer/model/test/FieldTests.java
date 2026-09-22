package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TankTest {
    @Test
    void shouldStartAtInitialCoordinates() {
        Tank tank = new Tank(new GridPoint2(1, 1));

        assertEquals(new GridPoint2(1, 1), tank.getCoordinates());
        assertFalse(tank.isMoving());
    }

    @Test
    void shouldStartMovingToEmptyCell() {
        Field field = new Field(10, 8);
        Tank tank = new Tank(new GridPoint2(1, 1));

        tank.move(Direction.UP, field);

        assertTrue(tank.isMoving());
        assertEquals(new GridPoint2(1, 2), tank.getDestinationCoordinates());
    }

    @Test
    void shouldNotMoveIntoTree() {
        Field field = new Field(10, 8);
        field.addTree(new Tree(new GridPoint2(1, 2)));

        Tank tank = new Tank(new GridPoint2(1, 1));

        tank.move(Direction.UP, field);

        assertFalse(tank.isMoving());
        assertEquals(new GridPoint2(1, 1), tank.getDestinationCoordinates());
    }

    @Test
    void shouldReachDestination() {
        Field field = new Field(10, 8);
        Tank tank = new Tank(new GridPoint2(1, 1));

        tank.move(Direction.RIGHT, field);
        tank.update(0.4f);

        assertEquals(new GridPoint2(2, 1), tank.getCoordinates());
        assertFalse(tank.isMoving());
    }
}