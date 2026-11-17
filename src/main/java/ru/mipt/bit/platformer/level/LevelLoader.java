package ru.mipt.bit.platformer.level;

import java.io.IOException;

/**
 * Type - interface, role - outbound port
 */
public interface LevelLoader {
    Level load() throws IOException;
}
