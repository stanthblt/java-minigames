import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Pendu extends JFrame implements ActionListener {
    private Connection connection;
    private String motMystere;
    private StringBuilder motCourant;
    private int tentativesRestantes;
    private JLabel motLabel, tentativesLabel, lettresIncorrectesLabel, scoreLabel;
    private JTextField lettreField;
    private JButton propositionBtn, ajouterMotBtn;
    private List<Character> lettresIncorrectes;
    private int score;

    public Pendu() {
        setTitle("Jeu du Pendu");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        connecterBaseDonnees();

        JPanel panel = new JPanel(new GridLayout(6, 1));

        motLabel = new JLabel("", JLabel.CENTER);
        panel.add(motLabel);

        tentativesLabel = new JLabel("", JLabel.CENTER);
        panel.add(tentativesLabel);

        lettresIncorrectesLabel = new JLabel("", JLabel.CENTER);
        panel.add(lettresIncorrectesLabel);

        lettreField = new JTextField();
        panel.add(lettreField);

        propositionBtn = new JButton("Proposer");
        propositionBtn.addActionListener(this);
        panel.add(propositionBtn);

        ajouterMotBtn = new JButton("Ajouter Mot");
        ajouterMotBtn.addActionListener(this);
        panel.add(ajouterMotBtn);

        scoreLabel = new JLabel("", JLabel.CENTER);
        panel.add(scoreLabel);

        add(panel);
        setVisible(true);

        initialiserJeu();
    }

    private void connecterBaseDonnees() {
        try {
            String url = "jdbc:mysql://localhost:3306/nom_DB";
            String username = "ethan";
            String password = "ethan";
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Impossible de se connecter à la base de données.");
            System.exit(1);
        }
    }

    private void initialiserJeu() {
        choisirMotMystere();
        motCourant = new StringBuilder("-".repeat(motMystere.length()));
        tentativesRestantes = 7;
        score = 0;
        lettresIncorrectes = new ArrayList<>();

        afficherMot();
        afficherTentatives();
        afficherLettresIncorrectes();
        afficherScore();
    }

    private void choisirMotMystere() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT mot FROM mots");
            List<String> mots = new ArrayList<>();
            while (resultSet.next()) {
                mots.add(resultSet.getString("mot"));
            }
            if (mots.isEmpty()) {
                int choix = JOptionPane.showConfirmDialog(this, "Il n'y a aucun mot disponible. Voulez-vous ajouter un mot maintenant ?", "Aucun mot disponible", JOptionPane.YES_NO_OPTION);
                if (choix == JOptionPane.YES_OPTION) {
                    ajouterMot();
                } else {
                    System.exit(0); 
                }
            } else {
                Random random = new Random();
                motMystere = mots.get(random.nextInt(mots.size()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void afficherMot() {
        motLabel.setText(motCourant.toString());
    }

    private void afficherTentatives() {
        tentativesLabel.setText("Tentatives restantes : " + tentativesRestantes);
    }

    private void afficherLettresIncorrectes() {
        StringBuilder lettresIncorrectesStr = new StringBuilder("Lettres incorrectes : ");
        for (char lettre : lettresIncorrectes) {
            lettresIncorrectesStr.append(lettre).append(" ");
        }
        lettresIncorrectesLabel.setText(lettresIncorrectesStr.toString());
    }

    private void afficherScore() {
        scoreLabel.setText("Score : " + score);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == propositionBtn) {
            proposerLettre();
        } else if (e.getSource() == ajouterMotBtn) {
            ajouterMot();
        }
    }

    private void proposerLettre() {
        String lettre = lettreField.getText().toUpperCase();
        lettreField.setText("");

        if (lettre.length() != 1 || !Character.isLetter(lettre.charAt(0))) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir une seule lettre.");
            return;
        }

        char lettreChar = lettre.charAt(0);
        boolean lettreTrouvee = false;
        for (int i = 0; i < motMystere.length(); i++) {
            if (motMystere.charAt(i) == lettreChar) {
                motCourant.setCharAt(i, lettreChar);
                lettreTrouvee = true;
            }
        }

        if (!lettreTrouvee) {
            tentativesRestantes--;
            lettresIncorrectes.add(lettreChar);
            if (tentativesRestantes == 0) {
                JOptionPane.showMessageDialog(this, "Vous avez perdu! Le mot mystère était : " + motMystere);
                reset();
                return;

            }
            System.out.println(motMystere);

        }

        afficherMot();
        afficherTentatives();
        afficherLettresIncorrectes();

        if (motCourant.indexOf("-") == -1) {
            JOptionPane.showMessageDialog(this, "Félicitations! Vous avez trouvé le mot mystère : " + motMystere);
            score += tentativesRestantes * 10;
            afficherScore();
            reset();
        }
    }

    private void ajouterMot() {
        String mot = JOptionPane.showInputDialog(this, "Entrez un nouveau mot :").toUpperCase();
        if (mot != null && !mot.isEmpty()) {
            try {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO mots (mot) VALUES (?)");
                statement.setString(1, mot);
                statement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Le mot a été ajouté avec succès.");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Une erreur est survenue lors de l'ajout du mot.");
            }
        }
    }

    private void reset() {
        initialiserJeu();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Pendu::new);
    }
}




