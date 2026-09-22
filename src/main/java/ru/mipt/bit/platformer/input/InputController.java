package ru.mipt.bit.platformer.input;

import java.util.ArrayList;
import java.util.List;

public class InputController {
    private final List<InputHandler> handlers = new ArrayList<>();

    public void addHandler(InputHandler handler) {
        handlers.add(handler);
    }

    public void handleInput() {
        for (InputHandler handler : handlers) {
            handler.handleInput();
        }
    }
}