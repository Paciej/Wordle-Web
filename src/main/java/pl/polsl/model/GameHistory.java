package pl.polsl.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a complete game session record.
 * <p>
 * This class stores information about a finished game including all attempts,
 * the result, timestamp, and the secret word.
 * </p>
 *
 * @author Maciej Porebski
 * @version 1.0
 */
public class GameHistory {
    
    /** The secret word for this game session. */
    private final String secretWord;
    
    /** List of all guess attempts made during this game. */
    private final List<GuessResult> attempts;
    
    /** Whether the game was won. */
    private final boolean won;
    
    /** Timestamp when the game was completed. */
    private final LocalDateTime timestamp;
    
    /**
     * Constructs a new GameHistory record.
     *
     * @param secretWord the secret word that was being guessed
     * @param attempts   the list of all attempts made
     * @param won        whether the player won the game
     */
    public GameHistory(String secretWord, List<GuessResult> attempts, boolean won) {
        this.secretWord = secretWord;
        this.attempts = new ArrayList<>(attempts); // Create copy to avoid external modification
        this.won = won;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Gets the secret word for this game.
     *
     * @return the secret word
     */
    public String getSecretWord() {
        return secretWord;
    }
    
    /**
     * Gets all attempts made during this game.
     *
     * @return list of guess results
     */
    public List<GuessResult> getAttempts() {
        return new ArrayList<>(attempts); // Return copy for immutability
    }
    
    /**
     * Checks if the game was won.
     *
     * @return true if won, false otherwise
     */
    public boolean isWon() {
        return won;
    }
    
    /**
     * Gets the timestamp when the game was completed.
     *
     * @return the completion timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    /**
     * Gets formatted timestamp string.
     *
     * @return formatted date and time string
     */
    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return timestamp.format(formatter);
    }
    
    /**
     * Gets the number of attempts made.
     *
     * @return number of attempts
     */
    public int getAttemptCount() {
        return attempts.size();
    }
}
