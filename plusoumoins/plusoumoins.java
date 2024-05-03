import java.util.Random;
import javax.swing.*;

public class PlusOuMoins {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(PlusOuMoins::startGame);
    }

    public static void startGame() {
        new PlusOuMoins().runGame();
    }

    public void runGame() {
        Random ran = new Random();
        int high = 0;

        while (true) {
            int nb = ran.nextInt(1000);
            int nbCoups = 0;
            int nbEntre;

            String input;
            do {
                input = JOptionPane.showInputDialog("Entrez un nombre entre 0 et 1000:");

                if (input == null) {
                    break;
                }

                nbEntre = Integer.parseInt(input);

                if (nbEntre > 1000) {
                    JOptionPane.showMessageDialog(null, "Veuillez entrer un nombre entre 0 et 1000.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } while (input != null && nbEntre > 1000);

            nbEntre = Integer.parseInt(input);

            while (true) {
                nbCoups++;

                if (nbEntre > nb) {
                    input = JOptionPane.showInputDialog(null, "Plus petit", "Indice", JOptionPane.PLAIN_MESSAGE);
                } else if (nbEntre < nb) {
                    input = JOptionPane.showInputDialog(null, "Plus grand", "Indice", JOptionPane.PLAIN_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Tu as trouvé en " + nbCoups + " coups", "Résultat", JOptionPane.PLAIN_MESSAGE);

                    if (high == 0 || high > nbCoups) {
                        high = nbCoups;
                    }
                    break;
                }

                if (input == null) {
                    break;
                }

                nbEntre = Integer.parseInt(input);
            }

            int replay = JOptionPane.showConfirmDialog(null, "Rejouer ?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (replay != JOptionPane.YES_OPTION) {
                break;
            }
        }
    }
}
