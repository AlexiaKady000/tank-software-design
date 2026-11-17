package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.utils.Disposable;

/**
 * Type - interface, role - internal rendering interface
 */
public interface ObjectGraphics extends Renderable, Disposable {
    void update();
}
