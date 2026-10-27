package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.Field;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomTankGenerator implements TankGenerator {
    private final int tankCount;
    private final Random random;

    public RandomTankGenerator(int tankCount, Random random) {
        if (tankCount < 0) {
            throw new IllegalArgumentException("Tank count must not be negative");
        }
        this.tankCount = tankCount;
        this.random = random;
    }

    @Override
    public List<Tank> generate(Level level) {
        Field field = new Field(level.getWidth(), level.getHeight());
        for (Tree tree : level.getTrees()) {
            field.addObstacle(tree);
        }
        GridPoint2 playerCoordinates = level.getPlayerCoordinates();
        List<GridPoint2> cells = new ArrayList<>();
        for (int y = 0; y < level.getHeight(); y++) {
            for (int x = 0; x < level.getWidth(); x++) {
                GridPoint2 coordinates = new GridPoint2(x, y);
                if (!coordinates.equals(playerCoordinates) && field.canMoveTo(coordinates)) {
                    cells.add(coordinates);
                }
            }
        }
        if (tankCount > cells.size()) {
            throw new IllegalArgumentException("Not enough free cells for AI tanks");
        }
        Collections.shuffle(cells, random);
        List<Tank> tanks = new ArrayList<>();
        for (int i = 0; i < tankCount; i++) {
            tanks.add(new Tank(cells.get(i)));
        }
        return tanks;
    }
}
