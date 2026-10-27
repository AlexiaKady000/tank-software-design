package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.model.MovementField;
import ru.mipt.bit.platformer.model.Tank;

public interface ControlStrategy {
    Command getCommand(Tank tank, MovementField field);
}
