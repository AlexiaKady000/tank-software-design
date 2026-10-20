package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
import java.util.List;

public class Field implements MovementField {
    private final int width;
    private final int height;
    private final List<Obstacle> obstacles = new ArrayList<>();

    public Field(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void addObstacle(Obstacle obstacle) {
        obstacles.add(obstacle);
    }

    @Override
    public boolean canMoveTo(GridPoint2 coordinates) {
        if (!isInsideField(coordinates)) {
            return false;
        }

        for (Obstacle obstacle : obstacles) {
            if (obstacle.blocks(coordinates)) {
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