package ru.mipt.bit.platformer.command;

public interface Command {
    Command NONE = () -> {};

    void execute();
}
