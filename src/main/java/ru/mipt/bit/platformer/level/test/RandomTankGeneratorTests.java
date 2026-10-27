package ru.mipt.bit.platformer.level.test;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.mipt.bit.platformer.level.FileLevelLoader;
import ru.mipt.bit.platformer.level.Level;
import ru.mipt.bit.platformer.level.RandomLevelLoader;
import ru.mipt.bit.platformer.level.RandomTankGenerator;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomTankGeneratorTest {
    @TempDir
    Path directory;

    @Test
    void shouldGenerateTwoUniqueTanksOnFreeCells() {
        for (int seed = 0; seed < 100; seed++) {
            Level level = new RandomLevelLoader(10, 8, 20, new Random(seed)).load();
            List<Tank> tanks = new RandomTankGenerator(2, new Random(seed)).generate(level);
            assertEquals(2, tanks.size());
            Set<GridPoint2> positions = new HashSet<>();
            positions.add(level.getPlayerCoordinates());
            for (Tank tank : tanks) {
                GridPoint2 coordinates = tank.getCoordinates();
                assertTrue(positions.add(coordinates));
                assertTrue(coordinates.x >= 0 && coordinates.x < level.getWidth());
                assertTrue(coordinates.y >= 0 && coordinates.y < level.getHeight());
                assertFalse(tank.isMoving());
                for (Tree tree : level.getTrees()) {
                    assertFalse(tree.blocks(coordinates));
                }
            }
        }
    }

    @Test
    void shouldAddAiOnlyToEmptyCellsOfFileLevel() throws IOException {
        Path path = directory.resolve("level.txt");
        Files.write(path, Arrays.asList("T__", "TXT"), StandardCharsets.UTF_8);
        Level level = new FileLevelLoader(path).load();

        List<Tank> tanks = new RandomTankGenerator(2, new Random(1)).generate(level);
        Set<GridPoint2> positions = new HashSet<>();
        for (Tank tank : tanks) {
            positions.add(tank.getCoordinates());
        }

        assertEquals(new HashSet<>(Arrays.asList(new GridPoint2(1, 1), new GridPoint2(2, 1))), positions);
        assertEquals(new GridPoint2(1, 0), level.getPlayerCoordinates());
        assertEquals(3, level.getTrees().size());
    }

    @Test
    void shouldRejectNegativeCount() {
        assertThrows(IllegalArgumentException.class, () -> new RandomTankGenerator(-1, new Random()));
    }

    @Test
    void shouldRejectInsufficientSpaceInsteadOfOverlappingOrLooping() {
        Level level = new Level(2, 1, new GridPoint2(), Collections.emptyList());
        assertThrows(IllegalArgumentException.class,
                () -> new RandomTankGenerator(2, new Random()).generate(level));
    }

    @Test
    void shouldAccountForTreesWhenCheckingFreeSpace() {
        Level level = new Level(3, 1, new GridPoint2(),
                Collections.singletonList(new Tree(new GridPoint2(1, 0))));
        assertThrows(IllegalArgumentException.class,
                () -> new RandomTankGenerator(2, new Random()).generate(level));
    }

    @Test
    void shouldAllowZeroAiOnSingleCellLevel() {
        Level level = new Level(1, 1, new GridPoint2(), Collections.emptyList());
        assertTrue(new RandomTankGenerator(0, new Random()).generate(level).isEmpty());
    }

    @Test
    void shouldHonorConfiguredCountAndRandomSeed() {
        Level level = new Level(5, 5, new GridPoint2(), Collections.emptyList());
        List<Tank> first = new RandomTankGenerator(5, new Random(42)).generate(level);
        List<Tank> second = new RandomTankGenerator(5, new Random(42)).generate(level);
        assertEquals(5, first.size());
        for (int i = 0; i < first.size(); i++) {
            assertEquals(first.get(i).getCoordinates(), second.get(i).getCoordinates());
        }
    }

    @Test
    void shouldGenerateFreshTanksWithoutChangingLevel() {
        Level level = new Level(3, 2, new GridPoint2(), Collections.emptyList());
        RandomTankGenerator generator = new RandomTankGenerator(2, new Random(1));
        List<Tank> first = generator.generate(level);
        List<Tank> second = generator.generate(level);

        assertNotSame(first.get(0), second.get(0));
        assertTrue(level.getTrees().isEmpty());
        assertEquals(new GridPoint2(), level.getPlayerCoordinates());
    }
}
