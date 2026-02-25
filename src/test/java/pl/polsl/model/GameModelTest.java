package pl.polsl.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the GameModel class.
 * <p>
 * This test class verifies all public methods of GameModel using
 * parameterized tests covering correct, incorrect, and boundary cases.
 * Tests focus on public interface without testing private methods directly.
 * The game allows 5 attempts (not 6).
 * </p>
 *
 * @author Maciej Porebski
 * @version 1.0
 */
class GameModelTest {
    
    /** Test instance of GameModel. */
    private GameModel model;
    
    /** Standard secret word used in most tests. */
    private static final String TEST_SECRET_WORD = "HOUSE";
    
    /** Maximum number of attempts allowed in the game. */
    private static final int MAX_ATTEMPTS = 5;
    
    /**
     * Sets up test fixture before each test.
     * <p>
     * Creates a fresh GameModel instance with a known secret word.
     * </p>
     */
    @BeforeEach
    void setUp() {
        model = new GameModel(TEST_SECRET_WORD);
    }
    
    /**
     * Provides test data for different secret words.
     * <p>
     * Includes boundary cases:
     * - Standard 5-letter words
     * - Words with repeated letters
     * - Different word patterns
     * </p>
     *
     * @return stream of test secret words
     */
    static Stream<String> provideSecretWords() {
        return Stream.of(
            "HOUSE",  // Standard word
            "APPLE",  // Word with repeated letters
            "TESTS",  // Word with repeated letters
            "WORLD",  // Different pattern
            "BRAIN"   // Another pattern
        );
    }
    
    /**
     * Tests GameModel constructor with various secret words.
     * <p>
     * Verifies that:
     * - Model is created successfully
     * - Secret word is stored correctly
     * - Initial state is correct (no attempts, not won)
     * - Game history is empty
     * - Max attempts is set to 5
     * </p>
     *
     * @param secretWord the word to use in model
     */
    @ParameterizedTest
    @MethodSource("provideSecretWords")
    void testConstructor(String secretWord) {
        // When
        GameModel testModel = new GameModel(secretWord);
        
        // Then
        assertNotNull(testModel, "Model should be created");
        assertEquals(secretWord, testModel.getSecretWord(), 
            "Secret word should match constructor parameter");
        assertEquals(0, testModel.getCurrentAttempt(), 
            "Initial attempt count should be 0");
        assertFalse(testModel.isGameWon(), 
            "Game should not be won initially");
        assertTrue(testModel.getResults().isEmpty(), 
            "Results should be empty initially");
        assertTrue(testModel.getGameHistory().isEmpty(), 
            "Game history should be empty initially");
        assertEquals(5, testModel.getWordLength(), 
            "Word length should be 5");
        assertEquals(MAX_ATTEMPTS, testModel.getMaxAttempts(), 
            "Max attempts should be 5");
    }
    
    /**
     * Provides test data for checkGuess method with correct guesses.
     *
     * @return stream of arguments with secret word and matching guess
     */
    static Stream<Arguments> provideCorrectGuesses() {
        return Stream.of(
            Arguments.of("HOUSE", "HOUSE"),
            Arguments.of("APPLE", "APPLE"),
            Arguments.of("WORLD", "WORLD"),
            Arguments.of("TESTS", "TESTS"),
            Arguments.of("BRAIN", "BRAIN")
        );
    }
    
    /**
     * Tests checkGuess with correct guesses.
     * <p>
     * Verifies that:
     * - Correct guess returns all '+' symbols
     * - Game is marked as won
     * - Result is added to results list
     * - Attempt counter is incremented
     * </p>
     *
     * @param secretWord the secret word
     * @param guess      the guess (same as secret)
     * @throws InvalidGuessException if guess validation fails
     */
    @ParameterizedTest
    @MethodSource("provideCorrectGuesses")
    void testCheckGuessCorrect(String secretWord, String guess) 
            throws InvalidGuessException {
        // Given
        GameModel testModel = new GameModel(secretWord);
        
        // When
        String feedback = testModel.checkGuess(guess);
        
        // Then
        assertEquals("+++++", feedback, 
            "Correct guess should return all correct symbols");
        assertTrue(testModel.isGameWon(), 
            "Game should be won after correct guess");
        assertEquals(1, testModel.getResults().size(), 
            "Results should contain one entry");
        assertEquals(1, testModel.getCurrentAttempt(), 
            "Attempt counter should be 1");
        assertTrue(testModel.isCorrect(guess), 
            "isCorrect should return true for matching guess");
    }
    
