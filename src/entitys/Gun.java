package entitys;

import javax.swing.*;
import java.awt.*;

public class Gun {
    public int x, y;
    public int WIDTH = 20, HEIGHT = 20;
    public boolean derecha;
    private Image sprite;

    public Gun(int x, int y, boolean derecha) {
        this.x = x;
        this.y = y;
        this.derecha = derecha;
        String path = "src/assets/gun.png";
        this.sprite = new ImageIcon(path).getImage();
    }

    public void update(int x,int y, boolean lookingRight) {
        if (lookingRight){
            this.derecha = lookingRight; // si el jugador mira a la izquierda, la pistola también lo hace
            this.x  = x + 25;
            this.y  = y + 10; // la pistola se mueve con el jugador
        }else{
            this.derecha = lookingRight; // si el jugador mira a la izquierda, la pistola también lo hace
            this.x  = x - 18;
            this.y  = y + 10; // la pistola se mueve con el jugador
        }
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if (derecha) {
            g2d.drawImage(sprite, x, y, WIDTH, HEIGHT, null);
        } else {
            g2d.drawImage(sprite, x + WIDTH, y, -WIDTH, HEIGHT, null); // imagen volteada
        }
    }
    
    

    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }
}
