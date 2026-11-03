package ru.mipt.bit.platformer.input.test;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.command.ShootCommand;
import ru.mipt.bit.platformer.input.TankInputHandler;
import ru.mipt.bit.platformer.model.Bullet;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Field;
import ru.mipt.bit.platformer.model.Tank;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class TankInputHandlerTest {
    @Test
    void shouldReturnDeferredCommandFromStrategy() {
        Tank tank = new Tank(new GridPoint2(1, 1));
        Field field = new Field(5, 5);
        field.addObject(tank);
        AtomicInteger calls = new AtomicInteger();
        TankInputHandler handler = new TankInputHandler(tank, field, (controlled, world) -> {
            assertSame(tank, controlled);
            assertSame(field, world);
            return calls::incrementAndGet;
        });
        Command command = handler.getCommand();
        assertEquals(0, calls.get());
        command.execute();
        assertEquals(1, calls.get());
    }

    @Test
    void shouldAllowShootingWhileMoving() {
        Tank tank = new Tank(new GridPoint2(1, 1));
        Field field = new Field(5, 5);
        field.addObject(tank);
        tank.move(Direction.RIGHT, field);
        TankInputHandler handler = new TankInputHandler(tank, field, ShootCommand::new);
        handler.getCommand().execute();
        assertTrue(tank.isMoving());
        assertTrue(field.getObjects().stream().anyMatch(object -> object instanceof Bullet));
        assertEquals(3, tank.getHealth());
    }

    @Test
    void shouldRetryAfterBlockedCommand() {
        Tank tank = new Tank(new GridPoint2(1, 1));
        Field field = new Field(5, 2);
        field.addObject(tank);
        AtomicInteger decisions = new AtomicInteger();
        TankInputHandler handler = new TankInputHandler(tank, field, (controlled, world) ->
                new MoveCommand(controlled, decisions.getAndIncrement() == 0 ? Direction.UP : Direction.RIGHT, world));
        handler.getCommand().execute();
        handler.getCommand().execute();
        assertEquals(2, decisions.get());
        assertTrue(tank.isMoving());
    }

    @Test
    void shouldIgnoreRemovedOrDeadTank() {
        Tank tank = new Tank(new GridPoint2(1, 1));
        Field field = new Field(5, 5);
        field.addObject(tank);
        TankInputHandler handler = new TankInputHandler(tank, field, (controlled, world) -> {
            fail("Inactive tank selected a command");
            return Command.NONE;
        });
        tank.damage(3);
        assertFalse(handler.isActive());
        assertSame(Command.NONE, handler.getCommand());
        field.removeObject(tank);
        handler.getCommand().execute();
    }
}
