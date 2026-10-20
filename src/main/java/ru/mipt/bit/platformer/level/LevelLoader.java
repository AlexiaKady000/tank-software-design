package ru.mipt.bit.platformer.level;

import java.io.IOException;

public interface LevelLoader {
    Level load() throws IOException;
}
