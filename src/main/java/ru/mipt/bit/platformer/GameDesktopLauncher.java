package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.util.TileMovement;

import static com.badlogic.gdx.Input.Keys.*;
import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.math.MathUtils.isEqual;
import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

public class GameDesktopLauncher implements ApplicationListener {
    private Batch batch;
    private Field field;
    private Tank tank;
    private Tree tree;

    @Override
    public void create() {
        batch = new SpriteBatch();
        // load level
        field = new Field(batch);
        // create player tank and set its initial position
        tank = new Tank("images/tank_blue.png", new GridPoint2(1, 1));
        field.placeTank(tank);
        // create tree obstacle
        tree = new Tree("images/greenTree.png", new GridPoint2(1, 3));
        field.addTree(tree);
    }

    @Override
    public void render() {
        // clear the screen
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
        // get time passed since the last render
        float deltaTime = Gdx.graphics.getDeltaTime();
        // process player input
        handleInput();
        // update player position
        tank.update(deltaTime, field.getTileMovement());
        // render each tile of the level
        field.render();
        // start recording all drawing commands
        batch.begin();
        // render player
        tank.render(batch);
        // render tree obstacle
        tree.render(batch);
        // submit all drawing requests
        batch.end();
    }

    private void handleInput() {
        for (Direction direction : Direction.values()) {
            if (direction.isPressed()) {
                tank.move(direction, field);
                break;
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        // do not react to window resizing
    }

    @Override
    public void pause() {
        // game doesn't get paused
    }

    @Override
    public void resume() {
        // game doesn't get paused
    }

    @Override
    public void dispose() {
        tree.dispose();
        tank.dispose();
        field.dispose();
        batch.dispose();
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        // level width: 10 tiles x 128px, height: 8 tiles x 128px
        config.setWindowedMode(1280, 1024);
        new Lwjgl3Application(new GameDesktopLauncher(), config);
    }
}
