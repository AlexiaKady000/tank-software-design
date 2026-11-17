package ru.mipt.bit.platformer.command;

import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.WorldContext;

/**
 * Type - class, role - use case
 */
public class ShootCommand implements Command {
    private final Tank tank;
    private final WorldContext world;

    public ShootCommand(Tank tank, WorldContext world) {
        this.tank = tank;
        this.world = world;
    }

    @Override
    public void execute() {
        tank.shoot(world);
    }
}
