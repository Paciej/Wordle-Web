package pl.polsl.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import pl.polsl.entities.Word;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import pl.polsl.utils.DbManager;

/**
 * Data Access Object for Word entity.
 * Provides basic CRUD operations without exception handling as required.
 *
 * @author Maciej Porebski
 * @version 1.0
 */
public class WordDao {

        /**
     * Loads 5-letter words from a text file into the database.
     * Transactionally inserts only words that do not currently exist in the DB.
     *
     * @param inputStream The input stream of the dictionary file.
     */
    /**
     * Loads 5-letter words from a text file into the database.
     * Transactionally inserts only words that do not currently exist in the DB.
     *
     * @param inputStream The input stream of the dictionary file.
     */
    public void loadDictionary(InputStream inputStream) {
        System.out.println("DEBUG: Starting loadDictionary...");

        if (inputStream == null) {
            System.err.println("DEBUG: InputStream is null. Aborting.");
            return;
        }

        EntityManager em = DbManager.getEmf().createEntityManager();
        try {
            // 1. Read all words from file into a List
            List<String> rawLines = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.toList());
            
            System.out.println("DEBUG: Raw lines read from file: " + rawLines.size());

            // 2. Filter valid words
            List<String> validWords = rawLines.stream()
                    .map(String::trim)
                    .filter(w -> w.length() == 5)
                    .map(String::toUpperCase)
                    .collect(Collectors.toList());

            System.out.println("DEBUG: Valid 5-letter words to process: " + validWords.size());

            if (validWords.isEmpty()) {
                System.out.println("DEBUG: No valid words found. Check file formatting (one word per line, 5 letters).");
                return;
            }

            em.getTransaction().begin();

            // 3. Fetch existing words to avoid duplicates
            List<String> existingTexts = em.createQuery("SELECT w.text FROM Word w", String.class)
                    .getResultList();
            
            System.out.println("DEBUG: Existing words in DB: " + existingTexts.size());

            // 4. Persist new words
            int count = 0;
            for (String text : validWords) {
                if (!existingTexts.contains(text)) {
                    Word newWord = new Word(text);
                    newWord.setUsageCount(0);
                    em.persist(newWord);
                    count++;
                }
            }

            System.out.println("DEBUG: Words marked for insert: " + count);

            if (count > 0) {
                em.getTransaction().commit();
                System.out.println("DEBUG: Transaction committed successfully.");
            } else {
                em.getTransaction().rollback();
                System.out.println("DEBUG: Nothing to commit. Rollback.");
            }

        } catch (Exception e) {
            System.err.println("DEBUG: Exception during dictionary load.");
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } finally {
            em.close();
        }
    }
   
    /**
     * Persists a new word in the database.
     * @param word Word entity to save
     */
    public void create(Word word) {
        EntityManager em = DbManager.getEmf().createEntityManager();
        em.getTransaction().begin();
        em.persist(word);
        em.getTransaction().commit();
    }

    /**
     * Retrieves all words from the database.
     * @return List of all Word entities
     */
    public List<Word> findAll() { 
        EntityManager em = DbManager.getEmf().createEntityManager();
        return em.createQuery("SELECT w FROM Word w", Word.class).getResultList();
    }

    /**
     * Finds a single word by its text.
     * @param text The 5-letter text to search for
     * @return Found Word entity
     */
    public Word findByText(String text) {
        EntityManager em = DbManager.getEmf().createEntityManager();
        return em.createQuery("SELECT w FROM Word w WHERE w.text = :text", Word.class)
                 .setParameter("text", text)
                 .getSingleResult();
    }
    
        /**
     * Checks if the specified word exists in the database.
     *
     * @param word The word string to verify.
     * @return true if the word is found in the database, false otherwise.
     */
    public boolean wordExists(String word) {
        EntityManager em = DbManager.getEmf().createEntityManager();
        try {
            String jpql = "SELECT COUNT(w) FROM Word w WHERE w.text = :text";
            Long count = em.createQuery(jpql, Long.class)
                    .setParameter("text", word)
                    .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            return false;
        } finally {
            em.close();
        }
    }
    
       /**
     * Selects a random word from the database.
     * Efficiently handles the selection using record counting and pagination offset.
     *
     * @return A random Word entity, or null if the database is empty or an error occurs.
     */
    public Word getRandomWord() {
        EntityManager em = DbManager.getEmf().createEntityManager();
        try {
            // Count total number of rows in the words table
            Long count = em.createQuery("SELECT COUNT(w) FROM Word w", Long.class).getSingleResult();

            if (count == 0) {
                return null;
            }

            // Generate a random index within the range of total records
            int randomPosition = new Random().nextInt(count.intValue());

            // Fetch the single word located at the random position
            TypedQuery<Word> query = em.createQuery("SELECT w FROM Word w", Word.class);
            query.setFirstResult(randomPosition);
            query.setMaxResults(1);

            return query.getSingleResult();

        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }
}
