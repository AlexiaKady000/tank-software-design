package ru.mipt.bit.platformer.model.test;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.model.Tree;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TreeTest {
    @Test
    void shouldStoreCoordinates() {
        Tree tree = new Tree(new GridPoint2(1, 3));

        assertEquals(new GridPoint2(1, 3), tree.getCoordinates());
    }

    @Test
    void shouldBlockOnlyOwnCellAndCopyCoordinates() {
        GridPoint2 position = new GridPoint2(1, 3);
        Tree tree = new Tree(position);
        position.set(4, 4);
        tree.getCoordinates().set(5, 5);

        assertTrue(tree.blocks(new GridPoint2(1, 3)));
        assertFalse(tree.blocks(new GridPoint2(1, 2)));
    }

}
