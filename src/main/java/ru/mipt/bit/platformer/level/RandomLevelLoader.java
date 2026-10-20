package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.Tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomLevelLoader implements LevelLoader {
    private final int width;
    private final int height;
    private final int treeCount;
    private final Random random;

    public RandomLevelLoader(int width, int height, int treeCount, Random random) {
        if (width <= 0 || height <= 0 || (long) width * height > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Invalid level dimensions");
        }
        if (treeCount < 0 || treeCount >= width * height) {
            throw new IllegalArgumentException("Tree count must leave room for the player");
        }
        this.width = width;
        this.height = height;
        this.treeCount = treeCount;
        this.random = random;
    }

    @Override
    public Level load() {
        List<GridPoint2> cells = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells.add(new GridPoint2(x, y));
            }
        }
        Collections.shuffle(cells, random);
        List<Tree> trees = new ArrayList<>();
        for (int i = 0; i < treeCount; i++) {
            trees.add(new Tree(cells.get(i)));
        }
        return new Level(width, height, cells.get(treeCount), trees);
    }
}
