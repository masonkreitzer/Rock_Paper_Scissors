/**
 * Cheat strategy - Computer always picks the move that beats the player's move.
 */
public class Cheat implements Strategy {

    @Override
    public String getMove(String playerMove) {
        return switch (playerMove) {
            case "R" -> "P"; // Paper beats Rock
            case "P" -> "S"; // Scissors beats Paper
            case "S" -> "R"; // Rock beats Scissors
            default -> "X";  // Invalid move
        };
    }
}
