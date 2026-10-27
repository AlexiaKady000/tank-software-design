package ru.mipt.bit.platformer.level.test;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.level.Level;
import ru.mipt.bit.platformer.model.Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LevelTest {
    @Test
    void shouldRejectInvalidDimensions() {
        for (int[] size : new int[][] {{0, 1}, {1, 0}, {-1, 1}, {1, -1}}) {
            assertThrows(IllegalArgumentException.class,
                    () -> new Level(size[0], size[1], new GridPoint2(), Collections.emptyList()));
        }
    }

    @Test
    void shouldRejectPlayerOutsideLevel() {
        for (GridPoint2 position : Arrays.asList(new GridPoint2(-1, 0), new GridPoint2(0, -1),
                new GridPoint2(2, 0), new GridPoint2(0, 2))) {
            assertThrows(IllegalArgumentException.class,
                    () -> new Level(2, 2, position, Collections.emptyList()));
        }
    }

    @Test
    void shouldRejectTreesOutsideLevel() {
        for (GridPoint2 position : Arrays.asList(new GridPoint2(-1, 0), new GridPoint2(0, -1),
                new GridPoint2(2, 0), new GridPoint2(0, 2))) {
            assertThrows(IllegalArgumentException.class, () -> new Level(2, 2, new GridPoint2(),
                    Collections.singletonList(new Tree(position))));
        }
    }

    @Test
    void shouldRejectTreeAtPlayerPosition() {
        assertThrows(IllegalArgumentException.class, () -> new Level(2, 2, new GridPoint2(),
                Collections.singletonList(new Tree(new GridPoint2()))));
    }

    @Test
    void shouldProtectLevelDataFromExternalChanges() {
        GridPoint2 player = new GridPoint2(1, 1);
        Tree tree = new Tree(new GridPoint2());
        List<Tree> trees = new ArrayList<>();
        trees.add(tree);
        Level level = new Level(3, 2, player, trees);
        player.set(5, 5);
        level.getPlayerCoordinates().set(6, 6);
        trees.clear();

        assertEquals(3, level.getWidth());
        assertEquals(2, level.getHeight());
        assertEquals(new GridPoint2(1, 1), level.getPlayerCoordinates());
        assertEquals(Collections.singletonList(tree), level.getTrees());
        assertThrows(UnsupportedOperationException.class, () -> level.getTrees().clear());
    }
}
