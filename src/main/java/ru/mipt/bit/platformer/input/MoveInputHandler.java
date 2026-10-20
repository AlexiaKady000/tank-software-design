package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.model.Direction;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntPredicate;

public class MoveInputHandler implements InputHandler {
    private final Consumer<Direction> move;
    private final IntPredicate isKeyPressed;
    private final Map<Integer, Direction> keyBindings;

    public MoveInputHandler(Consumer<Direction> move, IntPredicate isKeyPressed,
                           Map<Integer, Direction> keyBindings) {
        this.move = move;
        this.isKeyPressed = isKeyPressed;
        this.keyBindings = new LinkedHashMap<>(keyBindings);
    }

    @Override
    public void handleInput() {
        for (Map.Entry<Integer, Direction> binding : keyBindings.entrySet()) {
            if (isKeyPressed.test(binding.getKey())) {
                move.accept(binding.getValue());
                return;
            }
        }
    }
}
