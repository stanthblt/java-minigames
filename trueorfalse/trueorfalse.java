import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class trueorfalse extends JFrame {

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

    public trueorfalse() {
        super("True or False");

        
        random = new Random();
        askedQuestions = new ArrayList<>();
        score = 0;
        totalQuestions = questions.length;

        
        questionLabel = new JLabel();
        trueButton = new JButton("Vrais");
        falseButton = new JButton("Faux");
        resultLabel = new JLabel("");
        scoreLabel = new JLabel("Score: 0/" + totalQuestions);

        
        setLayout(new GridLayout(5, 1));
        
        
        add(questionLabel);
        add(trueButton);
        add(falseButton);
        add(resultLabel);
        add(scoreLabel);

        
        trueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAnswer(true);
            }
        });

        falseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAnswer(false);
            }
        });

        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setVisible(true);

        
        askNewQuestion();
    }

    private void askNewQuestion() {
       
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

        
        if (askedQuestions.size() == totalQuestions) {
            JOptionPane.showMessageDialog(this, "Félicitations ! Vous avez terminé le jeu avec un score de " + score + "/" + totalQuestions + " !");
            System.exit(0);
        } else {
            
            askNewQuestion();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new trueorfalse();
            }
        });
    }
}
