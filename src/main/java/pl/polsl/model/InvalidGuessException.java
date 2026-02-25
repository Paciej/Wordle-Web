package pl.polsl.model;

/**
 * The {@code InvalidGuessException} class represents an exception
 * that is thrown when a player makes an invalid guess during the game.
 * <p>
 * This exception is typically used in the {@link GameModel} to signal
 * that the player has attempted to guess after exceeding the maximum
 * number of allowed attempts or performed another invalid game action.
 * </p>
 *
 * <p>It extends the standard {@link Exception} class.</p>
 *
 * @author Maciej Porebski
 * @version 1.0
 */
public class InvalidGuessException extends Exception {
    
    /**
     * Constructs a new {@code InvalidGuessException} with the specified detail message.
     *
     * @param message a descriptive message explaining the reason for the exception
     */
    public InvalidGuessException(String message) {
        super(message);
    }
}
