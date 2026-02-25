package pl.polsl.model;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Unit tests for {@link AbstractGameModel#hasAttemptsLeft()}.
 * 
 * @author Maciej Porebski
 * @version 1.0
 */
class AbstractGameModelTest {

    /**
     * Concrete test implementation for base class testing.
     */
    private static class TestGameModel extends AbstractGameModel {
        public TestGameModel(int maxAttempts, String secretWord) {
            super(maxAttempts, secretWord);
        }

        @Override public boolean isCorrect(String guess) { return false; }
        @Override public String checkGuess(String guess) throws InvalidGuessException { return ""; }
        @Override public void restart() { }

        @Override
        public void saveCurrentGameToHistory() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public List<GameHistory> getGameHistory() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }

    /**
     * Test data - MUST be static method in enclosing class (NOT nested).
     */
    static Stream<Arguments> hasAttemptsLeftData() {
        return Stream.of(
            arguments(5, 0, true),   // normal: start
            arguments(5, 2, true),   // normal: middle  
            arguments(5, 4, true),   // boundary: last attempt
            arguments(5, 5, false),  // boundary: no attempts
            arguments(1, 0, true),   // edge: single attempt available
            arguments(1, 1, false)   // edge: single attempt used
        );
    }

    /**
     * Parameterized test verifying hasAttemptsLeft() logic.
     * Covers normal, boundary, and edge cases of game logic.
     */
    @ParameterizedTest(name = "maxAttempts={0}, currentAttempt={1} → hasAttemptsLeft={2}")
    @MethodSource("hasAttemptsLeftData")  // References method in enclosing class
    @DisplayName("hasAttemptsLeft() - all game scenarios")
    void shouldCorrectlyReportIfAttemptsAreLeft(int maxAttempts, int currentAttempt, boolean expected) {

        TestGameModel model = new TestGameModel(maxAttempts, "secret");
        model.currentAttempt = currentAttempt; 
        boolean actual = model.hasAttemptsLeft();
        assertEquals(expected, actual, 
            String.format("Expected hasAttemptsLeft=%s for max=%d, current=%d", 
                         expected, maxAttempts, currentAttempt));
    }
}