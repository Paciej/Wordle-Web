package pl.polsl.listeners;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import pl.polsl.dao.WordDao;
import pl.polsl.entities.Word;
import pl.polsl.model.AbstractGameModel;
import pl.polsl.model.GameModel;
import pl.polsl.utils.DbManager;

import java.io.InputStream;

/**
 * Listener for application lifecycle events handling database initialization and game model setup.
 * <p>
 * This listener initializes the DB connection via DbManager, bootstraps the dictionary
 * from a resource file, and creates the initial singleton instance of the game model.
 * </p>
 *
 * @author Maciej Porebski
 * @version 2.1
 */
@WebListener
public class GameContextListener implements ServletContextListener {

    /** Attribute name for storing the game model in application scope. */
    public static final String MODEL_ATTRIBUTE = "gameModel";

    /**
     * Initializes the application context when the web application starts.
     * <p>
     * 1. Initializes the DbManager (EntityManagerFactory).
     * 2. Loads dictionary words from 'words.txt' if the database needs population.
     * 3. Fetches a random word from the DB to initialize the singleton GameModel.
     * </p>
     *
     * @param sce the ServletContextEvent containing the ServletContext
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        // Initialize DB Connection (Singleton)
        DbManager.init();

        // Load Dictionary from file (Bootstrapping)
        loadDictionaryData();

        WordDao wordDao = new WordDao();
        Word randomWordEntity = wordDao.getRandomWord();

        String initialPassword = (randomWordEntity != null) ? randomWordEntity.getText() : "HOUSE"; // Fallback

        // Create single instance of the model
        AbstractGameModel model = new GameModel(initialPassword);

        // Store model in application scope
        context.setAttribute(MODEL_ATTRIBUTE, model);
        
        // Log initialization (optional)
        System.out.println("Game Context Initialized. Model word: " + initialPassword);
    }

    /**
     * Helper method to load words from the 'words.txt' resource file into the database.
     * Delegates the actual persisting logic to WordDao.
     */
    private void loadDictionaryData() {
        // Try to load using Context ClassLoader (better for WebApps)
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("words.txt")) {
            if (is != null) {
                System.out.println("DEBUG: File 'words.txt' found via ContextClassLoader.");
                WordDao wordDao = new WordDao();
                wordDao.loadDictionary(is);
            } else {
                // Try fallback to class relative path if previous failed
                System.out.println("DEBUG: File not found via ContextClassLoader. Trying getClass()...");
                try (InputStream is2 = getClass().getClassLoader().getResourceAsStream("words.txt")) {
                    if (is2 != null) {
                         System.out.println("DEBUG: File 'words.txt' found via getClass().");
                         WordDao wordDao = new WordDao();
                         wordDao.loadDictionary(is2);
                    } else {
                        System.err.println("CRITICAL: 'words.txt' NOT FOUND in resources!");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Cleans up resources when the application is shutting down.
     * <p>
     * Closes the DbManager (EntityManagerFactory) and removes attributes.
     * </p>
     *
     * @param sce the ServletContextEvent containing the ServletContext
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        // Close DB Connection
        DbManager.close();

        context.removeAttribute(MODEL_ATTRIBUTE);
        
        System.out.println("Game Context Destroyed. Database connection closed.");
    }
}
