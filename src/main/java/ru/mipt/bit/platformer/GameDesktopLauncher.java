package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.graphics.FieldGraphics;
import ru.mipt.bit.platformer.graphics.Renderable;
import ru.mipt.bit.platformer.graphics.TankGraphics;
import ru.mipt.bit.platformer.graphics.TreeGraphics;
import ru.mipt.bit.platformer.input.ControlStrategy;
import ru.mipt.bit.platformer.input.InputController;
import ru.mipt.bit.platformer.input.KeyboardControlStrategy;
import ru.mipt.bit.platformer.input.MoveInputHandler;
import ru.mipt.bit.platformer.input.RandomControlStrategy;
import ru.mipt.bit.platformer.level.FileLevelLoader;
import ru.mipt.bit.platformer.level.Level;
import ru.mipt.bit.platformer.level.LevelLoader;
import ru.mipt.bit.platformer.level.RandomLevelLoader;
import ru.mipt.bit.platformer.level.RandomTankGenerator;
import ru.mipt.bit.platformer.level.TankGenerator;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Field;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;
import ru.mipt.bit.platformer.model.Updatable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

public class GameDesktopLauncher implements ApplicationListener {
    private final LevelLoader levelLoader;
    private final TankGenerator tankGenerator;
    private Batch batch;
    private FieldGraphics fieldGraphics;
    private final List<Updatable> updatables = new ArrayList<>();
    private final List<Renderable> renderables = new ArrayList<>();
    private final List<Disposable> disposables = new ArrayList<>();
    private InputController inputController;

    public GameDesktopLauncher() {
        this(new RandomLevelLoader(10, 8, 20, new Random()));
    }

    public GameDesktopLauncher(LevelLoader levelLoader) {
        this(levelLoader, new RandomTankGenerator(2, new Random()));
    }

    public GameDesktopLauncher(LevelLoader levelLoader, TankGenerator tankGenerator) {
        this.levelLoader = levelLoader;
        this.tankGenerator = tankGenerator;
    }

    @Override
    public void create() {
        Level level;
        try {
            level = levelLoader.load();
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read level", e);
        }
        Tank player = new Tank(level.getPlayerCoordinates(), 0.4f);
        List<Tank> tanks = new ArrayList<>();
        tanks.add(player);
        tanks.addAll(tankGenerator.generate(level));
        batch = new SpriteBatch();
        Field field = new Field(level.getWidth(), level.getHeight());
        fieldGraphics = new FieldGraphics(batch, "level.tmx", Interpolation.smooth,
                level.getWidth(), level.getHeight());
        for (Tree tree : level.getTrees()) {
            field.addObstacle(tree);
        }
        inputController = new InputController();
        Map<Integer, Direction> keyBindings = new LinkedHashMap<>();
        keyBindings.put(Input.Keys.UP, Direction.UP);
        keyBindings.put(Input.Keys.W, Direction.UP);
        keyBindings.put(Input.Keys.LEFT, Direction.LEFT);
        keyBindings.put(Input.Keys.A, Direction.LEFT);
        keyBindings.put(Input.Keys.DOWN, Direction.DOWN);
        keyBindings.put(Input.Keys.S, Direction.DOWN);
        keyBindings.put(Input.Keys.RIGHT, Direction.RIGHT);
        keyBindings.put(Input.Keys.D, Direction.RIGHT);
        ControlStrategy keyboard = new KeyboardControlStrategy(Gdx.input::isKeyPressed, keyBindings);
        for (Tank tank : tanks) {
            field.addObstacle(tank);
            ControlStrategy strategy = tank == player
                    ? keyboard : new RandomControlStrategy(new Random());
            inputController.addHandler(new MoveInputHandler(tank, field, strategy));
            TankGraphics tankGraphics = new TankGraphics(tank, "images/tank_blue.png");
            updatables.add(tank);
            updatables.add(deltaTime -> tankGraphics.update(fieldGraphics.getTileMovement()));
            renderables.add(tankGraphics);
            disposables.add(tankGraphics);
        }
        for (Tree tree : level.getTrees()) {
            TreeGraphics treeGraphics = new TreeGraphics(tree, fieldGraphics.getGroundLayer(), "images/greenTree.png");
            renderables.add(treeGraphics);
            disposables.add(treeGraphics);
        }
        disposables.add(fieldGraphics);
        disposables.add(batch);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
        float deltaTime = Gdx.graphics.getDeltaTime();
        inputController.handleInput();
        for (Updatable updatable : updatables) {
            updatable.update(deltaTime);
        }
        fieldGraphics.render();
        batch.begin();
        for (Renderable renderable : renderables) {
            renderable.render(batch);
        }
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
        for (Disposable disposable : disposables) {
            disposable.dispose();
        }
    }

    public static void main(String[] args) throws IOException {
        LevelLoader loader = args.length == 0
                ? new RandomLevelLoader(10, 8, 20, new Random())
                : new FileLevelLoader(Paths.get(args[0]));
        Level level = loader.load();
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(level.getWidth() * 128, level.getHeight() * 128);
        new Lwjgl3Application(new GameDesktopLauncher(() -> level), config);
    }
}