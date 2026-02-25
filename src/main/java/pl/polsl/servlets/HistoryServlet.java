package pl.polsl.servlets;

import java.io.IOException;
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

/**
 * Servlet responsible for displaying game history.
 * <p>
 * This servlet provides access to the historical data stored in the model.
 * Both GET and POST requests are handled by the same processing method
 * to avoid code duplication.
 * </p>
 *
 * @author Maciej Porebski
 * @version 2.0
 */
@WebServlet(name = "HistoryServlet", urlPatterns = {"/history"})
public class HistoryServlet extends HttpServlet {
    
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
     * This method eliminates code duplication by handling both request types
     * in a single place. It retrieves historical data from the shared model
     * and forwards to the history view.
     * </p>
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Get shared model from application scope
        ServletContext context = getServletContext();
        AbstractGameModel model = (AbstractGameModel) context.getAttribute(
            GameContextListener.MODEL_ATTRIBUTE);
        
        if (model == null) {
            request.setAttribute("errorMessage", "No game data available");
        } else {
            // Set history data attributes
            request.setAttribute("gameHistory", model.getGameHistory());
            request.setAttribute("currentResults", model.getResults());
            request.setAttribute("currentGameActive", 
                !model.getResults().isEmpty() && (model.hasAttemptsLeft() || model.isGameWon()));
            
            // Set statistics if GameModel is used
            if (model instanceof GameModel) {
                GameModel gameModel = (GameModel) model;
                request.setAttribute("totalGames", gameModel.getTotalGamesPlayed());
                request.setAttribute("gamesWon", gameModel.getTotalGamesWon());
                request.setAttribute("winPercentage", 
                    String.format("%.1f", gameModel.getWinPercentage()));
            }
        }
        
        // Forward to history view
        RequestDispatcher dispatcher = request.getRequestDispatcher("/history.jsp");
        dispatcher.forward(request, response);
    }
    
    /**
     * Returns information about the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "History Servlet for Wordle 5x5 - displays game history and statistics";
    }
}
