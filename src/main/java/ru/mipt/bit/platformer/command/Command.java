package ru.mipt.bit.platformer.command;

/**
 * Type - interface, role - inbound port
 */
public interface Command {
    Command NONE = () -> {};

    void execute();
}
