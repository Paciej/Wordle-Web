package pl.polsl.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * GameSession entity representing a single play session stored in the database.
 *
 * @author Maciej Porebski
 * @version 1.1
 */
@Entity
@Table(name = "game_sessions")
public class GameSession implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Primary key of the session. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Owning side of the relation - session references exactly one word. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    /** Number of attempts in this session. */
    @Column(nullable = false)
    private Integer attempts = 0;

    /** Flag indicating whether the game was solved. */
    @Column(nullable = false)
    private Boolean solved = false;

    /** Session start time stored as TIMESTAMP (provider maps LocalDateTime). */
    @Column(name = "start_time", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime startTime;

    /**
     * Default constructor required by JPA.
     */
    public GameSession() { }

    /**
     * Automatically initializes startTime before persisting the entity.
     */
    @PrePersist
    public void prePersist() {
        if (startTime == null) startTime = LocalDateTime.now();
    }

    /**
     * Retrieves the unique identifier of this game session.
     *
     * @return the primary key ID
     */
    public Long getId() { return id; }

    /**
     * Sets the unique identifier for this game session.
     *
     * @param id the primary key ID to set
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Retrieves the word entity associated with this session.
     *
     * @return the associated Word object
     */
    public Word getWord() { return word; }

    /**
     * Sets the word entity for this session.
     *
     * @param word the Word object to associate
     */
    public void setWord(Word word) { this.word = word; }

    /**
     * Retrieves the number of attempts made in this session.
     *
     * @return the attempt count
     */
    public Integer getAttempts() { return attempts; }

    /**
     * Sets the number of attempts made in this session.
     *
     * @param attempts the attempt count to set
     */
    public void setAttempts(Integer attempts) { this.attempts = attempts; }

    /**
     * Checks if the game was successfully solved.
     *
     * @return true if the game was won, false otherwise
     */
    public Boolean getSolved() { return solved; }

    /**
     * Sets the solved status of the game.
     *
     * @param solved true if the game was won
     */
    public void setSolved(Boolean solved) { this.solved = solved; }

    /**
     * Retrieves the timestamp when the session started.
     *
     * @return the start date and time
     */
    public LocalDateTime getStartTime() { return startTime; }

    /**
     * Sets the timestamp when the session started.
     *
     * @param startTime the start date and time to set
     */
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
}
