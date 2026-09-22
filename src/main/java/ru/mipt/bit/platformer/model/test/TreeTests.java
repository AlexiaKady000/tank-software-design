package ru.mipt.bit.platformer.model.test;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TreeTest {
    @Test
    void shouldStoreCoordinates() {
        Tree tree = new Tree(new GridPoint2(1, 3));

        assertEquals(new GridPoint2(1, 3), tree.getCoordinates());
    }
}