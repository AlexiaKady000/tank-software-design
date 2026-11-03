package ru.mipt.bit.platformer.command;

import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.WorldContext;
import ru.mipt.bit.platformer.model.Tank;

public class MoveCommand implements Command {
    private final Tank tank;
    private final Direction direction;
    private final WorldContext field;

    public MoveCommand(Tank tank, Direction direction, WorldContext field) {
        this.tank = tank;
        this.direction = direction;
        this.field = field;
    }

    @Override
    public void execute() {
        if (field.contains(tank)) {
            tank.move(direction, field);
            field.resolveCollisions();
        }
    }
}
