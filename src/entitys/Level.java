package entitys;

import java.util.ArrayList;
import java.util.List;

public class Level {
    private final List<Enemy> enemies;
    private final List<Platform> platforms;

    public Level() {
        enemies = new ArrayList<>();
        platforms = new ArrayList<>();
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public List<Platform> getPlatforms() {
        return platforms;
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    public void addPlatform(Platform platform) {
        platforms.add(platform);
    }
}