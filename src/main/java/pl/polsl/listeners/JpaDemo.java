package pl.polsl.listeners;

import jakarta.persistence.EntityManager;
import pl.polsl.entities.GameSession;
import pl.polsl.entities.Word;
import pl.polsl.utils.DbManager;
import java.util.List;

/**
 * Simple JPA demo class that performs read/write operations without web access.
 */
public class JpaDemo {

    /**
     * Runs example read/write operations on Word and GameSession entities.
     *
     * @param args program arguments
     */
    public static void main(String[] args) {

        try (EntityManager em = DbManager.getEmf().createEntityManager()) {
            em.getTransaction().begin();

            Word w = new Word("APPLE");
            em.persist(w);

            GameSession s = new GameSession();
            s.setWord(w);
            s.setAttempts(1);
            s.setSolved(false);
            em.persist(s);

            em.getTransaction().commit();

            Word found = em.createQuery("SELECT w FROM Word w WHERE w.text = :t", Word.class)
                    .setParameter("t", "APPLE")
                    .getSingleResult();

            List<GameSession> sessions = em.createQuery(
                    "SELECT gs FROM GameSession gs WHERE gs.word = :word", GameSession.class)
                    .setParameter("word", found)
                    .getResultList();
        }
        
        DbManager.close();
    }
}
