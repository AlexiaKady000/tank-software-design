package ru.mipt.bit.platformer.input.test;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.input.ControlStrategy;
import ru.mipt.bit.platformer.input.MoveInputHandler;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.MovementField;
import ru.mipt.bit.platformer.model.Tank;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoveInputHandlerTest {
    @Test
    void shouldExecuteCommandFromStrategy() {
        Tank tank = new Tank(new GridPoint2(1, 1));
        MovementField field = coordinates -> true;
        AtomicInteger calls = new AtomicInteger();
        ControlStrategy strategy = (controlledTank, movementField) -> {
            assertSame(tank, controlledTank);
            assertSame(field, movementField);
            return calls::incrementAndGet;
        };

        new MoveInputHandler(tank, field, strategy).handleInput();

        assertEquals(1, calls.get());
    }

    @Test
    void shouldNotChooseCommandsWhileMoving() {
        Tank tank = new Tank(new GridPoint2(1, 1));
        AtomicInteger decisions = new AtomicInteger();
        MoveInputHandler handler = new MoveInputHandler(tank, coordinates -> true, (controlled, field) -> {
            decisions.incrementAndGet();
            return new MoveCommand(controlled, Direction.RIGHT, field);
        });

        handler.handleInput();
        tank.update(0.2f);
        handler.handleInput();
        assertEquals(1, decisions.get());
        assertEquals(new GridPoint2(2, 1), tank.getDestinationCoordinates());

        tank.update(0.2f);
        handler.handleInput();
        assertEquals(2, decisions.get());
        assertEquals(new GridPoint2(3, 1), tank.getDestinationCoordinates());
    }

    @Test
    void shouldRetryAfterBlockedCommand() {
        Tank tank = new Tank(new GridPoint2(1, 1));
        AtomicInteger decisions = new AtomicInteger();
        MoveInputHandler handler = new MoveInputHandler(tank, coordinates -> coordinates.y < 2,
                (controlled, field) -> new MoveCommand(controlled,
                        decisions.getAndIncrement() == 0 ? Direction.UP : Direction.RIGHT, field));

        handler.handleInput();
        handler.handleInput();

        assertEquals(2, decisions.get());
        assertTrue(tank.isMoving());
        assertEquals(new GridPoint2(2, 1), tank.getDestinationCoordinates());
    }

    @Test
    void shouldHandleStrategyWithoutCommand() {
        Tank tank = new Tank(new GridPoint2(1, 1));
        new MoveInputHandler(tank, coordinates -> true, (controlled, field) -> Command.NONE).handleInput();
        assertEquals(new GridPoint2(1, 1), tank.getDestinationCoordinates());
    }
}
