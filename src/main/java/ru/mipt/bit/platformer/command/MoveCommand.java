package ru.mipt.bit.platformer.command;

import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.MovementField;
import ru.mipt.bit.platformer.model.Tank;

public class MoveCommand implements Command {
    private final Tank tank;
    private final Direction direction;
    private final MovementField field;

    public MoveCommand(Tank tank, Direction direction, MovementField field) {
        this.tank = tank;
        this.direction = direction;
        this.field = field;
    }

    @Override
    public void execute() {
        tank.move(direction, field);
    }
}
