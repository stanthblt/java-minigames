import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private class Tile {
        int x;
        int y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }  

    int boardWidth;
    int boardHeight;
    int tileSize = 25;
    
    Tile snakeHead;
    ArrayList<Tile> snakeBody;

    Tile food;
    Random random;

    int velocityX;
    int velocityY;
    Timer gameLoop;

    boolean gameOver = false;
    boolean gameStarted = false; 

    SnakeGame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        addKeyListener(this);
        setFocusable(true);

        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<Tile>();

        food = new Tile(10, 10);
        random = new Random();
        placeFood();

        velocityX = 1;
        velocityY = 0;

        gameLoop = new Timer(115, this);
        gameLoop.start();
    }   

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(0, 74, 42));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(new Color(7, 43, 32)); 
        for(int i = 0; i < boardWidth/tileSize; i++) {
            for(int j = 0; j < boardHeight/tileSize; j++) {
                if ((i + j) % 2 == 0) {
                    g.fillRect(i*tileSize, j*tileSize, tileSize, tileSize);
                }
            }
        }

        draw(g);
    }

    public void draw(Graphics g) {
        g.setColor(Color.red); 
        g.fill3DRect(food.x*tileSize, food.y*tileSize, tileSize, tileSize, true);

        g.setColor(new Color(138, 43, 226)); 
        g.fill3DRect(snakeHead.x*tileSize, snakeHead.y*tileSize, tileSize, tileSize, true);
        
        for (int i = 0; i < snakeBody.size(); i++) {
            Tile snakePart = snakeBody.get(i);
            g.fill3DRect(snakePart.x*tileSize, snakePart.y*tileSize, tileSize, tileSize, true);
        }

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        if (gameOver) {
            g.setColor(Color.red);
            g.drawString("Game Over: " + String.valueOf(snakeBody.size()), tileSize - 16, tileSize);
            restartGame();
        }
        else {
            g.drawString("Score: " + String.valueOf(snakeBody.size()), tileSize - 16, tileSize);
        }
    }

    public void placeFood(){
        food.x = random.nextInt((boardWidth - 2 * tileSize) / tileSize) + 1; 
        food.y = random.nextInt((boardHeight - 2 * tileSize) / tileSize) + 1; 
    }

    public void move() {
        if (snakeHead.x <= 0 || snakeHead.x >= (boardWidth / tileSize) - 1 ||
            snakeHead.y <= 0 || snakeHead.y >= (boardHeight / tileSize) - 1 ||
            collisionWithBorder() || collisionWithBody()) {
            gameOver = true;
            return;
        }

        if (collision(snakeHead, food)) {
            snakeBody.add(new Tile(food.x, food.y));
            placeFood();
        }

        for (int i = snakeBody.size()-1; i >= 0; i--) {
            Tile snakePart = snakeBody.get(i);
            if (i == 0) { 
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            }
            else {
                Tile prevSnakePart = snakeBody.get(i-1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;
    }

    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    public boolean collisionWithBorder() {
        return snakeHead.x == 0 || snakeHead.x == (boardWidth / tileSize) - 1 ||
               snakeHead.y == 0 || snakeHead.y == (boardHeight / tileSize) - 1;
    }
    
    public boolean collisionWithBody() {
        for (Tile bodyPart : snakeBody) {
            if (collision(snakeHead, bodyPart)) {
                return true;
            }
        }
        return false;
    }

    public void restartGame() {
        gameLoop.stop();
        int result = JOptionPane.showConfirmDialog(this, "Game Over. Your Score: " + snakeBody.size() + "\nDo you want to play again?", "Game Over", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            gameOver = false;
            snakeHead.x = 5;
            snakeHead.y = 5;
            snakeBody.clear();
            placeFood();
            velocityX = 1;
            velocityY = 0;
            gameLoop.start();
        } else {
            System.exit(0);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) { 
        if (gameStarted) {
            move();
            repaint();
        }
    }  

    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameStarted) {
            gameStarted = true;
        }
        
        if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
            velocityX = 0;
            velocityY = -1;
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1) {
            velocityX = 0;
            velocityY = 1;
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1) {
            velocityX = -1;
            velocityY = 0;
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1) {
            velocityX = 1;
            velocityY = 0;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
