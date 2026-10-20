package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.Tree;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileLevelLoader implements LevelLoader {
    private final Path path;

    public FileLevelLoader(Path path) {
        this.path = path;
    }

    @Override
    public Level load() throws IOException {
        List<String> rows = Files.readAllLines(path, StandardCharsets.UTF_8);
        if (!rows.isEmpty() && rows.get(0).startsWith("\uFEFF")) {
            rows.set(0, rows.get(0).substring(1));
        }
        if (rows.isEmpty() || rows.get(0).isEmpty()) {
            throw new IllegalArgumentException("Level must not be empty");
        }
        int width = rows.get(0).length();
        int height = rows.size();
        GridPoint2 playerCoordinates = null;
        List<Tree> trees = new ArrayList<>();
        for (int row = 0; row < height; row++) {
            String line = rows.get(row);
            if (line.length() != width) {
                throw new IllegalArgumentException("Level rows must have equal width");
            }
            for (int x = 0; x < width; x++) {
                GridPoint2 coordinates = new GridPoint2(x, height - row - 1);
                switch (line.charAt(x)) {
                    case 'T':
                        trees.add(new Tree(coordinates));
                        break;
                    case 'X':
                        if (playerCoordinates != null) {
                            throw new IllegalArgumentException("Level must have exactly one player");
                        }
                        playerCoordinates = coordinates;
                        break;
                    case '_':
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown level symbol at row "
                                + (row + 1) + ", column " + (x + 1));
                }
            }
        }
        if (playerCoordinates == null) {
            throw new IllegalArgumentException("Level must have exactly one player");
        }
        return new Level(width, height, playerCoordinates, trees);
    }
}
