package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.command.ShootCommand;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.WorldContext;

import java.util.Random;

public class RandomControlStrategy implements ControlStrategy {
    private final Random random;
    private final float shootProbability;

    public RandomControlStrategy(Random random) {
        this(random, 0.2f);
    }

    public RandomControlStrategy(Random random, float shootProbability) {
        if (!Float.isFinite(shootProbability) || shootProbability < 0f || shootProbability > 1f) {
            throw new IllegalArgumentException("Shoot probability must be between zero and one");
        }
        this.random = random;
        this.shootProbability = shootProbability;
    }

    @Override
    public Command getCommand(Tank tank, WorldContext world) {
        if (random.nextFloat() < shootProbability) {
            return new ShootCommand(tank, world);
        }
        if (tank.isMoving()) {
            return Command.NONE;
        }
        Direction[] directions = Direction.values();
        return new MoveCommand(tank, directions[random.nextInt(directions.length)], world);
    }
}
