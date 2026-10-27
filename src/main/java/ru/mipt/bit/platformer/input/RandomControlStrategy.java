package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.MovementField;
import ru.mipt.bit.platformer.model.Tank;

import java.util.Random;

public class RandomControlStrategy implements ControlStrategy {
    private final Random random;

    public RandomControlStrategy(Random random) {
        this.random = random;
    }

    @Override
    public Command getCommand(Tank tank, MovementField field) {
        Direction[] directions = Direction.values();
        return new MoveCommand(tank, directions[random.nextInt(directions.length)], field);
    }
}
