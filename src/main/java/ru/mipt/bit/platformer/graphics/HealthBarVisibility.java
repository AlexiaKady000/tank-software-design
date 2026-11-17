package ru.mipt.bit.platformer.graphics;

/**
 * Type - class, role - presentation state
 */
public class HealthBarVisibility {
    private boolean visible;

    public boolean isVisible() {
        return visible;
    }

    public void toggle() {
        visible = !visible;
    }
}
