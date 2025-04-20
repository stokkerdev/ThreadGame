package entitys;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Player extends Thread {
    public boolean lookingRight = true;
    public boolean alive = true;
    public int x, y;
    public int velX = 0, velY = 0;
    public final int WIDTH = 30, HEIGHT = 40;
    public boolean jumping = false;
    public int health = 100;
    public final int gravity = 1;
    private int currentGroundY;
    public Gun gun;
    private List<Platform> platforms; // Lista de plataformas

    private Image sprite;

    public Player(int x, int y) {
        this.currentGroundY = 300; // Suelo inicial
        this.x = x;
        this.y = y;
        this.gun = new Gun(x, y, lookingRight); // Inicializa la pistola
        this.sprite = new ImageIcon("src/assets/player.png").getImage(); // Ruta relativa
    }

    public void setPlatforms(List<Platform> platforms) {
        this.platforms = platforms; // Asignar la lista de plataformas
    }

    @Override
    public void run() {
        while (alive) {
            if (platforms != null) {
                move(platforms); // Pasar la lista de plataformas al método move
            }
            try {
                Thread.sleep(30); // Controlar la velocidad del hilo
            } catch (InterruptedException e) {
                break; // Salir del bucle si el hilo es interrumpido
            }
        }
        System.out.println("Player thread has stopped.");
    }

    public void move(List<Platform> platforms) {
        x += velX;

        // Aplicar gravedad
        velY += gravity;
        y += velY;

        // Verificar colisiones con plataformas
        boolean onPlatform = false;
        for (Platform platform : platforms) {
            if (isCollidingWithPlatform(platform)) {
                onPlatform = true;
                y = platform.getY() - HEIGHT; // Ajustar la posición del jugador para que quede sobre la plataforma
                velY = 0; // Detener la caída
                jumping = false; // Permitir que el jugador vuelva a saltar
                break;
            }
        }

        // Si no está sobre ninguna plataforma, verificar si está en el suelo
        if (!onPlatform && y >= currentGroundY) {
            y = currentGroundY;
            velY = 0;
            jumping = false;
        }

        // Actualizar la posición de la pistola
        this.gun.update(this.x, this.y, this.lookingRight);
    }

    private boolean isCollidingWithPlatform(Platform platform) {
        // Verificar si el jugador está tocando la plataforma desde arriba
        return x + WIDTH > platform.getX() &&
               x < platform.getX() + platform.getWidth() &&
               y + HEIGHT >= platform.getY() &&
               y + HEIGHT <= platform.getY() + 10; // Margen para detectar colisión desde arriba
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
            alive = false;
            System.out.println("Player has died!");
            this.interrupt(); // Interrumpir el hilo del jugador
        }
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        if (lookingRight) {
            g2d.drawImage(sprite, x, y, WIDTH, HEIGHT, null);
        } else {
            g2d.drawImage(sprite, x + WIDTH, y, -WIDTH, HEIGHT, null); // Imagen volteada
        }
    }
}