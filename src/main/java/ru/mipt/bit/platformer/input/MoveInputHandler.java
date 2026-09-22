package ru.mipt.bit.platformer.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Field;
import ru.mipt.bit.platformer.model.Tank;

public class MoveInputHandler implements InputHandler {
    private final Tank tank;
    private final Field field;

    public MoveInputHandler(Tank tank, Field field) {
        this.tank = tank;
        this.field = field;
    }

    @Override
    public void handleInput() {
        if (isPressed(Input.Keys.UP, Input.Keys.W)) {
            tank.move(Direction.UP, field);
        } else if (isPressed(Input.Keys.LEFT, Input.Keys.A)) {
            tank.move(Direction.LEFT, field);
        } else if (isPressed(Input.Keys.DOWN, Input.Keys.S)) {
            tank.move(Direction.DOWN, field);
        } else if (isPressed(Input.Keys.RIGHT, Input.Keys.D)) {
            tank.move(Direction.RIGHT, field);
        }
    }

    private boolean isPressed(int firstKey, int secondKey) {
        return Gdx.input.isKeyPressed(firstKey)
                || Gdx.input.isKeyPressed(secondKey);
    }
}