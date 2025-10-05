import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

/**
 * Main game window for Rock Paper Scissors.
 * Includes buttons, stats, and strategy-based computer opponent.
 */
public class RockPaperScissorsFrame extends JFrame {

    // GUI components
    private final JTextArea resultsArea;
    private final JTextField playerWinsField;
    private final JTextField computerWinsField;
    private final JTextField tiesField;

    // Game stats
    private int playerWins = 0;
    private int computerWins = 0;
    private int ties = 0;

    // Player move tracking for strategies
    private int rockCount = 0;
    private int paperCount = 0;
    private int scissorsCount = 0;
    private String lastPlayerMove = "";

    // External strategy objects
    private final Strategy cheatStrategy = new Cheat();
    private final Strategy randomStrategy = new RandomStrategy();

    private final Random rand = new Random();

    /**
     * Constructor that builds the GUI and sets up event handling.
     */
    public RockPaperScissorsFrame() {
        setTitle("Rock Paper Scissors Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ---- Button Panel ----
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Choose your move"));

// Load icons
        ImageIcon rockIcon = new ImageIcon(getClass().getResource("/Images/rock.png"));
        ImageIcon paperIcon = new ImageIcon(getClass().getResource("/Images/paper.png"));
        ImageIcon scissorsIcon = new ImageIcon(getClass().getResource("/Images/scissors.png"));
        ImageIcon quitIcon = new ImageIcon(getClass(). getResource("/Images/quit.png"));

// Create buttons with icons
        JButton rockButton = new JButton("Rock", rockIcon);
        JButton paperButton = new JButton("Paper", paperIcon);
        JButton scissorsButton = new JButton("Scissors", scissorsIcon);
        JButton quitButton = new JButton("Quit", quitIcon);

        buttonPanel.add(rockButton);
        buttonPanel.add(paperButton);
        buttonPanel.add(scissorsButton);
        buttonPanel.add(quitButton);

        add(buttonPanel, BorderLayout.NORTH);

        // ---- Stats Panel ----
        JPanel statsPanel = new JPanel(new GridLayout(3, 2));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Game Stats"));

        playerWinsField = new JTextField("0");
        computerWinsField = new JTextField("0");
        tiesField = new JTextField("0");

        playerWinsField.setEditable(false);
        computerWinsField.setEditable(false);
        tiesField.setEditable(false);

        statsPanel.add(new JLabel("Player Wins:"));
        statsPanel.add(playerWinsField);
        statsPanel.add(new JLabel("Computer Wins:"));
        statsPanel.add(computerWinsField);
        statsPanel.add(new JLabel("Ties:"));
        statsPanel.add(tiesField);

        add(statsPanel, BorderLayout.CENTER);

        // ---- Results Area ----
        resultsArea = new JTextArea(10, 35);
        resultsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultsArea);
        add(scrollPane, BorderLayout.SOUTH);

        // ---- Button Listeners ----
        ActionListener moveListener = e -> {
            String playerMove = "";
            if (e.getSource() == rockButton) playerMove = "R";
            else if (e.getSource() == paperButton) playerMove = "P";
            else if (e.getSource() == scissorsButton) playerMove = "S";

            if (!playerMove.isEmpty()) playRound(playerMove);
        };

        rockButton.addActionListener(moveListener);
        paperButton.addActionListener(moveListener);
        scissorsButton.addActionListener(moveListener);

        quitButton.addActionListener(e -> System.exit(0));

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Plays one round of the game with the given player move.
     */
    private void playRound(String playerMove) {
        updateMoveCounts(playerMove);

        int probability = rand.nextInt(100) + 1;
        Strategy computerStrategy;
        String strategyName;

        if (probability <= 10) {
            computerStrategy = cheatStrategy;
            strategyName = "Cheat";
        } else if (probability <= 30) {
            computerStrategy = new LeastUsed();
            strategyName = "Least Used";
        } else if (probability <= 50) {
            computerStrategy = new MostUsed();
            strategyName = "Most Used";
        } else if (probability <= 70) {
            computerStrategy = new LastUsed();
            strategyName = "Last Used";
        } else {
            computerStrategy = randomStrategy;
            strategyName = "Random";
        }

        String computerMove = computerStrategy.getMove(playerMove);
        String result = determineWinner(playerMove, computerMove);

        resultsArea.append(result + " (Computer: " + strategyName + ")\n");
        updateStats();
        lastPlayerMove = playerMove;
    }

    /**
     * Determines the winner based on player and computer moves.
     */
    private String determineWinner(String playerMove, String computerMove) {
        if (playerMove.equals(computerMove)) {
            ties++;
            return "It's a tie!";
        }

        return switch (playerMove + computerMove) {
            case "RS" -> { playerWins++; yield "Rock breaks Scissors. Player wins!"; }
            case "PR" -> { playerWins++; yield "Paper covers Rock. Player wins!"; }
            case "SP" -> { playerWins++; yield "Scissors cut Paper. Player wins!"; }
            case "SR" -> { computerWins++; yield "Rock breaks Scissors. Computer wins!"; }
            case "RP" -> { computerWins++; yield "Paper covers Rock. Computer wins!"; }
            case "PS" -> { computerWins++; yield "Scissors cut Paper. Computer wins!"; }
            default -> "Invalid move.";
        };
    }

    /**
     * Updates the player’s move counters for strategies.
     */
    private void updateMoveCounts(String move) {
        switch (move) {
            case "R" -> rockCount++;
            case "P" -> paperCount++;
            case "S" -> scissorsCount++;
        }
    }

    /**
     * Updates the stats panel fields.
     */
    private void updateStats() {
        playerWinsField.setText(String.valueOf(playerWins));
        computerWinsField.setText(String.valueOf(computerWins));
        tiesField.setText(String.valueOf(ties));
    }

    // ================= INNER STRATEGIES ================= //

    /**
     * Picks the symbol that beats the one the player used least often.
     */
    private class LeastUsed implements Strategy {
        @Override
        public String getMove(String playerMove) {
            int min = Math.min(rockCount, Math.min(paperCount, scissorsCount));
            if (min == rockCount) return "P"; // Beat Rock
            else if (min == paperCount) return "S"; // Beat Paper
            else return "R"; // Beat Scissors
        }
    }

    /**
     * Picks the symbol that beats the one the player used most often.
     */
    private class MostUsed implements Strategy {
        @Override
        public String getMove(String playerMove) {
            int max = Math.max(rockCount, Math.max(paperCount, scissorsCount));
            if (max == rockCount) return "P"; // Beat Rock
            else if (max == paperCount) return "S"; // Beat Paper
            else return "R"; // Beat Scissors
        }
    }

    /**
     * Picks the symbol that beats the player’s last used move.
     */
    private class LastUsed implements Strategy {
        @Override
        public String getMove(String playerMove) {
            if (lastPlayerMove.isEmpty()) return randomStrategy.getMove(playerMove);

            return switch (lastPlayerMove) {
                case "R" -> "P";
                case "P" -> "S";
                case "S" -> "R";
                default -> randomStrategy.getMove(playerMove);
            };
        }
    }
}
