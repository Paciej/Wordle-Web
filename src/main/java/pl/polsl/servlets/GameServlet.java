package pl.polsl.servlets;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pl.polsl.listeners.GameContextListener;
import pl.polsl.model.AbstractGameModel;
import pl.polsl.model.GameModel;
import pl.polsl.model.InvalidGuessException;
import pl.polsl.model.WordValidator;
import pl.polsl.utils.CookieManager;

/**
 * Servlet responsible for game logic with cookie support for game state.
 * <p>
 * This servlet uses cookies to store current game state and game history,
 * allowing users to resume games across browser sessions.
 * </p>
 *
 * @author Maciej Porebski
 * @version 5.0
 */
@WebServlet(name = "GameServlet", urlPatterns = {"/game"})
public class GameServlet extends HttpServlet {
    
    /** The validator used to verify the correctness of guessed words. */
    private WordValidator validator;
    
    /** Cookie names. */
    private static final String COOKIE_CURRENT_ATTEMPTS = "wordleCurrentAttempts";
    private static final String COOKIE_GAME_WON = "wordleGameWon";
    private static final String COOKIE_TOTAL_GAMES = "wordleTotalGames";
    private static final String COOKIE_GAMES_WON = "wordleGamesWon";
    private static final String COOKIE_LAST_PLAYED = "wordleLastPlayed";
    
    /**
     * Initializes the servlet and creates the word validator.
     *
     * @throws ServletException if an error occurs during initialization
     */
    @Override
    public void init() throws ServletException {
        super.init();
        this.validator = new WordValidator();
    }
    
    /**
     * Handles HTTP GET requests by delegating to processRequest.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /**
     * Handles HTTP POST requests by delegating to processRequest.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /**
     * Processes both GET and POST requests with unified logic.
     * <p>
     * This method handles cookies for tracking game state and history.
     * </p>
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Get shared model from application scope
            ServletContext context = getServletContext();
            AbstractGameModel model = (AbstractGameModel) context.getAttribute(
                GameContextListener.MODEL_ATTRIBUTE);
            
            if (model == null) {
                request.setAttribute("errorMessage", 
                    "Game model not initialized. Please contact administrator.");
            } else {
                // Process actions only for POST requests
                if ("POST".equalsIgnoreCase(request.getMethod())) {
                    String action = request.getParameter("action");
                    
                    if (action == null || action.trim().isEmpty()) {
                        request.setAttribute("errorMessage", 
                            "No action specified. Please use the buttons provided.");
                    } else if ("guess".equals(action)) {
                        handleGuess(request, response, model);
                        saveGameStateToCookies(request, response, model);
                    } else if ("restart".equals(action)) {
                        handleRestart(request, response, model);
                        saveGameStateToCookies(request, response, model);
                    } else {
                        request.setAttribute("errorMessage", 
                            "Unknown action: " + action);
                    }
                }
                
                // Update last played timestamp
                updateLastPlayedCookie(response);
                
                // Load cookie data for display
                loadCookieData(request, model);
                
                // Set view attributes for both GET and POST
                setViewAttributes(request, model);
            }
            
        } catch (InvalidGuessException e) {
            request.setAttribute("errorMessage", "❌ " + e.getMessage());
            ServletContext context = getServletContext();
            AbstractGameModel model = (AbstractGameModel) context.getAttribute(
                GameContextListener.MODEL_ATTRIBUTE);
            if (model != null) {
                setViewAttributes(request, model);
                loadCookieData(request, model);
            }
            
        } catch (Exception e) {
            request.setAttribute("errorMessage", 
                "Unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Always forward to JSP view
        RequestDispatcher dispatcher = request.getRequestDispatcher("/wordle.jsp");
        dispatcher.forward(request, response);
    }
    
    /**
     * Saves current game state to cookies.
     * <p>
     * Stores: current attempts count, game won status.
     * </p>
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @param model    the game model
     */
    private void saveGameStateToCookies(HttpServletRequest request, 
                                       HttpServletResponse response,
                                       AbstractGameModel model) {
        // Save current game state
        int attempts = model.getResults() != null ? model.getResults().size() : 0;
        CookieManager.setCookie(response, COOKIE_CURRENT_ATTEMPTS, 
            String.valueOf(attempts), CookieManager.SESSION_MAX_AGE);
        
        CookieManager.setCookie(response, COOKIE_GAME_WON, 
            String.valueOf(model.isGameWon()), CookieManager.SESSION_MAX_AGE);
        
        // Update history statistics if game ended
        if (model.isGameWon() || !model.hasAttemptsLeft()) {
            updateHistoryStatistics(request, response, model.isGameWon());
        }
    }
    
