package pl.polsl.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the GameHistory class.
 * <p>
 * This test class verifies all public methods of GameHistory using
 * parameterized tests covering correct, incorrect, and boundary cases.
 * </p>
 *
 * @author Maciej Porebski
 * @version 1.0
 */
class GameHistoryTest {
    
    /**
     * Provides test data for GameHistory constructor with various scenarios.
     * <p>
     * Test cases include:
     * - Normal game with few attempts
     * - Game with maximum attempts
     * - Game with single attempt (boundary)
     * - Empty attempts list (boundary)
     * - Won and lost game states
     * </p>
     *
     * @return stream of arguments for parameterized tests
     */
    static Stream<Arguments> provideGameHistoryData() {
        return Stream.of(
            // secretWord, attempts, won, expectedAttemptCount
            Arguments.of("HOUSE", createAttempts(3), true, 3),
            Arguments.of("APPLE", createAttempts(6), false, 6),
            Arguments.of("WORLD", createAttempts(1), true, 1),  // Boundary: minimum attempts
            Arguments.of("BRAIN", createAttempts(0), false, 0), // Boundary: no attempts
            Arguments.of("TESTS", createAttempts(5), false, 5)
        );
    }
    
    /**
     * Tests the GameHistory constructor and basic getters with various inputs.
     * <p>
     * Verifies that:
     * - Secret word is correctly stored
     * - Attempt count matches input
     * - Won status is correctly set
     * - Timestamp is not null and recent
     * </p>
     *
     * @param secretWord         the secret word for the game
     * @param attempts           list of guess attempts
     * @param won                whether the game was won
     * @param expectedAttemptCount expected number of attempts
     */
    @ParameterizedTest
    @MethodSource("provideGameHistoryData")
    void testGameHistoryConstructorAndGetters(String secretWord, 
                                              List<GuessResult> attempts,
                                              boolean won,
                                              int expectedAttemptCount) {
        // Given & When
        GameHistory history = new GameHistory(secretWord, attempts, won);
        
        // Then
        assertEquals(secretWord, history.getSecretWord(), 
            "Secret word should match input");
        assertEquals(expectedAttemptCount, history.getAttemptCount(), 
            "Attempt count should match input list size");
        assertEquals(won, history.isWon(), 
            "Won status should match input");
        assertNotNull(history.getTimestamp(), 
            "Timestamp should not be null");
        assertTrue(history.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)),
            "Timestamp should be recent");
    }
    
    /**
     * Provides test data for formatted timestamp verification.
     *
     * @return stream of secret words for test cases
     */
    static Stream<String> provideSecretWords() {
        return Stream.of("HOUSE", "APPLE", "WORLD", "BRAIN", "TESTS");
    }
    
    /**
     * Tests the getFormattedTimestamp method with various game histories.
     * <p>
     * Verifies that:
     * - Formatted timestamp matches expected pattern (yyyy-MM-dd HH:mm:ss)
     * - Timestamp string is not null or empty
     * - Format contains expected separators
     * </p>
     *
     * @param secretWord the secret word for creating game history
     */
    @ParameterizedTest
    @MethodSource("provideSecretWords")
    void testGetFormattedTimestamp(String secretWord) {
        // Given
        List<GuessResult> attempts = createAttempts(3);
        GameHistory history = new GameHistory(secretWord, attempts, true);
        
        // When
        String formattedTimestamp = history.getFormattedTimestamp();
        
        // Then
        assertNotNull(formattedTimestamp, 
            "Formatted timestamp should not be null");
        assertFalse(formattedTimestamp.isEmpty(), 
            "Formatted timestamp should not be empty");
        assertTrue(formattedTimestamp.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"),
            "Formatted timestamp should match pattern yyyy-MM-dd HH:mm:ss");
        assertTrue(formattedTimestamp.contains("-"), 
            "Timestamp should contain date separator");
        assertTrue(formattedTimestamp.contains(":"), 
            "Timestamp should contain time separator");
    }
    
    /**
     * Provides test data for getAttempts immutability test.
     * <p>
     * Tests boundary cases:
     * - Empty list
     * - Single element
     * - Multiple elements
     * </p>
     *
     * @return stream of attempt counts
     */
    static Stream<Integer> provideAttemptCounts() {
        return Stream.of(0, 1, 3, 6);  // Boundary and normal cases
    }
    
    /**
     * Tests that getAttempts returns an immutable copy.
     * <p>
     * Verifies that:
     * - Modifying returned list doesn't affect internal state
     * - Original list size remains unchanged
     * - Immutability is maintained (defensive copy)
     * </p>
     *
     * @param attemptCount number of attempts to create
     */
    @ParameterizedTest
    @MethodSource("provideAttemptCounts")
    void testGetAttemptsImmutability(int attemptCount) {
        // Given
        List<GuessResult> originalAttempts = createAttempts(attemptCount);
        GameHistory history = new GameHistory("HOUSE", originalAttempts, true);
        
        // When
        List<GuessResult> retrievedAttempts = history.getAttempts();
        int originalSize = retrievedAttempts.size();
        
        // Try to modify the retrieved list
        try {
            retrievedAttempts.add(new GuessResult("ADDED", 
                new LetterStatus[]{LetterStatus.NOT_PRESENT, LetterStatus.NOT_PRESENT,
                                   LetterStatus.NOT_PRESENT, LetterStatus.NOT_PRESENT,
                                   LetterStatus.NOT_PRESENT}));
        } catch (UnsupportedOperationException e) {
            // Expected if implementation returns unmodifiable list
        }
        
        // Then
        assertEquals(originalSize, history.getAttempts().size(),
            "Internal list size should not change after external modification attempt");
        assertEquals(attemptCount, history.getAttemptCount(),
            "Attempt count should remain consistent");
    }
    
    /**
     * Provides test data for boundary cases with null and empty values.
     *
     * @return stream of test arguments
     */
    static Stream<Arguments> provideBoundaryData() {
        return Stream.of(
            // Testing with empty string as secret word (boundary)
            Arguments.of("", createAttempts(1), true),
            // Testing with very long secret word (boundary)
            Arguments.of("VERYLONGWORD", createAttempts(2), false),
            // Testing with single character (boundary)
            Arguments.of("A", createAttempts(3), true)
        );
    }
    
    /**
     * Tests GameHistory with boundary cases for secret word.
     * <p>
     * Verifies behavior with:
     * - Empty string
     * - Very long strings
     * - Single character
     * </p>
     *
     * @param secretWord the boundary case secret word
     * @param attempts   list of attempts
     * @param won        game result
     */
    @ParameterizedTest
    @MethodSource("provideBoundaryData")
    void testBoundaryCases(String secretWord, List<GuessResult> attempts, boolean won) {
        // Given & When
        GameHistory history = new GameHistory(secretWord, attempts, won);
        
        // Then
        assertNotNull(history, 
            "GameHistory should be created even with boundary secret word values");
        assertEquals(secretWord, history.getSecretWord(),
            "Secret word should be stored as-is, even for boundary cases");
        assertNotNull(history.getTimestamp(),
            "Timestamp should be set for boundary cases");
    }
    
    /**
     * Tests GameHistory with null attempts list.
     * <p>
     * This tests incorrect usage - passing null should be handled gracefully
     * or throw an exception depending on implementation.
     * </p>
     */
    @Test
    void testNullAttemptsList() {
        // Given
        String secretWord = "HOUSE";
        List<GuessResult> nullAttempts = null;
        
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            GameHistory history = new GameHistory(secretWord, nullAttempts, true);
            history.getAttempts(); // Try to access attempts
        }, "Creating GameHistory with null attempts should throw NullPointerException");
    }
    
    /**
     * Provides test data for getAttemptCount with edge cases.
     *
     * @return stream of attempt counts including boundaries
     */
    static Stream<Integer> provideAttemptCountBoundaries() {
        return Stream.of(0, 1, 5, 6, 10); // Including 0 (min), 6 (typical max), 10 (over max)
    }
    
    /**
     * Tests getAttemptCount with various list sizes including boundaries.
     * <p>
     * Verifies correct count for:
     * - Zero attempts (empty list)
     * - Single attempt (minimum valid game)
     * - Maximum typical attempts
     * - Beyond maximum attempts
     * </p>
     *
     * @param count the number of attempts to test
     */
    @ParameterizedTest
    @MethodSource("provideAttemptCountBoundaries")
    void testGetAttemptCountBoundaries(int count) {
        // Given
        List<GuessResult> attempts = createAttempts(count);
        GameHistory history = new GameHistory("TESTS", attempts, count <= 6);
        
        // When
        int actualCount = history.getAttemptCount();
        
        // Then
        assertEquals(count, actualCount,
            "Attempt count should match the number of attempts provided");
        assertEquals(attempts.size(), actualCount,
            "Attempt count should match list size");
    }
    
    /**
     * Helper method to create a list of GuessResult objects.
     *
     * @param count number of attempts to create
     * @return list of GuessResult objects
     */
    private static List<GuessResult> createAttempts(int count) {
        List<GuessResult> attempts = new ArrayList<>();
        LetterStatus[] feedback = {
            LetterStatus.CORRECT,
            LetterStatus.WRONG_POSITION,
            LetterStatus.NOT_PRESENT,
            LetterStatus.CORRECT,
            LetterStatus.NOT_PRESENT
        };
        
        for (int i = 0; i < count; i++) {
            attempts.add(new GuessResult("WORD" + i, feedback));
        }
        
        return attempts;
    }
}
