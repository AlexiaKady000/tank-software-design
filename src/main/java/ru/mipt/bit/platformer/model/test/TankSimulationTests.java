package ru.mipt.bit.platformer.model.test;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.input.InputController;
import ru.mipt.bit.platformer.input.MoveInputHandler;
import ru.mipt.bit.platformer.input.RandomControlStrategy;
import ru.mipt.bit.platformer.level.Level;
import ru.mipt.bit.platformer.level.RandomLevelLoader;
import ru.mipt.bit.platformer.level.RandomTankGenerator;
import ru.mipt.bit.platformer.model.Field;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TankSimulationTest {
    @Test
    void shouldPreserveOccupancyDuringLongSimulation() {
        for (int seed = 0; seed < 10; seed++) {
            Level level = new RandomLevelLoader(12, 8, 20, new Random(seed)).load();
            Field field = new Field(level.getWidth(), level.getHeight());
            for (Tree tree : level.getTrees()) {
                field.addObstacle(tree);
            }
            List<Tank> tanks = new ArrayList<>();
            tanks.add(new Tank(level.getPlayerCoordinates()));
            tanks.addAll(new RandomTankGenerator(10, new Random(seed)).generate(level));
            InputController controls = new InputController();
            for (int i = 0; i < tanks.size(); i++) {
                Tank tank = tanks.get(i);
                field.addObstacle(tank);
                controls.addHandler(new MoveInputHandler(tank, field,
                        new RandomControlStrategy(new Random(seed * 100 + i))));
            }
            boolean moved = false;
            for (int tick = 0; tick < 1000; tick++) {
                controls.handleInput();
                checkOccupancy(tanks, level);
                for (Tank tank : tanks) {
                    moved |= tank.isMoving();
                    tank.update(0.05f);
                }
                checkOccupancy(tanks, level);
            }
            assertTrue(moved);
        }
    }

    private void checkOccupancy(List<Tank> tanks, Level level) {
        Set<GridPoint2> occupied = new HashSet<>();
        for (Tank tank : tanks) {
            Set<GridPoint2> cells = new HashSet<>();
            cells.add(tank.getCoordinates());
            if (tank.isMoving()) {
                cells.add(tank.getDestinationCoordinates());
            }
            for (GridPoint2 cell : cells) {
                assertTrue(cell.x >= 0 && cell.x < level.getWidth());
                assertTrue(cell.y >= 0 && cell.y < level.getHeight());
                assertTrue(occupied.add(cell), "Tanks overlap at " + cell);
                for (Tree tree : level.getTrees()) {
                    assertFalse(tree.blocks(cell));
                }
            }
        }
    }
}
