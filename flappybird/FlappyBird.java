import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener, MouseListener {
    private static final int BOARD_WIDTH = 360;
    private static final int BOARD_HEIGHT = 640;
    private static final int BIRD_X = BOARD_WIDTH / 8;
    private static final int BIRD_Y = BOARD_WIDTH / 2;
    private static final int BIRD_WIDTH = 45;
    private static final int BIRD_HEIGHT = 35;
    private static final int PIPE_WIDTH = 64;
    private static final int PIPE_HEIGHT = 512;
    private static final double GRAVITY = 1;
    private static final int PIPE_SPEED = -4;
    private static final int JUMP_VELOCITY = -10;
    private static final int PIPE_INTERVAL = 1500;
    private static JFrame mainFrame;

    private Image backgroundImg;
    private Image birdImg;
    private Image topPipeImg;
    private Image bottomPipeImg;

    private Bird bird;
    private ArrayList<Pipe> pipes;

    private Timer gameLoop;
    private Timer placePipeTimer;
    private boolean gameOver = false;
    private double score = 0;

    public FlappyBird() {
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);

        loadImages();

        bird = new Bird(birdImg);
        pipes = new ArrayList<>();

        placePipeTimer = new Timer(PIPE_INTERVAL, e -> placePipes());
        placePipeTimer.start();

        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }

    private void loadImages() {
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
    }

    private void placePipes() {
        int randomPipeY = (int) (0 - PIPE_HEIGHT / 4 - Math.random() * (PIPE_HEIGHT / 2));
        int openingSpace = BOARD_HEIGHT / 4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + PIPE_HEIGHT + openingSpace;
        pipes.add(bottomPipe);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, BOARD_WIDTH, BOARD_HEIGHT, null);
        g.drawImage(birdImg, bird.x, bird.y, bird.width, bird.height, null);

        for (Pipe pipe : pipes) {
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over: " + (int) score, 10, 35);
        } else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    private void move() {
        bird.y += bird.velocityY;
        bird.velocityY += GRAVITY;

        if (bird.y < 0) {
            bird.y = 0;
        }

        for (Pipe pipe : pipes) {
            pipe.x += PIPE_SPEED;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                score += 0.5;
                pipe.passed = true;
            }

            if (collision(bird, pipe)) {
                gameOver = true;
                placePipeTimer.stop();
                gameLoop.stop();
                DBManager.saveScore((int) score);
                showGameOverDialog();
            }
        }

        if (bird.y > BOARD_HEIGHT) {
            gameOver = true;
            placePipeTimer.stop();
            gameLoop.stop();
            DBManager.saveScore((int) score);
            showGameOverDialog();
        }
    }

    private boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    private void showGameOverDialog() {
        ImageIcon icon = loadIcon("./flappybird.png", 80, 64);

        Object[] options = {"Replay", "Exit"};

        int response = JOptionPane.showOptionDialog(null, "Game Over! \nYour score: " + (int) score, "Game Over",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, icon, options, options[0]);

        if (response == 0) {
            resetGame();
        } else {
            displayMainMenu();
        }
    }

    private void resetGame() {
        bird.y = BIRD_Y;
        bird.velocityY = 0;
        pipes.clear();
        gameOver = false;
        score = 0;
        gameLoop.start();
        placePipeTimer.start();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            bird.velocityY = JUMP_VELOCITY;

            if (gameOver) {
                resetGame();
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        bird.velocityY = JUMP_VELOCITY;

        if (gameOver) {
            resetGame();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    private ImageIcon loadIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(getClass().getResource(path));
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }

    private static class Bird {
        int x = BIRD_X;
        int y = BIRD_Y;
        int width = BIRD_WIDTH;
        int height = BIRD_HEIGHT;
        int velocityY = 0;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    private static class Pipe {
        int x = BOARD_WIDTH;
        int y = 0;
        int width = PIPE_WIDTH;
        int height = PIPE_HEIGHT;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    public static void main(String[] args) {
        Object[] options = {"Quitter", "Voir le Scoreboard", "Jouer"};
        int response = JOptionPane.showOptionDialog(null, "Bienvenue dans Flappy Bird", "Menu Principal",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
    
        if (response == 0) {           
            System.exit(0);
        } else if (response == 1) {    
            showScoreboard();
        } else if (response == 2) {    
            startGame();
        }
    }

    public static void startGame() {
        mainFrame = new JFrame("Flappy Bird");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.getContentPane().add(new FlappyBird());
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private static void displayMainMenu() {
        if (mainFrame != null) {
            mainFrame.dispose();
            mainFrame = null; 
        }
    
        Object[] options = {"Quitter", "Voir le Scoreboard", "Jouer"};
        int response = JOptionPane.showOptionDialog(null, "Bienvenue dans Flappy Bird", "Menu Principal",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
    
        if (response == 0) {           
            System.exit(0);
        } else if (response == 1) {    
            showScoreboard();
        } else if (response == 2) {    
            startGame();
        }
    }
    
    
    private static void showScoreboard() {
        JFrame frame = new JFrame("Scoreboard");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JTextArea scoreArea = new JTextArea(10, 30);
        scoreArea.setEditable(false);
        DBManager.displayScores(scoreArea);
    
        JButton backButton = new JButton("Retour");
        backButton.addActionListener(e -> {
            frame.dispose();  
            displayMainMenu();
        });
    
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(scoreArea), BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.SOUTH);
    
        frame.setContentPane(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

class DBManager {
    private static final String DB_URL = "jdbc:sqlite:flappybird.db";

    static {
        initializeDatabase();
    }

    private static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS scores (id INTEGER PRIMARY KEY, score INTEGER NOT NULL)";
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveScore(int score) {
        String sql = "INSERT INTO scores(score) VALUES(?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, score);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void displayScores(JTextArea scoreArea) {
        String sql = "SELECT score FROM scores ORDER BY score DESC LIMIT 10";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                scoreArea.append("Score: " + rs.getInt("score") + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
