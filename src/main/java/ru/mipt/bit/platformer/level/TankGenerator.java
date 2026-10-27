package ru.mipt.bit.platformer.level;

import ru.mipt.bit.platformer.model.Tank;

import java.util.List;

public interface TankGenerator {
    List<Tank> generate(Level level);
}
