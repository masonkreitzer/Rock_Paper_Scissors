import java.util.Random;

/**
 * Random strategy - Computer picks Rock, Paper, or Scissors randomly.
 */
public class RandomStrategy implements Strategy {

    private final Random random = new Random();

    @Override
    public String getMove(String playerMove) {
        int pick = random.nextInt(3);
        return switch (pick) {
            case 0 -> "R";
            case 1 -> "P";
            case 2 -> "S";
            default -> "X";
        };
    }
}
