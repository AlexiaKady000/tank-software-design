package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.command.Command;

/**
 * Type - interface, role - internal interface
 */
public interface InputHandler {
    Command getCommand();

    default boolean isActive() {
        return true;
    }
}
