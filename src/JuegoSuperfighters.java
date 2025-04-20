import entitys.Player;
import entitys.Enemy;
import entitys.bullet;
import entitys.Platform;
import entitys.Level;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class JuegoSuperfighters extends JPanel implements KeyListener, ActionListener {
   
    private boolean gameOver = false;
    private Player player;
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<bullet> bullets = new ArrayList<>();
    private final List<Level> levels = new ArrayList<>();
    private int currentLevelIndex = 0;
    private Timer gameTimer;
    private long lastShotTime = 0;
    private final int SHOOT_DELAY = 500; // Retraso entre disparos en milisegundos
    private Semaphore enemySemaphore;
    private Semaphore finalSemaphore;



    public JuegoSuperfighters() {
        enemySemaphore = new Semaphore(2); // Limitar a 2 enemigos activos a la vez
        finalSemaphore = new Semaphore(6); // Semáforo para el hilo principal

        JFrame frame = new JFrame("Mini Superfighters");
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.addKeyListener(this);
        frame.setVisible(true);

        player = new Player(100, 300);

        // Crear niveles
        createLevels();

        // Cargar el primer nivel
        loadLevel(0);

        gameTimer = new Timer(20, this);
        gameTimer.start();
    }

    private void createLevels() {
        // Nivel 1
        Level level1 = new Level();

        level1.addPlatform(new Platform(200, 250, 100, 20));

        level1.addEnemy(new Enemy(600, 300, player,enemySemaphore));
       // level1.addEnemy(new Enemy(200, 300, player,enemySemaphore));
        //level1.addEnemy(new Enemy(700, 200, player,enemySemaphore));
        //level1.addEnemy(new Enemy(700, 100, player,enemySemaphore));


        levels.add(level1);

        // Nivel 2
        Level level2 = new Level();

        level2.addPlatform(new Platform(300, 200, 150, 20));
        level2.addPlatform(new Platform(500, 150, 100, 20));
        level2.addPlatform(new Platform(700, 100, 150, 20));
        level2.addPlatform(new Platform(82, 250, 50, 20));



        level2.addEnemy(new Enemy(500, 50, player,enemySemaphore));
        level2.addEnemy(new Enemy(650, 250, player,enemySemaphore));
        level2.addEnemy(new Enemy(750, 300, player,enemySemaphore));
        level2.addEnemy(new Enemy(400, 100, player,enemySemaphore));
        level2.addEnemy(new Enemy(750, 300, player,enemySemaphore));
        level2.addEnemy(new Enemy(750, 200, player,enemySemaphore));
        level2.addEnemy(new Enemy(750, 50, player,enemySemaphore));
        level2.addEnemy(new Enemy(500, 50, player,enemySemaphore));
        
        levels.add(level2);


        Level level3 = new Level();

        level3.addPlatform(new Platform(200, 250, 100, 20));
        level3.addPlatform(new Platform(400, 200, 150, 20));    
        level3.addPlatform(new Platform(600, 150, 100, 20));
        level3.addPlatform(new Platform(800, 100, 150, 20));    
        level3.addPlatform(new Platform(82, 250, 50, 20));

        level3.addEnemy(new Enemy(600, 300, player,finalSemaphore));
        level3.addEnemy(new Enemy(200, 300, player,finalSemaphore));
        level3.addEnemy(new Enemy(700, 200, player,finalSemaphore));
        level3.addEnemy(new Enemy(700, 100, player,finalSemaphore));
        level3.addEnemy(new Enemy(700, 300, player,finalSemaphore));
        level3.addEnemy(new Enemy(700, 200, player,finalSemaphore));
        level3.addEnemy(new Enemy(700, 100, player,finalSemaphore));
        level3.addEnemy(new Enemy(700, 50, player,finalSemaphore));
        level3.addEnemy(new Enemy(700, 50, player,finalSemaphore));
        level3.addEnemy(new Enemy(700, 50, player,finalSemaphore));

        levels.add(level3);
    }

    private void loadLevel(int levelIndex) {
        if (levelIndex >= levels.size()) {
            System.out.println("No more levels! You win!");
            gameOver = true;
            return;
        }
    
        currentLevelIndex = levelIndex;
        Level level = levels.get(levelIndex);
    
        // Reiniciar enemigos y balas
        enemies.clear();
        bullets.clear();
    
        // Crear nuevos enemigos para el nivel actual
        for (Enemy enemy : level.getEnemies()) {

            Enemy newEnemy;
            if (levelIndex != 2) {
                newEnemy = new Enemy(enemy.x, enemy.y, player, enemySemaphore);
            } else {
                newEnemy = new Enemy(enemy.x, enemy.y, player, finalSemaphore);
            }
            enemies.add(newEnemy);
            newEnemy.start(); // Iniciar el nuevo hilo del enemigo
        }
    
        // Asignar plataformas al jugador
        player.setPlatforms(level.getPlatforms());
    
        repaint();
    }

    @Override
public void actionPerformed(ActionEvent e) {
    if (!player.alive) {
        triggerGameOver();
        return;
    }

    // Obtener las plataformas del nivel actual
    Level currentLevel = levels.get(currentLevelIndex);
    List<Platform> platforms = currentLevel.getPlatforms();

    // Mover al jugador y verificar colisiones con plataformas
    player.move(platforms);

    // Mover balas y verificar colisiones
    for (int i = 0; i < bullets.size(); i++) {
        bullet b = bullets.get(i);
        b.move(getWidth());

        // Verificar colisiones con enemigos
        for (Enemy enemy : enemies) {
            b.checkCollision(enemy);
        }

        // Eliminar balas inactivas
        if (!b.isActive()) {
            bullets.remove(i);
            i--;
        }
    }

    // Verificar si todos los enemigos han sido derrotados
    boolean allEnemiesDefeated = true;
    for (Enemy enemy : enemies) {
        if (enemy.alive) {
            allEnemiesDefeated = false;
            break;
        }
    }

    if (allEnemiesDefeated) {
        loadLevel(currentLevelIndex + 1); // Cargar el siguiente nivel
    }

    repaint();
}

    public void triggerGameOver() {
        gameOver = true;
        gameTimer.stop(); // Detener el temporizador del juego
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gameOver) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("GAME OVER", getWidth() / 2 - 150, getHeight() / 2);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Press R to Restart", getWidth() / 2 - 100, getHeight() / 2 + 50);
            return;
        }

        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.GRAY);
        g.fillRect(0, 340, getWidth(), 160);

        // Dibujar plataformas
        Level currentLevel = levels.get(currentLevelIndex);
        for (Platform platform : currentLevel.getPlatforms()) {
            platform.draw(g);
        }

        player.draw(g);
        player.gun.draw(g);

        for (Enemy enemy : enemies) {
            if (enemy.alive) {
                enemy.draw(g);
            }
        }

        // Dibujar balas
        for (bullet b : bullets) {
            b.draw(g);
        }

        g.setColor(Color.WHITE);
        g.drawString("Health: " + player.health, 10, 20);
    }

    public void restartGame() {
        gameOver = false;
        player = new Player(100, 300); // Reiniciar al jugador
        enemies.clear(); // Limpiar la lista de enemigos
        bullets.clear(); // Limpiar la lista de balas
    
        // Crear nuevos enemigos y cargar el primer nivel
        loadLevel(0);
    
        gameTimer.start(); // Reiniciar el temporizador del juego
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (gameOver && key == KeyEvent.VK_R) {
            restartGame();
        }

        if (!gameOver) {
            if (key == KeyEvent.VK_A) {
                player.velX = -5;
                player.lookingRight = false;
            } else if (key == KeyEvent.VK_D) {
                player.velX = 5;
                player.lookingRight = true;
            } else if (key == KeyEvent.VK_W && !player.jumping) {
                player.jumping = true;
                player.velY = -15;
            } else if (key == KeyEvent.VK_SPACE) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastShotTime >= SHOOT_DELAY) {
                    bullets.add(new bullet(player.x + player.WIDTH / 2, player.y + player.HEIGHT / 2, player.lookingRight));
                    lastShotTime = currentTime;
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_D) player.velX = 0;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        new JuegoSuperfighters();
    }
}