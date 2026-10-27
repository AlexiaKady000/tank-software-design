package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.model.MovementField;
import ru.mipt.bit.platformer.model.Tank;

public class MoveInputHandler implements InputHandler {
    private final Tank tank;
    private final MovementField field;
    private final ControlStrategy strategy;

    public MoveInputHandler(Tank tank, MovementField field, ControlStrategy strategy) {
        this.tank = tank;
        this.field = field;
        this.strategy = strategy;
    }

    @Override
    public void handleInput() {
        if (!tank.isMoving()) {
            strategy.getCommand(tank, field).execute();
        }
    }
}
