package ru.mipt.bit.platformer.level.test;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.mipt.bit.platformer.level.FileLevelLoader;
import ru.mipt.bit.platformer.level.Level;
import ru.mipt.bit.platformer.level.RandomLevelLoader;
import ru.mipt.bit.platformer.model.Tree;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LevelLoaderTest {
    @TempDir
    Path directory;

    private Level read(String text) throws IOException {
        Path path = directory.resolve("level.txt");
        Files.write(path, text.getBytes(StandardCharsets.UTF_8));
        return new FileLevelLoader(path).load();
    }

    @Test
    void shouldReadExampleWithTopRowFirst() throws IOException {
        Level level = read("___T__T___\n__TT__TTTT\n_________T\nTTTT__T__T\n_____X____\n__________\n");

        assertEquals(10, level.getWidth());
        assertEquals(6, level.getHeight());
        assertEquals(new GridPoint2(5, 1), level.getPlayerCoordinates());
        assertEquals(15, level.getTrees().size());
        assertEquals(new GridPoint2(3, 5), level.getTrees().get(0).getCoordinates());
    }

    @Test
    void shouldReadWindowsLineEndingsAndBom() throws IOException {
        Level level = read("\uFEFFTX\r\n__\r\n");

        assertEquals(2, level.getHeight());
        assertEquals(new GridPoint2(1, 1), level.getPlayerCoordinates());
    }

    @Test
    void shouldRejectMalformedLevels() {
        for (String text : new String[] {"", "___", "XX", "X_\n_", "X?", "X_\n\n"}) {
            assertThrows(IllegalArgumentException.class, () -> read(text));
        }
    }

    @Test
    void shouldReportMissingFile() {
        assertThrows(IOException.class,
                () -> new FileLevelLoader(directory.resolve("missing.txt")).load());
    }

    @Test
    void shouldGenerateUniqueTreesAndFreePlayerCell() {
        for (int seed = 0; seed < 100; seed++) {
            Level level = new RandomLevelLoader(10, 8, 20, new Random(seed)).load();
            Set<GridPoint2> occupied = new HashSet<>();
            assertEquals(20, level.getTrees().size());
            for (Tree tree : level.getTrees()) {
                GridPoint2 coordinates = tree.getCoordinates();
                assertTrue(coordinates.x >= 0 && coordinates.x < 10);
                assertTrue(coordinates.y >= 0 && coordinates.y < 8);
                assertTrue(occupied.add(coordinates));
            }
            assertFalse(occupied.contains(level.getPlayerCoordinates()));
        }
    }

    @Test
    void shouldSupportSingleCellAndMaximumTreeCount() {
        Level single = new RandomLevelLoader(1, 1, 0, new Random(1)).load();
        assertEquals(new GridPoint2(0, 0), single.getPlayerCoordinates());
        assertTrue(single.getTrees().isEmpty());

        Level full = new RandomLevelLoader(2, 2, 3, new Random(1)).load();
        assertEquals(3, full.getTrees().size());
        for (Tree tree : full.getTrees()) {
            assertFalse(tree.blocks(full.getPlayerCoordinates()));
        }
    }

    @Test
    void shouldRejectInvalidRandomParameters() {
        assertThrows(IllegalArgumentException.class, () -> new RandomLevelLoader(0, 8, 0, new Random()));
        assertThrows(IllegalArgumentException.class, () -> new RandomLevelLoader(2, 2, -1, new Random()));
        assertThrows(IllegalArgumentException.class, () -> new RandomLevelLoader(2, 2, 4, new Random()));
        assertThrows(IllegalArgumentException.class,
                () -> new RandomLevelLoader(Integer.MAX_VALUE, 2, 0, new Random()));
    }
}
