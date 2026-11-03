package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.command.Command;

public interface InputHandler {
    Command getCommand();

    default boolean isActive() {
        return true;
    }
}
