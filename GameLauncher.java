import javax.swing.*;
import java.awt.*;

public class GameLauncher extends JFrame {

    public GameLauncher() {
        super("Game Launcher");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLayout(new GridLayout(6, 1));

        JButton flappyBirdButton = new JButton("Jouer à Flappy Bird");
        JButton snakeGameButton = new JButton("Jouer à Snake Game");
        JButton plusOuMoinsButton = new JButton("Jouer à Plus ou Moins");
        JButton trueOrFalseButton = new JButton("Jouer à True or False");
        JButton penduButton = new JButton("Jouer au Pendu");
        JButton exitButton = new JButton("Quitter");

        flappyBirdButton.addActionListener(e -> FlappyBird.startGame());
        snakeGameButton.addActionListener(e -> SnakeGame.startGame());
        plusOuMoinsButton.addActionListener(e -> PlusOuMoins.startGame());
        trueOrFalseButton.addActionListener(e -> {
            TrueOrFalse trueOrFalse = new TrueOrFalse();
            trueOrFalse.startGame();
        });
        penduButton.addActionListener(e -> {
            Pendu pendu = new Pendu();
            pendu.startGame();
        });
        exitButton.addActionListener(e -> System.exit(0));

        add(flappyBirdButton);
        add(snakeGameButton);
        add(plusOuMoinsButton);
        add(trueOrFalseButton);
        add(penduButton);
        add(exitButton);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameLauncher::new);
    }
}
