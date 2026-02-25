package pl.polsl.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.time.LocalDateTime;
import pl.polsl.entities.GameSession;
import java.util.List;
import pl.polsl.entities.Word;
import pl.polsl.utils.DbManager;

/**
 * Data Access Object for GameSession entity.
 * Handles saving and retrieving game results.
 *
 * @author Maciej Porebski
 * @version 1.0
 */
public class GameSessionDao {

    public GameSessionDao() {}

    /**
     * Saves a finished game session to the database.
     * @param session GameSession entity with results
     */
    public void save(GameSession session) {
        EntityManager em = DbManager.getEmf().createEntityManager();
        em.getTransaction().begin();
        em.persist(session);
        em.getTransaction().commit();
    }

      /**
     * Saves a completed game session and increments the word usage counter.
     * <p>
     * 1. Finds the Word entity by text.<br>
     * 2. Increments the {@code usageCount} of the word.<br>
     * 3. Creates a new GameSession and links it to the word.<br>
     * 4. Persists changes transactionally using cascading merge.
     * </p>
     *
     * @param secretWordText the text of the word that was played
     * @param attempts       number of attempts made by the player
     * @param solved         true if the game was won, false otherwise
     */
    public void saveSession(String secretWordText, int attempts, boolean solved) {
        EntityManager em = DbManager.getEmf().createEntityManager();
        try {
            em.getTransaction().begin();

            Word wordEntity = null;
            try {
                wordEntity = em.createQuery("SELECT w FROM Word w WHERE w.text = :text", Word.class)
                        .setParameter("text", secretWordText)
                        .getSingleResult();
            } catch (NoResultException e) {
                System.err.println("Error: Word '" + secretWordText + "' not found in DB. Session not saved.");
                em.getTransaction().rollback();
                return;
            }

            int currentCount = (wordEntity.getUsageCount() == null) ? 0 : wordEntity.getUsageCount();
            wordEntity.setUsageCount(currentCount + 1);

            GameSession session = new GameSession();
            session.setAttempts(attempts);
            session.setSolved(solved);
            session.setStartTime(LocalDateTime.now());

            wordEntity.addGameSession(session);
            em.merge(wordEntity);

            em.getTransaction().commit();
            System.out.println("Game session saved. Word usage incremented to: " + wordEntity.getUsageCount());

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
    
    /**
     * Retrieves full history of games.
     * @return List of all GameSession entities ordered by date
     */
    public List<GameSession> findAllHistory() {
        EntityManager em = DbManager.getEmf().createEntityManager();
        return em.createQuery("SELECT gs FROM GameSession gs ORDER BY gs.startTime DESC", GameSession.class)
                 .getResultList();
    }
}