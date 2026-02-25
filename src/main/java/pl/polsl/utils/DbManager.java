package pl.polsl.utils;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Singleton class responsible for managing the EntityManagerFactory lifecycle.
 * Ensures that the factory is created only once during the application's runtime.
 */
public class DbManager {

    private static EntityManagerFactory emf;

    /**
     * Initializes the EntityManagerFactory using the configuration defined in persistence.xml.
     * This method should be called once at application startup (e.g., in a ServletContextListener).
     */
    public static void init() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("WordlePU");
        }
    }

    /**
     * Retrieves the singleton instance of EntityManagerFactory.
     *
     * @return The active EntityManagerFactory instance.
     */
    public static EntityManagerFactory getEmf() {
        return emf;
    }

    /**
     * Closes the EntityManagerFactory to release database resources.
     * Should be called upon application shutdown.
     */
    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
