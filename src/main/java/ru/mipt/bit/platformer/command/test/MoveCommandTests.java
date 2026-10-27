package ru.mipt.bit.platformer.command.test;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Field;
import ru.mipt.bit.platformer.model.Tank;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoveCommandTest {
    @Test
    void shouldDeferMovementUntilExecution() {
        Tank tank = new Tank(new GridPoint2(1, 1));
        AtomicInteger checks = new AtomicInteger();
        Command command = new MoveCommand(tank, Direction.UP, coordinates -> {
            checks.incrementAndGet();
            return true;
        });

        assertFalse(tank.isMoving());
        assertEquals(0, checks.get());
        command.execute();
        assertTrue(tank.isMoving());
        assertEquals(new GridPoint2(1, 2), tank.getDestinationCoordinates());
        assertEquals(1, checks.get());
    }

    @Test
    void shouldCheckFieldAtExecutionTime() {
        Tank tank = new Tank(new GridPoint2(1, 1));
        Field field = new Field(3, 3);
        Command command = new MoveCommand(tank, Direction.RIGHT, field);
        field.addObstacle(coordinates -> coordinates.equals(new GridPoint2(2, 1)));

        command.execute();

        assertFalse(tank.isMoving());
        assertEquals(new GridPoint2(1, 1), tank.getDestinationCoordinates());
    }

    @Test
    void shouldSupportEmptyCommand() {
        Command.NONE.execute();
    }
}
