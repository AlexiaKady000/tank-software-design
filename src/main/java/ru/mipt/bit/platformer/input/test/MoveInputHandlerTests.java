package ru.mipt.bit.platformer.input.test;

import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.input.MoveInputHandler;
import ru.mipt.bit.platformer.model.Direction;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoveInputHandlerTest {
    @Test
    void shouldUseCustomKeyBindings() {
        List<Direction> moves = new ArrayList<>();
        Map<Integer, Direction> bindings = new LinkedHashMap<>();
        bindings.put(42, Direction.DOWN);
        MoveInputHandler handler = new MoveInputHandler(moves::add, key -> key == 42, bindings);

        handler.handleInput();

        assertEquals(1, moves.size());
        assertEquals(Direction.DOWN, moves.get(0));
    }

    @Test
    void shouldMoveOnceUsingFirstPressedBinding() {
        List<Direction> moves = new ArrayList<>();
        Map<Integer, Direction> bindings = new LinkedHashMap<>();
        bindings.put(1, Direction.UP);
        bindings.put(2, Direction.LEFT);
        MoveInputHandler handler = new MoveInputHandler(moves::add, key -> true, bindings);
        bindings.clear();

        handler.handleInput();

        assertEquals(1, moves.size());
        assertEquals(Direction.UP, moves.get(0));
    }

    @Test
    void shouldNotMoveWhenNoKeyIsPressed() {
        List<Direction> moves = new ArrayList<>();
        Map<Integer, Direction> bindings = new LinkedHashMap<>();
        bindings.put(42, Direction.DOWN);
        MoveInputHandler handler = new MoveInputHandler(moves::add, key -> false, bindings);

        handler.handleInput();

        assertTrue(moves.isEmpty());
    }
}
