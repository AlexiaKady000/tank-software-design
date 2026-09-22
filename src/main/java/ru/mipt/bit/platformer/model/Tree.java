package ru.mipt.bit.platformer.model;
import com.badlogic.gdx.math.GridPoint2;

public class Tree {
    private final GridPoint2 coordinates;

    public Tree(GridPoint2 coordinates) {
        this.coordinates = new GridPoint2(coordinates);
    }

    public GridPoint2 getCoordinates() {
        return new GridPoint2(coordinates);
    }
}