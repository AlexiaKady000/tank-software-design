package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.WorldContext;

public class TankInputHandler implements InputHandler {
    private final Tank tank;
    private final WorldContext world;
    private final ControlStrategy strategy;

    public TankInputHandler(Tank tank, WorldContext world, ControlStrategy strategy) {
        this.tank = tank;
        this.world = world;
        this.strategy = strategy;
    }

    @Override
    public boolean isActive() {
        return tank.isAlive() && world.contains(tank);
    }

    @Override
    public Command getCommand() {
        return isActive() ? strategy.getCommand(tank, world) : Command.NONE;
    }
}