    /**
     * Provides test data for checkGuess with partially correct guesses.
     * <p>
     * Format: secret word, guess, expected feedback pattern
     * </p>
     *
     * @return stream of test arguments
     */
    static Stream<Arguments> providePartialGuesses() {
        return Stream.of(
            // secretWord, guess, expectedFeedback description
            Arguments.of("HOUSE", "HORSE", "has wrong position and correct"),
            Arguments.of("HOUSE", "HELLO", "has correct and not present"),
            Arguments.of("APPLE", "PLEAS", "has wrong positions"),
            Arguments.of("TESTS", "STEAM", "has multiple states"),
            Arguments.of("WORLD", "WOODS", "has correct, wrong and not present")
        );
    }
    
    /**
     * Tests checkGuess with partially correct guesses.
     * <p>
     * Verifies that:
     * - Feedback is generated
     * - Feedback has correct length
     * - Game is not won
     * - Result is recorded
     * </p>
     *
     * @param secretWord the secret word
     * @param guess      the guess
     * @param description description of expected feedback
     * @throws InvalidGuessException if guess validation fails
     */
    @ParameterizedTest
    @MethodSource("providePartialGuesses")
    void testCheckGuessPartial(String secretWord, String guess, String description) 
            throws InvalidGuessException {
        // Given
        GameModel testModel = new GameModel(secretWord);
        
        // When
        String feedback = testModel.checkGuess(guess);
        
        // Then
        assertNotNull(feedback, "Feedback should not be null");
        assertEquals(5, feedback.length(), "Feedback should have 5 characters");
        assertFalse(testModel.isGameWon(), 
            "Game should not be won with partial guess");
        assertEquals(1, testModel.getResults().size(), 
            "Results should contain one entry");
        assertFalse(testModel.isCorrect(guess), 
            "isCorrect should return false for non-matching guess");
    }
    
    /**
     * Provides test data for completely wrong guesses.
     *
     * @return stream of arguments with no matching letters
     */
    static Stream<Arguments> provideWrongGuesses() {
        return Stream.of(
            Arguments.of("HOUSE", "TRICK"),
            Arguments.of("APPLE", "CROWN"),
            Arguments.of("TESTS", "CROWN"),
            Arguments.of("WORLD", "PAUSE"),
            Arguments.of("BRAIN", "CLOUD")
        );
    }
    
    /**
     * Tests checkGuess with completely wrong guesses.
     * <p>
     * Verifies that:
     * - Feedback contains only '-' or '?' symbols
     * - Game is not won
     * - No '+' symbols in feedback (no correct positions)
     * </p>
     *
     * @param secretWord the secret word
     * @param guess      the completely wrong guess
     * @throws InvalidGuessException if guess validation fails
     */
    @ParameterizedTest
    @MethodSource("provideWrongGuesses")
    void testCheckGuessCompletelyWrong(String secretWord, String guess) 
            throws InvalidGuessException {
        // Given
        GameModel testModel = new GameModel(secretWord);
        
        // When
        String feedback = testModel.checkGuess(guess);
        
        // Then
        assertNotNull(feedback, "Feedback should not be null");
        assertEquals(5, feedback.length(), "Feedback should have 5 characters");
        assertFalse(testModel.isGameWon(), "Game should not be won");
        assertFalse(feedback.contains("+"), 
            "Feedback should not contain correct position markers");
    }
    