    /**
     * Updates game history statistics in cookies.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @param won      whether the game was won
     */
    private void updateHistoryStatistics(HttpServletRequest request,
                                         HttpServletResponse response,
                                         boolean won) {
        // Get current statistics
        String totalGamesStr = CookieManager.getCookie(request, COOKIE_TOTAL_GAMES);
        String gamesWonStr = CookieManager.getCookie(request, COOKIE_GAMES_WON);
        
        int totalGames = parseIntOrDefault(totalGamesStr, 0);
        int gamesWon = parseIntOrDefault(gamesWonStr, 0);
        
        // Increment counters
        totalGames++;
        if (won) {
            gamesWon++;
        }
        
        // Save updated statistics
        CookieManager.setCookie(response, COOKIE_TOTAL_GAMES, String.valueOf(totalGames));
        CookieManager.setCookie(response, COOKIE_GAMES_WON, String.valueOf(gamesWon));
    }
    
    /**
     * Loads cookie data and sets it as request attributes for display.
     *
     * @param request the HttpServletRequest object
     * @param model   the game model
     */
    private void loadCookieData(HttpServletRequest request, AbstractGameModel model) {
        // Current game state from cookies
        String currentAttempts = CookieManager.getCookie(request, COOKIE_CURRENT_ATTEMPTS);
        String gameWon = CookieManager.getCookie(request, COOKIE_GAME_WON);
        
        // History statistics from cookies
        String totalGames = CookieManager.getCookie(request, COOKIE_TOTAL_GAMES);
        String gamesWon = CookieManager.getCookie(request, COOKIE_GAMES_WON);
        String lastPlayed = CookieManager.getCookie(request, COOKIE_LAST_PLAYED);
        
        // Set attributes for JSP display
        request.setAttribute("cookieCurrentAttempts", currentAttempts != null ? currentAttempts : "0");
        request.setAttribute("cookieGameWon", gameWon);
        request.setAttribute("cookieTotalGames", totalGames != null ? totalGames : "0");
        request.setAttribute("cookieGamesWon", gamesWon != null ? gamesWon : "0");
        request.setAttribute("cookieLastPlayed", lastPlayed);
        
        // Calculate win rate
        int total = parseIntOrDefault(totalGames, 0);
        int won = parseIntOrDefault(gamesWon, 0);
        double winRate = total > 0 ? (won * 100.0 / total) : 0.0;
        request.setAttribute("cookieWinRate", String.format("%.1f", winRate));
        
        // Get history from model if available
        if (model instanceof GameModel) {
            GameModel gameModel = (GameModel) model;
            request.setAttribute("cookieHistorySize", gameModel.getGameHistory().size());
        }
    }
    
    /**
     * Updates the last played timestamp cookie.
     *
     * @param response the HttpServletResponse object
     */
    private void updateLastPlayedCookie(HttpServletResponse response) {
        String timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        CookieManager.setCookie(response, COOKIE_LAST_PLAYED, timestamp);
    }
    
    /**
     * Parses string to int with default value.
     *
     * @param str          the string to parse
     * @param defaultValue the default value if parsing fails
     * @return parsed integer or default value
     */
    private int parseIntOrDefault(String str, int defaultValue) {
        if (str == null) return defaultValue;
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Processes a user's guess and updates the model.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @param model    the shared game model
     * @throws InvalidGuessException if validation fails
     */
    private void handleGuess(HttpServletRequest request, HttpServletResponse response,
                            AbstractGameModel model) throws InvalidGuessException {
        
        if (!model.hasAttemptsLeft()) {
            request.setAttribute("errorMessage", 
                "No attempts left! Please restart the game.");
            return;
        }
        
        if (model.isGameWon()) {
            request.setAttribute("errorMessage", 
                "Game already won! Please restart to play again.");
            return;
        }
        
        String guess = request.getParameter("guess");
        
        if (guess == null || guess.trim().isEmpty()) {
            throw new InvalidGuessException("No word provided. Please enter a word.");
        }
                
        guess = guess.trim().toUpperCase();
        
        // Validate and process
        validator.isValid(guess);
        model.checkGuess(guess);
        
        // Check game result
        if (model.isCorrect(guess)) {
            request.setAttribute("resultMessage", 
                "Congratulations! You won! The word was: " + model.getSecretWord());
        } else if (!model.hasAttemptsLeft()) {
            request.setAttribute("resultMessage", 
                "Game over! The correct word was: " + model.getSecretWord());
        }
    }
    
    /**
     * Restarts the game by resetting the model state.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @param model    the shared game model
     */
    private void handleRestart(HttpServletRequest request, HttpServletResponse response,
                              AbstractGameModel model) {
        model.restart();
    }
    
    /**
     * Sets all required attributes for the JSP view.
     *
     * @param request the HttpServletRequest object
     * @param model   the game model
     */
    private void setViewAttributes(HttpServletRequest request, AbstractGameModel model) {
        request.setAttribute("results", model.getResults());
        request.setAttribute("attemptsLeft", model.hasAttemptsLeft());
        request.setAttribute("gameWon", model.isGameWon());
        request.setAttribute("maxAttempts", model.getMaxAttempts());
        
        if (model.getResults() != null) {
            request.setAttribute("currentAttempt", model.getResults().size());
        }
    }
    
    @Override
    public String getServletInfo() {
        return "Game Servlet for Wordle 5x5 with game state and history cookies";
    }
}
