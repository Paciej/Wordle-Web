package pl.polsl.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pl.polsl.dao.WordDao;
import pl.polsl.dao.GameSessionDao;
import lombok.Getter;
import lombok.ToString;
import pl.polsl.entities.Word;
/**
 * The {@code GameModel} class represents the core game logic of the 5x5 word guessing game.
 * <p>
 * It stores the secret word, keeps track of the user's attempts and feedback, 
 * and provides methods to check guesses, validate the game state, and retrieve information.
 * </p>
 *
 * <p>This class is part of the <b>Model</b> component in the MVC pattern and contains no input/output operations.</p>
 * 
 * @author Maciej Porebski
 * @version 1.0
 */
@Getter
@ToString
public class GameModel extends AbstractGameModel {
    
    /** List storing all completed games. */
    private final List<GameHistory> gameHistory;
        
    /**
     * Constructs a new {@code GameModel} with the specified secret word.
     * 
     * @param secretWord the word that the player needs to guess
     */
    public GameModel(String secretWord) {
        super(5, secretWord);
        this.gameHistory = new ArrayList<>();
    }
    
    private void addResults(String guess, LetterStatus[] feedback) {
        results.add(new GuessResult(guess, feedback));
    }
    
/**
 * Generates feedback for a guess using LetterStatus enum.
 *
 * @param guess the guessed word
 * @return array of LetterStatus for each letter
 */
private LetterStatus[] generateFeedback(String guess) {
    LetterStatus[] feedback = new LetterStatus[this.getWordLength()];
    boolean[] usedInSecret = new boolean[this.getWordLength()];
    
    // First pass: mark correct positions
    for (int i = 0; i < this.getWordLength(); i++) {
        if (guess.charAt(i) == secretWord.charAt(i)) {
            feedback[i] = LetterStatus.CORRECT;
            usedInSecret[i] = true;
        }
    }
    
    // Second pass: mark wrong positions
    for (int i = 0; i < this.getWordLength(); i++) {
        if (feedback[i] == null) {
            boolean found = false;
            for (int j = 0; j < this.getWordLength(); j++) {
                if (!usedInSecret[j] && guess.charAt(i) == secretWord.charAt(j)) {
                    feedback[i] = LetterStatus.WRONG_POSITION;
                    usedInSecret[j] = true;
                    found = true;
                    break;
                }
            }
            if (!found) {
                feedback[i] = LetterStatus.NOT_PRESENT;
            }
        }
    }
        
    return feedback;
    }

    /**
     * Converts LetterStatus array to string representation.
     *
     * @param statuses array of LetterStatus
     * @return string with status symbols
     */
    private String statusesToString(LetterStatus[] statuses) {
        StringBuilder sb = new StringBuilder();
        for (LetterStatus status : statuses) {
            sb.append(status.getSymbol());
        }
        return sb.toString();
    }

    @Override
    public String checkGuess(String guess) throws InvalidGuessException {
        
        if (currentAttempt >= maxAttempts) {
            throw new InvalidGuessException("Player guessing after 5 tries");
        }
                
        LetterStatus[] feedback = generateFeedback(guess);
        
        addResults(guess, feedback);
        this.gameWon = isCorrect(guess);
        this.currentAttempt++;
        return statusesToString(feedback);
    }
    
    @Override
    public void restart() {
        
        if (!results.isEmpty()) {
            saveCurrentGameToHistory();
        }
                
        this.results.clear();
        this.gameWon = false;
        this.currentAttempt = 0;
        try {
            WordDao dao = new WordDao();
            Word newWordEntity = dao.getRandomWord();
            
            if (newWordEntity != null) {
                this.secretWord = newWordEntity.getText();
            } else {
                // Fallback in case DB is empty or error occurs
                System.err.println("Warning: Could not fetch new word from DB. Keeping old word.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean isCorrect(String guess) {
                
        return secretWord.equals(guess);
    }
    
    @Override
    public void saveCurrentGameToHistory() {
        if (!results.isEmpty()) {
            GameHistory record = new GameHistory(
                secretWord,
                results,
                gameWon
            );
            gameHistory.add(record);
            
            try {
                GameSessionDao dao = new GameSessionDao();
                dao.saveSession(this.secretWord, this.currentAttempt, this.gameWon);
            } catch (Exception e) {
                System.err.println("Failed to save game session to DB: " + e.getMessage());
            }
        }
    }
    
    @Override
    public List<GameHistory> getGameHistory() {
        return Collections.unmodifiableList(gameHistory);
    }
    
    /**
     * Gets total number of games played.
     *
     * @return number of completed games
     */
    public int getTotalGamesPlayed() {
        return gameHistory.size();
    }
    
    /**
     * Gets number of games won.
     *
     * @return number of won games
     */
    public int getTotalGamesWon() {
        return (int) gameHistory.stream()
                .filter(GameHistory::isWon)
                .count();
    }
    
    /**
     * Gets win percentage.
     *
     * @return win percentage as double (0-100)
     */
    public double getWinPercentage() {
        if (gameHistory.isEmpty()) {
            return 0.0;
        }
        return (double) getTotalGamesWon() / getTotalGamesPlayed() * 100;
    }
}
