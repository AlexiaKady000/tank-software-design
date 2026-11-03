package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.command.ShootCommand;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.WorldContext;
import ru.mipt.bit.platformer.model.Tank;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.IntPredicate;
import java.util.function.BooleanSupplier;

public class KeyboardControlStrategy implements ControlStrategy {
    private final IntPredicate isKeyPressed;
    private final BooleanSupplier isShootPressed;
    private final Map<Integer, Direction> keyBindings;

    public KeyboardControlStrategy(IntPredicate isKeyPressed, Map<Integer, Direction> keyBindings) {
        this(isKeyPressed, keyBindings, () -> false);
    }

    public KeyboardControlStrategy(IntPredicate isKeyPressed, Map<Integer, Direction> keyBindings,
                                   BooleanSupplier isShootPressed) {
        this.isKeyPressed = isKeyPressed;
        this.isShootPressed = isShootPressed;
        this.keyBindings = new LinkedHashMap<>(keyBindings);
    }

    @Override
    public Command getCommand(Tank tank, WorldContext field) {
        if (isShootPressed.getAsBoolean()) {
            return new ShootCommand(tank, field);
        }
        if (tank.isMoving()) {
            return Command.NONE;
        }
        for (Map.Entry<Integer, Direction> binding : keyBindings.entrySet()) {
            if (isKeyPressed.test(binding.getKey())) {
                return new MoveCommand(tank, binding.getValue(), field);
            }
        }
        return Command.NONE;
    }
}