    /**
     * Tests checkGuess boundary case - maximum attempts reached.
     * <p>
     * Verifies that:
     * - After 5 attempts, further guesses throw exception
     * - Exception has meaningful message
     * - Game state reflects all attempts used
     * </p>
     */
    @Test
    void testCheckGuessMaxAttemptsReached() {
        // Given - make 5 attempts (max allowed)
        assertDoesNotThrow(() -> {
            for (int i = 0; i < MAX_ATTEMPTS; i++) {
                model.checkGuess("WRONG");
            }
        }, "First 5 attempts should not throw exception");
        
        // When & Then - 6th attempt should throw
        InvalidGuessException exception = assertThrows(
            InvalidGuessException.class,
            () -> model.checkGuess("HOUSE"),
            "Should throw exception when max attempts exceeded"
        );
        
        assertTrue(exception.getMessage().contains("after 5 tries") 
                || exception.getMessage().contains("guessing after"),
            "Exception message should mention attempt limit");
        assertEquals(MAX_ATTEMPTS, model.getCurrentAttempt(), 
            "Should have 5 recorded attempts");
        assertFalse(model.hasAttemptsLeft(), 
            "Should have no attempts left");
    }
    
    /**
     * Tests multiple sequential guesses.
     * <p>
     * Verifies that:
     * - Multiple guesses are recorded correctly
     * - Attempt counter increments properly
     * - Each result is stored
     * Boundary: tests from 1 to max (5) attempts
     * </p>
     *
     * @param attemptCount number of attempts to make
     * @throws InvalidGuessException if guess validation fails
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void testMultipleGuesses(int attemptCount) throws InvalidGuessException {
        // When
        for (int i = 0; i < attemptCount; i++) {
            model.checkGuess("WRONG");
        }
        
        // Then
        assertEquals(attemptCount, model.getCurrentAttempt(), 
            "Attempt count should match number of guesses");
        assertEquals(attemptCount, model.getResults().size(), 
            "Results list should contain all attempts");
    }
    
    /**
     * Tests isCorrect method with various guesses.
     * <p>
     * Verifies correct/incorrect identification and case sensitivity.
     * </p>
     *
     * @param guess       the guess to check
     * @param shouldMatch whether it should match "HOUSE"
     */
    @ParameterizedTest
    @CsvSource({
        "HOUSE, true",   // Exact match
        "HORSE, false",  // One letter different
        "MOUSE, false",  // One letter different
        "house, false",  // Lowercase (case sensitive)
        "WRONG, false",  // Completely different
        "HOUS, false"    // Too short (boundary)
    })
    void testIsCorrect(String guess, boolean shouldMatch) {
        // When
        boolean result = model.isCorrect(guess);
        
        // Then
        assertEquals(shouldMatch, result, 
            "isCorrect should return " + shouldMatch + " for guess: " + guess);
    }
    
    /**
     * Tests restart method after playing a game.
     * <p>
     * Verifies that:
     * - Game state is reset
     * - Previous game is saved to history
     * - Results are cleared
     * - Counters are reset
     * </p>
     *
     * @throws InvalidGuessException if guess validation fails
     */
    @Test
    void testRestartAfterGame() throws InvalidGuessException {
        // Given - play a game
        model.checkGuess("WRONG");
        model.checkGuess("GUESS");
        assertEquals(2, model.getCurrentAttempt(), "Should have 2 attempts");
        
        // When
        model.restart();
        
        // Then
        assertEquals(0, model.getCurrentAttempt(), 
            "Attempt counter should be reset");
        assertTrue(model.getResults().isEmpty(), 
            "Results should be cleared");
        assertFalse(model.isGameWon(), 
            "Game won flag should be reset");
        assertEquals(1, model.getGameHistory().size(), 
            "Previous game should be in history");
    }
    
    /**
     * Tests restart method without playing (boundary case).
     * <p>
     * Verifies that restart works even with no attempts made.
     * </p>
     */
    @Test
    void testRestartWithoutPlaying() {
        // Given - no attempts made
        assertEquals(0, model.getCurrentAttempt(), "Should start with 0 attempts");
        
        // When
        model.restart();
        
        // Then
        assertEquals(0, model.getCurrentAttempt(), 
            "Attempt counter should remain 0");
        assertTrue(model.getResults().isEmpty(), 
            "Results should be empty");
        assertEquals(0, model.getGameHistory().size(), 
            "History should be empty when no game was played");
    }
    
