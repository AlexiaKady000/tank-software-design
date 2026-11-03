package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.command.Command;

import java.util.ArrayList;
import java.util.List;

public class InputController {
    private final List<InputHandler> handlers = new ArrayList<>();

    public void addHandler(InputHandler handler) {
        handlers.add(handler);
    }

    public List<Command> collectCommands() {
        handlers.removeIf(handler -> !handler.isActive());
        List<Command> commands = new ArrayList<>();
        for (InputHandler handler : handlers) {
            commands.add(handler.getCommand());
        }
        return commands;
    }
}
