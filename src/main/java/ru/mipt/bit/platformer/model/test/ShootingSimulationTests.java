package ru.mipt.bit.platformer.model.test;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.input.InputController;
import ru.mipt.bit.platformer.input.RandomControlStrategy;
import ru.mipt.bit.platformer.input.TankInputHandler;
import ru.mipt.bit.platformer.level.Level;
import ru.mipt.bit.platformer.level.RandomLevelLoader;
import ru.mipt.bit.platformer.level.RandomTankGenerator;
import ru.mipt.bit.platformer.model.Bullet;
import ru.mipt.bit.platformer.model.Field;
import ru.mipt.bit.platformer.model.GameObject;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShootingSimulationTest {
    @Test
    void shouldKeepWorldConsistentDuringMovementShootingAndDeaths() {
        for (int seed = 0; seed < 5; seed++) {
            Level level = new RandomLevelLoader(12, 8, 20, new Random(seed)).load();
            Field world = new Field(12, 8);
            InputController input = new InputController();
            for (Tree tree : level.getTrees()) {
                world.addObject(tree);
            }
            int index = 0;
            for (Tank tank : new RandomTankGenerator(8, new Random(seed)).generate(level)) {
                world.addObject(tank);
                input.addHandler(new TankInputHandler(tank, world,
                        new RandomControlStrategy(new Random(seed * 100 + index++))));
            }
            for (int tick = 0; tick < 1000; tick++) {
                for (Command command : input.collectCommands()) {
                    command.execute();
                }
                world.live(tick % 50 == 0 ? 0.5f : 0.025f);
                Set<GridPoint2> occupied = new HashSet<>();
                for (GameObject object : world.getObjects()) {
                    if (object instanceof Tank) {
                        Tank tank = (Tank) object;
                        assertTrue(tank.isAlive());
                        assertTrue(world.isInsideField(tank.getCoordinates()));
                        assertTrue(occupied.add(tank.getCoordinates()));
                        if (tank.isMoving()) {
                            assertTrue(world.isInsideField(tank.getDestinationCoordinates()));
                            assertTrue(occupied.add(tank.getDestinationCoordinates()));
                        }
                        for (Tree tree : level.getTrees()) {
                            assertFalse(tree.blocks(tank.getCoordinates()));
                            assertFalse(tree.blocks(tank.getDestinationCoordinates()));
                        }
                    }
                    if (object instanceof Bullet) {
                        assertTrue(world.isInsideField(((Bullet) object).getCoordinates()));
                    }
                }
            }
        }
    }
}
