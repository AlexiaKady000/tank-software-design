package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.MovementField;
import ru.mipt.bit.platformer.model.Tank;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.IntPredicate;

public class KeyboardControlStrategy implements ControlStrategy {
    private final IntPredicate isKeyPressed;
    private final Map<Integer, Direction> keyBindings;

    public KeyboardControlStrategy(IntPredicate isKeyPressed, Map<Integer, Direction> keyBindings) {
        this.isKeyPressed = isKeyPressed;
        this.keyBindings = new LinkedHashMap<>(keyBindings);
    }

    @Override
    public Command getCommand(Tank tank, MovementField field) {
        for (Map.Entry<Integer, Direction> binding : keyBindings.entrySet()) {
            if (isKeyPressed.test(binding.getKey())) {
                return new MoveCommand(tank, binding.getValue(), field);
            }
        }
        return Command.NONE;
    }
}
