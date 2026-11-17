package ru.mipt.bit.platformer.level;

import ru.mipt.bit.platformer.model.Tank;

import java.util.List;

/**
 * Type - interface, role - internal factory interface
 */
public interface TankGenerator {
    List<Tank> generate(Level level);
}
