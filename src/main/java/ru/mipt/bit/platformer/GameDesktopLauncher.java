package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.graphics.FieldGraphics;
import ru.mipt.bit.platformer.graphics.TankGraphics;
import ru.mipt.bit.platformer.graphics.TreeGraphics;
import ru.mipt.bit.platformer.input.InputController;
import ru.mipt.bit.platformer.input.MoveInputHandler;
import ru.mipt.bit.platformer.model.Field;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

public class GameDesktopLauncher implements ApplicationListener {
    private Batch batch;
    private Field field;
    private Tank tank;
    private Tree tree;

    private FieldGraphics fieldGraphics;
    private TankGraphics tankGraphics;
    private TreeGraphics treeGraphics;
    private InputController inputController;

    @Override
    public void create() {
        batch = new SpriteBatch();
        field = new Field(10, 8);
        tank = new Tank(new GridPoint2(1, 1));
        tree = new Tree(new GridPoint2(1, 3));
        field.addTree(tree);
        fieldGraphics = new FieldGraphics(batch);
        tankGraphics = new TankGraphics(tank);
        treeGraphics = new TreeGraphics(tree, fieldGraphics.getGroundLayer());
        inputController = new InputController();
        inputController.addHandler(new MoveInputHandler(tank, field));
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
        float deltaTime = Gdx.graphics.getDeltaTime();
        inputController.handleInput();
        tank.update(deltaTime);
        tankGraphics.update(fieldGraphics.getTileMovement());
        fieldGraphics.render();
        batch.begin();
        tankGraphics.render(batch);
        treeGraphics.render(batch);
        batch.end();
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
        treeGraphics.dispose();
        tankGraphics.dispose();
        fieldGraphics.dispose();
        batch.dispose();
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        // level width: 10 tiles x 128px, height: 8 tiles x 128px
        config.setWindowedMode(1280, 1024);
        new Lwjgl3Application(new GameDesktopLauncher(), config);
    }
}