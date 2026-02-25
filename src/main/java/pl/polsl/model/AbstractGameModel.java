package pl.polsl.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 * Abstract base class defining the common state and operations for a word guessing game model.
 * <p>
 * This class stores shared game data such as the secret word, current attempt index,
 * maximum number of attempts and a list of guess results. Concrete game implementations
 * should extend this class and provide specific game logic.
 * </p>
 *
 * @author Maciej Porebski
 * @version 1.0
 */
@Getter
public abstract class AbstractGameModel {
    
    /** Maximum number of allowed attempts. */
    protected final int maxAttempts;
    
    /** The number of attempts made so far. */
    protected int currentAttempt;
    
    /** The word a user needs to guess. */
    protected String secretWord;
    
    /** The length of the secret word. */
    private final int wordLength;
    
    /** The boolean state that indicates if the user has guessed the secret word. */
    protected boolean gameWon;
    
    /** List storing all guess results with their feedback for the current game. */
    protected final List<GuessResult> results;
    
    /**
     * Constructs an AbstractGameModel with specified maximum attempts and secret word.
     * <p>
     * Initializes the game state with zero attempts, empty results list,
     * and sets the game as not won.
     * </p>
     * 
     * @param maxAttempts the maximum number of attempts allowed in the game
     * @param secretWord  the word that the player needs to guess
     */
    public AbstractGameModel(int maxAttempts, String secretWord) {
        this.maxAttempts = maxAttempts;
        this.currentAttempt = 0;
        this.secretWord = secretWord;
        this.wordLength = secretWord.length();
        this.gameWon = false;
        this.results = new ArrayList<>();
    }
    
    /**
     * Checks whether the player still has remaining attempts.
     *
     * @return {@code true} if the number of attempts made is less than the maximum allowed, {@code false} otherwise
     */
    public boolean hasAttemptsLeft() {
        return currentAttempt < maxAttempts;
    }

    /**
     * Checks if the player's guess matches the secret word exactly.
     *
     * @param guess the word guessed by the player
     * @return {@code true} if the guess matches the secret word, {@code false} otherwise
     */
    public abstract boolean isCorrect(String guess);
    
    /**
     * Checks the player's guess against the secret word and generates feedback.
     * <p>
     * This method evaluates each letter of the guess and returns feedback indicating:
     * </p>
     * <ul>
     *   <li>'+' indicates the letter is in the correct position</li>
     *   <li>'?' indicates the letter is in the secret word but in a different position</li>
     *   <li>'-' indicates the letter is not in the secret word</li>
     * </ul>
     * 
     * @param guess the word guessed by the player
     * @return a string representing the feedback for the guess
     * @throws InvalidGuessException if the player attempts to guess after reaching the maximum number of tries
     */
    public abstract String checkGuess(String guess) throws InvalidGuessException;
    
    /**
     * Resets the game state to start a new game.
     * <p>
     * Clears the results list, resets the attempt counter, and sets the game as not won.
     * The secret word remains unchanged unless overridden by concrete implementations.
     * </p>
     */
    public abstract void restart();
    
    /**
     * Saves the current game session to the game history.
     * <p>
     * This method should be called when a game ends (either by winning or running out of attempts)
     * to preserve the game data for historical tracking and statistics.
     * </p>
     */
    public abstract void saveCurrentGameToHistory();

    /**
     * Gets the history of all completed games.
     * <p>
     * Returns an immutable view of the game history to prevent external modification
     * of historical records.
     * </p>
     *
     * @return unmodifiable list of completed game records
     */
    public abstract List<GameHistory> getGameHistory();

}

