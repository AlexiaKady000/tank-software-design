package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
import java.util.List;

public class Field {
    private final int width;
    private final int height;
    private final List<Tree> trees = new ArrayList<>();

    public Field(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void addTree(Tree tree) {
        trees.add(tree);
    }

    public boolean canMoveTo(GridPoint2 coordinates) {
        if (!isInsideField(coordinates)) {
            return false;
        }

        for (Tree tree : trees) {
            if (tree.getCoordinates().equals(coordinates)) {
                return false;
            }
        }

        return true;
    }

    private boolean isInsideField(GridPoint2 coordinates) {
        return coordinates.x >= 0
                && coordinates.x < width
                && coordinates.y >= 0
                && coordinates.y < height;
    }
}