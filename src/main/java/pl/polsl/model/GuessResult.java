package pl.polsl.model;

/**
 * Record representing a single guess attempt and its evaluation feedback.
 * <p>
 * This immutable data structure stores the word that was guessed along with
 * the feedback for each letter position, indicating whether letters are correct,
 * in wrong positions, or not present in the secret word.
 * </p>
 *
 * @param attempt  the guessed word as a string
 * @param feedback array of LetterStatus indicating the result for each letter
 *
 * @author Maciej Porebski
 * @version 1.0
 */
public record GuessResult(String attempt, LetterStatus[] feedback) {}

