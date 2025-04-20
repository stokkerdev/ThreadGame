package entitys;

import java.awt.Graphics;
import java.awt.Color;

public class bullet {
    int x, y;
    int WIDTH = 10, HEIGHT = 5;
    int velX = 20;
    boolean active = true; // Indica si la bala está activa
    boolean lookingRight;

    public bullet(int x, int y , boolean lookingRight) {
        this.x = x;
        this.y = y ;
        this.lookingRight = lookingRight; // Guardar la dirección de la bala
    }

    public void move(int screenWidth) {
        if (!active) return; // Si la bala no está activa, no se mueve

        if (lookingRight) {
            velX = 20; // Velocidad de la bala hacia la derecha
        } else {
            velX = -20; // Velocidad de la bala hacia la izquierda
        }
        x += velX;

        // Si la bala se sale de la pantalla, se desactiva
        if (x > screenWidth  || x < 0) {
            active = false;
        }
    }

    public void checkCollision(Enemy enemy) {
        if (!active || !enemy.alive) return; // Verificar si la bala está activa y el enemigo está vivo
    
        // Verificar si la bala impacta al enemigo
        if (x < enemy.getX() + enemy.getWidth() &&
            x + WIDTH > enemy.getX() &&
            y < enemy.getY() + enemy.getHeight() &&
            y + HEIGHT > enemy.getY()) {
            active = false; // Desactivar la bala
            enemy.takeDamage(40); // Llamar a un método para que el enemigo reciba daño
        }
    }
    public void draw(Graphics g) {
        if (!active) return; // Si la bala no está activa, no se dibuja

        g.setColor(Color.RED);
        g.fillRect(x, y, WIDTH, HEIGHT);
    }

    public boolean isActive() {
        return active;
    }
}