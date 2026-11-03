package ru.mipt.bit.platformer.command.test;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Field;
import ru.mipt.bit.platformer.model.Tank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoveCommandTest {
    @Test
    void shouldDeferMovementUntilExecution() {
        Tank tank = new Tank(new GridPoint2(1, 1));
        Field field = new Field(3, 3);
        field.addObject(tank);
        Command command = new MoveCommand(tank, Direction.UP, field);
        assertFalse(tank.isMoving());
        command.execute();
        assertTrue(tank.isMoving());
        assertEquals(new GridPoint2(1, 2), tank.getDestinationCoordinates());
    }

    @Test
    void shouldCheckFieldAtExecutionTime() {
        Tank tank = new Tank(new GridPoint2(1, 1));
        Field field = new Field(3, 3);
        field.addObject(tank);
        Command command = new MoveCommand(tank, Direction.RIGHT, field);
        field.addObstacle(coordinates -> coordinates.equals(new GridPoint2(2, 1)));
        command.execute();
        assertFalse(tank.isMoving());
        assertEquals(new GridPoint2(1, 1), tank.getDestinationCoordinates());
    }

    @Test
    void shouldIgnoreCommandAfterTankRemoval() {
        Tank tank = new Tank(new GridPoint2(1, 1));
        Field field = new Field(3, 3);
        field.addObject(tank);
        Command command = new MoveCommand(tank, Direction.RIGHT, field);
        field.removeObject(tank);
        command.execute();
        assertFalse(tank.isMoving());
    }

    @Test
    void shouldSupportEmptyCommand() {
        Command.NONE.execute();
    }
}
