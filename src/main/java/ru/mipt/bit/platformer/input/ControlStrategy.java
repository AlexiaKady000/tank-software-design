package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.model.WorldContext;
import ru.mipt.bit.platformer.model.Tank;

public interface ControlStrategy {
    Command getCommand(Tank tank, WorldContext field);
}