    /**
     * Tests saveCurrentGameToHistory method.
     * <p>
     * Verifies that completed games are saved correctly with all data.
     * </p>
     *
     * @throws InvalidGuessException if guess validation fails
     */
    @Test
    void testSaveCurrentGameToHistory() throws InvalidGuessException {
        // Given - play a game
        model.checkGuess("WRONG");
        model.checkGuess("HOUSE");
        assertTrue(model.isGameWon(), "Game should be won");
        
        // When
        model.saveCurrentGameToHistory();
        
        // Then
        assertEquals(1, model.getGameHistory().size(), 
            "History should contain one game");
        
        GameHistory savedGame = model.getGameHistory().get(0);
        assertEquals(TEST_SECRET_WORD, savedGame.getSecretWord(), 
            "Saved game should have correct secret word");
        assertTrue(savedGame.isWon(), 
            "Saved game should be marked as won");
        assertEquals(2, savedGame.getAttemptCount(), 
            "Saved game should have correct attempt count");
    }
    
    /**
     * Tests getGameHistory immutability.
     * <p>
     * Verifies that returned history list cannot be modified externally.
     * </p>
     *
     * @throws InvalidGuessException if guess validation fails
     */
    @Test
    void testGetGameHistoryImmutability() throws InvalidGuessException {
        // Given - play and save a game
        model.checkGuess("HOUSE");
        model.restart();
        
        // When
        List<GameHistory> history = model.getGameHistory();
        int originalSize = history.size();
        
        // Then - try to modify
        assertThrows(UnsupportedOperationException.class, 
            () -> history.add(new GameHistory("TEST", model.getResults(), false)),
            "Should not allow modification of returned history list");
        
        assertEquals(originalSize, model.getGameHistory().size(), 
            "Internal history should not change");
    }
    
    /**
     * Tests getTotalGamesPlayed with various game counts.
     * <p>
     * Boundary cases: 0, 1, multiple games.
     * </p>
     *
     * @param gameCount number of games to play
     * @throws InvalidGuessException if guess validation fails
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 3, 5, 10})
    void testGetTotalGamesPlayed(int gameCount) throws InvalidGuessException {
        // Given & When - play specified number of games
        for (int i = 0; i < gameCount; i++) {
            if (model.getResults().isEmpty() || i > 0) {
                model.checkGuess("WRONG");
            }
            model.restart();
        }
        
        // Then
        assertEquals(gameCount, model.getTotalGamesPlayed(), 
            "Total games should match number of restarts with attempts");
    }
    
    /**
     * Provides test data for win/loss scenarios.
     *
     * @return stream of arguments with win counts and total counts
     */
    static Stream<Arguments> provideWinLossScenarios() {
        return Stream.of(
            Arguments.of(0, 0),   // No games (boundary)
            Arguments.of(1, 1),   // 1 win, 1 game (100%)
            Arguments.of(0, 1),   // 0 wins, 1 game (0%)
            Arguments.of(2, 4),   // 2 wins, 4 games (50%)
            Arguments.of(3, 5),   // 3 wins, 5 games (60%)
            Arguments.of(5, 10)   // 5 wins, 10 games (50%)
        );
    }
    
    /**
     * Tests getTotalGamesWon and getWinPercentage together.
     * <p>
     * Verifies correct counting and percentage calculation for various scenarios.
     * Uses 5 attempts per losing game (not 6).
     * </p>
     *
     * @param wins  number of games to win
     * @param total total number of games to play
     * @throws InvalidGuessException if guess validation fails
     */
    @ParameterizedTest
    @MethodSource("provideWinLossScenarios")
    void testWinStatistics(int wins, int total) throws InvalidGuessException {
        // Given & When - play games with specified win/loss ratio
        for (int i = 0; i < total; i++) {
            if (i < wins) {
                model.checkGuess("HOUSE"); // Win
            } else {
                // Lose - use all 5 attempts
                for (int j = 0; j < MAX_ATTEMPTS; j++) {
                    model.checkGuess("WRONG");
                }
            }
            model.restart();
        }
        
        // Then
        assertEquals(wins, model.getTotalGamesWon(), 
            "Total wins should match");
        assertEquals(total, model.getTotalGamesPlayed(), 
            "Total games should match");
        
        double expectedPercentage = total > 0 ? (wins * 100.0 / total) : 0.0;
        assertEquals(expectedPercentage, model.getWinPercentage(), 0.01, 
            "Win percentage should be calculated correctly");
    }
    
