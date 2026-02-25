package pl.polsl.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Word entity representing a single 5-letter word stored in the dictionary table.
 *
 * @author Maciej Porebski
 * @version 1.1
 */
@Entity
@Table(name = "words")
public class Word implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Primary key of the word. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Word text (5 letters), unique across the table. */
    @Column(nullable = false, unique = true, length = 5)
    private String text;

    /** Counter tracking how many times the word was used. */
    @Column(name = "usage_count", nullable = false)
    private Integer usageCount = 0;

    /** Game sessions that used this word (inverse side of the relation). */
    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<GameSession> gameSessions = new ArrayList<>();

    /**
     * Default constructor required by JPA.
     */
    public Word() { }

    /**
     * Convenience constructor to create a Word with text.
     *
     * @param text 5-letter word text
     */
    public Word(String text) {
        this.text = text;
    }

    /**
     * Adds a session to this word and synchronizes both sides of the association.
     *
     * @param session session to add
     */
    public void addGameSession(GameSession session) {
        gameSessions.add(session);
        session.setWord(this);
    }

    /**
     * Removes a session from this word and synchronizes both sides of the association.
     *
     * @param session session to remove
     */
    public void removeGameSession(GameSession session) {
        gameSessions.remove(session);
        session.setWord(null);
    }

    /**
     * Retrieves the unique identifier of the word.
     *
     * @return the primary key ID
     */
    public Long getId() { return id; }

    /**
     * Sets the unique identifier for the word.
     *
     * @param id the primary key ID to set
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Retrieves the text content of the word.
     *
     * @return the 5-letter word string
     */
    public String getText() { return text; }

    /**
     * Sets the text content of the word.
     *
     * @param text the 5-letter word string to set
     */
    public void setText(String text) { this.text = text; }

    /**
     * Retrieves the usage count of the word.
     *
     * @return the number of times this word has been used
     */
    public Integer getUsageCount() { return usageCount; }

    /**
     * Sets the usage count for the word.
     *
     * @param usageCount the new usage count to set
     */
    public void setUsageCount(Integer usageCount) { this.usageCount = usageCount; }

    /**
     * Retrieves the list of game sessions associated with this word.
     *
     * @return a list of GameSession objects
     */
    public List<GameSession> getGameSessions() { return gameSessions; }

    /**
     * Sets the list of game sessions associated with this word.
     *
     * @param gameSessions the list of GameSession objects to set
     */
    public void setGameSessions(List<GameSession> gameSessions) { this.gameSessions = gameSessions; }
}
