package pl.polsl.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.polsl.dao.WordDao;

/**
 * The {@code WordValidator} class verifies whether a given word
 * meets the required game constraints, including dictionary validation.
 * 
 * <p>This class supports Dependency Injection for easier unit testing.</p>
 * 
 * @author Maciej Porebski
 * @version 1.2
 */
public class WordValidator {

    /** Data Access Object used for dictionary lookups. */
    private final WordDao wordDao;

    /**
     * Default constructor used by the application.
     * Initializes a new instance of WordDao that connects to the real database.
     */
    public WordValidator() {
        this.wordDao = new WordDao();
    }

    /**
     * Constructor for testing purposes (Dependency Injection).
     * Allows injecting a mock or specific WordDao instance.
     *
     * @param wordDao the WordDao instance to use for validation
     */
    public WordValidator(WordDao wordDao) {
        this.wordDao = wordDao;
    }
    
    /**
     * Validates the given word according to game rules and dictionary existence.
     *
     * @param word the word to validate
     * @return {@code true} if the word is valid and exists in DB
     * @throws InvalidGuessException if the word is invalid or not found in DB
     */
    public boolean isValid(String word) throws InvalidGuessException {
        
        if (word == null) {
            throw new InvalidGuessException("A word cannot be null");
        }
        
        if (word.length() != 5) {
            throw new InvalidGuessException("A word must contain 5 letters");
        }
                
        Pattern p = Pattern.compile("[^a-z]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(word);
        boolean hasSpecial = m.find();
        
        if (hasSpecial) {
            throw new InvalidGuessException("A word cannot contain any special characters");
        }

        // Use the injected/initialized DAO instance instead of creating 'new' one here
        if (!this.wordDao.wordExists(word)) {
            throw new InvalidGuessException("The word '" + word + "' does not exist in the dictionary.");
        }
        
        return true;
    }
}
