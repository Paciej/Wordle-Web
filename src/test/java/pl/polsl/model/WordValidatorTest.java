package pl.polsl.model;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import pl.polsl.entities.Word;
import pl.polsl.utils.DbManager;

/**
 * Unit tests for WordValidator class.
 * Tests cover valid, invalid and boundary cases of isValid method.
 *
 * @author Maciej Porebski
 * @version 1.0
 */
public class WordValidatorTest {

    /**
     * Tests isValid(String) with words having invalid length (not equal to 5).
     * Verifies that InvalidGuessException is thrown.
     *
     * @param word word with invalid length
     */
    @ParameterizedTest(name = "Invalid length \"{0}\" should be rejected")
    @CsvSource({
        "'',       A word must contain 5 letters",
        "a,        A word must contain 5 letters",
        "ab,       A word must contain 5 letters",
        "abcd,     A word must contain 5 letters",
        "abcdef,   A word must contain 5 letters",
        "abcdefg,  A word must contain 5 letters"
    })
    public void testIsValidInvalidLength(String word, String expectedMessage) {
        WordValidator validator = new WordValidator();

        InvalidGuessException ex = assertThrows(
                InvalidGuessException.class,
                () -> validator.isValid(word),
                "Word with length different than 5 should cause InvalidGuessException"
        );

        assertEquals(expectedMessage, ex.getMessage(),
                "Exception message for invalid length is incorrect");
    }

    /**
     * Tests isValid(String) with words containing special characters.
     * Verifies that InvalidGuessException is thrown.
     *
     * @param word word containing at least one special character
     */
    @ParameterizedTest(name = "Special characters in \"{0}\" should be rejected")
    @CsvSource({
        "ab#de",
        "a$cde",
        "abc!e",
        "ab@de",
        "a%cd*",
        "ąbcde",
        "ab+de"
    })
    public void testIsValidSpecialCharacters(String word) {
        WordValidator validator = new WordValidator();

        InvalidGuessException ex = assertThrows(
                InvalidGuessException.class,
                () -> validator.isValid(word),
                "Word with special characters should cause InvalidGuessException"
        );

        assertEquals("A word cannot contain any special characters", ex.getMessage(),
                "Exception message for special characters is incorrect");
    }

    /**
     * Tests isValid(String) with null value.
     * Verifies that InvalidGuessException is thrown.
     * (If currently NullPointerException is thrown, dostosuj w klasie produkcyjnej
     * albo zmień oczekiwany typ wyjątku w tym teście.)
     *
     * @param word null word
     */
    @ParameterizedTest(name = "Null word should be rejected")
    @NullSource
    public void testIsValidNull(String word) {
        WordValidator validator = new WordValidator();

        assertThrows(
                InvalidGuessException.class,
                () -> validator.isValid(word),
                "Null word should cause InvalidGuessException"
        );
    }

/**
 * Parameterized test verifying that words containing digits or spaces are rejected by the validator.
 * <p>
 * This test covers the invalid input scenarios where:
 * <ul>
 *     <li>Words contain digits (e.g., "a1 2b", "1a2b3")</li>
 *     <li>Words contain spaces (e.g., "ab 12")</li>
 *     <li>Words are entirely numeric (e.g., "12345")</li>
 * </ul>
 * Each test case verifies that:
 * <ol>
 *     <li>{@link WordValidator#isValid(String)} throws {@link InvalidGuessException}</li>
 *     <li>Exception message exactly matches "A word cannot contain any special characters"</li>
 * </ol>
 * Uses {@link CsvSource} to provide multiple invalid word examples efficiently.
 *
 * @param word the invalid word containing digits or spaces to be tested
 */
    @ParameterizedTest(name = "Digits or spaces in \"{0}\" should be rejected")
    @CsvSource({
        "a1 2b",
        "12345",
        "ab 12",
        "1a2b3"
    })
    public void testIsValidDigitsOrSpaces(String word) {
        WordValidator validator = new WordValidator();
        
        InvalidGuessException ex = assertThrows (
                InvalidGuessException.class,
                () -> validator.isValid(word),
                "Word with digits or spaces should cause InvalidGuessException"
        );
        
        assertEquals("A word cannot contain any special characters",
                ex.getMessage(),
                "Exception message for digits/spaces is incorrect"
                );
        
    }
}
