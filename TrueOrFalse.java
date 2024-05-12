import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TrueOrFalse extends JFrame {

    private static TrueOrFalse instance;

    private JLabel questionLabel;
    private JButton trueButton;
    private JButton falseButton;
    private JLabel resultLabel;
    private JLabel scoreLabel;

    private String[] questions = {
        "Nico est gay ?", 
        "Les gauchistes sont des gens avec un IQ > 200", 
        "Caporal est un Gros bot", 
        "quentinou est trop chou", 
        "Louis 14 etait le plus puissant roi de france",
        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAa"
    };

    private boolean[] answers = {
        true, 
        false, 
        true, 
        true, 
        true,
        false
    };

    private List<Integer> askedQuestions;
    private Random random;
    private int score;
    private int totalQuestions;
    private int currentQuestionIndex;

    public TrueOrFalse() {
        super("True or False");
        initializeComponents();
        random = new Random();
        askedQuestions = new ArrayList<>();
        score = 0;
        totalQuestions = questions.length;
    }

    public static TrueOrFalse getInstance() {
        if (instance == null) {
            instance = new TrueOrFalse();
        }
        return instance;
    }

    private void initializeComponents() {
        questionLabel = new JLabel();
        trueButton = new JButton("Vrai");
        falseButton = new JButton("Faux");
        resultLabel = new JLabel("");
        scoreLabel = new JLabel("Score: 0/" + totalQuestions);

        setLayout(new GridLayout(5, 1));
        add(questionLabel);
        add(trueButton);
        add(falseButton);
        add(resultLabel);
        add(scoreLabel);

        trueButton.addActionListener(e -> checkAnswer(true));
        falseButton.addActionListener(e -> checkAnswer(false));

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 250);
    }

    private void askNewQuestion() {
        if (askedQuestions.size() >= totalQuestions) {
            finishGame();
            return;
        }
        int newQuestionIndex;
        do {
            newQuestionIndex = random.nextInt(questions.length);
        } while (askedQuestions.contains(newQuestionIndex));
        currentQuestionIndex = newQuestionIndex;
        questionLabel.setText(questions[currentQuestionIndex]);
        askedQuestions.add(currentQuestionIndex);
    }

    private void checkAnswer(boolean userAnswer) {
        boolean correctAnswer = answers[currentQuestionIndex];
        if (userAnswer == correctAnswer) {
            resultLabel.setText("Correct !");
            score++;
        } else {
            resultLabel.setText("Incorrect...");
        }
        scoreLabel.setText("Score: " + score + "/" + totalQuestions);
        askNewQuestion();
    }

    private void finishGame() {
        JOptionPane.showMessageDialog(this, "Félicitations ! Vous avez terminé le jeu avec un score de " + score + "/" + totalQuestions + " !");
        setVisible(false);
        GameLauncher.getInstance().setVisible(true); 
        resetInstance();
    }

    private void resetInstance() {
        instance = null;
    }

    public void startGame() {
        setVisible(true);
        askNewQuestion();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TrueOrFalse.getInstance().startGame();
        });
    }
}
