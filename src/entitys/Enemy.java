package entitys;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Enemy extends Thread {

    public boolean lookingRight = true; // Badnera para controlar la dirección del enemigo
    //public boolean active = false; // Bandera para controlar si el enemigo está activo
    public int x, y; // Posición del enemigo
    public final int WIDTH = 30, HEIGHT = 40; // Dimensiones del enemigo, 
    public int health = 100; // Salud del enemigo, se puede modificar a 20 para que mueran con una sola bala 
    public boolean alive = false; 


    private Player target;
    private Image sprite;
    private final List<bullet> projectiles = new ArrayList<>(); // Lista de proyectiles
    private final Random random = new Random();
    private Semaphore enemySemaphore;

    public Enemy(int x, int y, Player player, Semaphore enemySemaphore) {
        this.x = x;
        this.y = y;
        this.target = player;
        this.sprite = new ImageIcon("src/assets/enemy.png").getImage();
        this.enemySemaphore = enemySemaphore; // Guardar el semáforo
    }

   

    @Override
    public void run() {
        try {
            // Adquirir un permiso del semáforo antes de activarse
            enemySemaphore.acquire();
            alive = true; // Activar al enemigo después de adquirir el semáforo
            System.out.println("Enemy activated: " + this);

            while (alive && target.alive) {
                // Aplicar gravedad
                

                // Movimiento del enemigo
                if (x > target.x) {
                    x -= 1;
                    lookingRight = true;
                } else if (x < target.x) {
                    x += 1;
                    lookingRight = false;
                }

              

                // Disparar proyectiles en intervalos aleatorios
                if (random.nextInt(100) < 2) { // Probabilidad de disparar (ajustable)
                    shoot();
                }

                // Mover proyectiles y verificar colisiones con el jugador
                moveProjectiles();

                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Enemy interrupted: " + this);
        } finally {
            // Liberar el permiso del semáforo cuando el enemigo muera
            enemySemaphore.release();
            System.out.println("Enemy deactivated: " + this);
        }
    }

    


    public void shoot() {
        // Crear un nuevo proyectil y agregarlo a la lista
        projectiles.add(new bullet(x + WIDTH / 2, y + 15, !lookingRight));
    }

    public void moveProjectiles() {
        for (int i = 0; i < projectiles.size(); i++) {
            bullet b = projectiles.get(i);
            b.move(800); // Supongamos que el ancho de la pantalla es 800

            // Verificar colisión con el jugador
            if (b.x < target.x + target.WIDTH &&
                b.x + b.WIDTH > target.x &&
                b.y < target.y + target.HEIGHT &&
                b.y + b.HEIGHT > target.y) {
                target.takeDamage(10); // Hacer daño al jugador
                b.active = false; // Desactivar el proyectil
            }

            // Eliminar proyectiles inactivos
            if (!b.isActive()) {
                projectiles.remove(i);
                i--;
            }
        }
    }


    public void draw(Graphics g) {
        if (!alive) return; // No dibujar si el enemigo no está activo o está muerto

        Graphics2D g2d = (Graphics2D) g;

        if (lookingRight) {
            g2d.drawImage(sprite, x, y, WIDTH, HEIGHT, null);
        } else {
            g2d.drawImage(sprite, x + WIDTH, y, -WIDTH, HEIGHT, null); // Imagen volteada
        }

        // Dibujar proyectiles
        for (bullet b : projectiles) {
            b.draw(g);
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }
    
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
            alive = false;
            System.out.println("Enemy has died!");
            this.interrupt(); // Interrumpir el hilo del enemigo
        }
    }

}