package pl.polsl.model;

import lombok.Getter;

/**
 * Enumeration representing the status of a letter in a guess.
 * <p>
 * This enum defines three possible states for each letter when comparing
 * a guessed word with the secret word in the Wordle game.
 * </p>
 *
 * @author Maciej Porebski
 * @version 1.0
 */

@Getter
public enum LetterStatus {
    
    /**
     * The letter is in the correct position.
     */
    CORRECT('+', "Correct position", "#4CAF50"),
    
    /**
     * The letter exists in the word but in wrong position.
     */
    WRONG_POSITION('?', "Wrong position", "#FF9800"),
    
    /**
     * The letter does not exist in the word.
     */
    NOT_PRESENT('-', "Not in word", "#9E9E9E");
    
    /** Symbol representing this status. */
    private final char symbol;
    
    /** Human-readable description of this status. */
    private final String description;
    
    /** Color code for visual representation. */
    private final String colorCode;
    
    /**
     * Constructs a LetterStatus with specified properties.
     *
     * @param symbol      the character symbol for this status
     * @param description human-readable description
     * @param colorCode   hex color code for UI display
     */
    LetterStatus(char symbol, String description, String colorCode) {
        this.symbol = symbol;
        this.description = description;
        this.colorCode = colorCode;
    }
        
    /**
     * Finds LetterStatus by its symbol character.
     *
     * @param symbol the symbol to search for
     * @return the matching LetterStatus
     * @throws IllegalArgumentException if symbol is not recognized
     */
    public static LetterStatus fromSymbol(char symbol) {
        for (LetterStatus status : values()) {
            if (status.symbol == symbol) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown symbol: " + symbol);
    }
    
    @Override
    public String toString() {
        return String.valueOf(symbol);
    }
}
