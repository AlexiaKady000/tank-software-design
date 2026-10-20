package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.Tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Level {
    private final int width;
    private final int height;
    private final GridPoint2 playerCoordinates;
    private final List<Tree> trees;

    public Level(int width, int height, GridPoint2 playerCoordinates, List<Tree> trees) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Level dimensions must be positive");
        }
        if (playerCoordinates.x < 0 || playerCoordinates.x >= width
                || playerCoordinates.y < 0 || playerCoordinates.y >= height) {
            throw new IllegalArgumentException("Player must be inside the level");
        }
        for (Tree tree : trees) {
            GridPoint2 coordinates = tree.getCoordinates();
            if (coordinates.x < 0 || coordinates.x >= width
                    || coordinates.y < 0 || coordinates.y >= height) {
                throw new IllegalArgumentException("Tree must be inside the level");
            }
            if (tree.blocks(playerCoordinates)) {
                throw new IllegalArgumentException("Player cannot start inside a tree");
            }
        }
        this.width = width;
        this.height = height;
        this.playerCoordinates = new GridPoint2(playerCoordinates);
        this.trees = Collections.unmodifiableList(new ArrayList<>(trees));
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public GridPoint2 getPlayerCoordinates() {
        return new GridPoint2(playerCoordinates);
    }

    public List<Tree> getTrees() {
        return trees;
    }
}
