/**
 * Strategy interface for computer move algorithms.
 */
public interface Strategy {
    /**
     * Returns the computer's move ("R", "P", or "S") based on the player's move.
     * @param playerMove The player's chosen move.
     * @return The computer's move.
     */
    String getMove(String playerMove);
}