    /**
     * Tests getWinPercentage boundary case - no games played.
     * <p>
     * Verifies that percentage is 0 when no games have been played.
     * </p>
     */
    @Test
    void testGetWinPercentageNoGames() {
        // When
        double percentage = model.getWinPercentage();
        
        // Then
        assertEquals(0.0, percentage, 0.01, 
            "Win percentage should be 0 when no games played");
    }
    
    /**
     * Tests hasAttemptsLeft method through game progression.
     * <p>
     * Verifies correct state at each attempt count (up to 5).
     * </p>
     *
     * @throws InvalidGuessException if guess validation fails
     */
    @Test
    void testHasAttemptsLeft() throws InvalidGuessException {
        // Initially should have attempts
        assertTrue(model.hasAttemptsLeft(), 
            "Should have attempts at start");
        
        // Make 4 attempts - should still have 1 left
        for (int i = 0; i < 4; i++) {
            model.checkGuess("WRONG");
            assertTrue(model.hasAttemptsLeft(), 
                "Should have attempts left after " + (i + 1) + " attempts");
        }
        
        // Make 5th attempt - should have none left
        model.checkGuess("WRONG");
        assertFalse(model.hasAttemptsLeft(), 
            "Should have no attempts left after 5 attempts");
    }
    
    /**
     * Tests boundary case - exactly at attempt limit.
     * <p>
     * Verifies state when exactly MAX_ATTEMPTS have been used.
     * </p>
     *
     * @throws InvalidGuessException if guess validation fails
     */
    @Test
    void testExactlyAtMaxAttempts() throws InvalidGuessException {
        // Given & When - make exactly 5 attempts
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            model.checkGuess("WRONG");
        }
        
        // Then
        assertEquals(MAX_ATTEMPTS, model.getCurrentAttempt(), 
            "Should have exactly 5 attempts");
        assertFalse(model.hasAttemptsLeft(), 
            "Should have no attempts left");
        assertEquals(MAX_ATTEMPTS, model.getResults().size(), 
            "Results should contain 5 entries");
    }
    
    /**
     * Tests game flow - complete game scenario with 5 attempts max.
     * <p>
     * Integration test verifying typical game progression.
     * </p>
     *
     * @throws InvalidGuessException if guess validation fails
     */
    @Test
    void testCompleteGameFlow() throws InvalidGuessException {
        // Round 1: Win on 3rd attempt
        model.checkGuess("WRONG");
        model.checkGuess("GUESS");
        model.checkGuess("HOUSE");
        assertTrue(model.isGameWon(), "Should win on correct guess");
        assertEquals(3, model.getCurrentAttempt(), "Should have 3 attempts");
        
        // Restart for round 2
        model.restart();
        assertEquals(0, model.getCurrentAttempt(), "Attempts should reset");
        assertEquals(1, model.getGameHistory().size(), "Should have 1 game in history");
        
        // Round 2: Lose after 5 attempts
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            model.checkGuess("WRONG");
        }
        assertFalse(model.isGameWon(), "Should not win with wrong guesses");
        assertFalse(model.hasAttemptsLeft(), "Should have no attempts left");
        
        // Check final statistics
        model.restart();
        assertEquals(2, model.getTotalGamesPlayed(), "Should have played 2 games");
        assertEquals(1, model.getTotalGamesWon(), "Should have won 1 game");
        assertEquals(50.0, model.getWinPercentage(), 0.01, "Win rate should be 50%");
    }
    
    /**
     * Tests boundary case - winning on last possible attempt.
     * <p>
     * Verifies that player can win on the 5th (last) attempt.
     * </p>
     *
     * @throws InvalidGuessException if guess validation fails
     */
    @Test
    void testWinOnLastAttempt() throws InvalidGuessException {
        // Given & When - make 4 wrong guesses, then correct one
        for (int i = 0; i < MAX_ATTEMPTS - 1; i++) {
            model.checkGuess("WRONG");
        }
        model.checkGuess("HOUSE"); // 5th and final attempt
        
        // Then
        assertTrue(model.isGameWon(), "Should win on 5th attempt");
        assertEquals(MAX_ATTEMPTS, model.getCurrentAttempt(), 
            "Should have used all 5 attempts");
        assertFalse(model.hasAttemptsLeft(), 
            "Should have no attempts left even though won");
    }
}
