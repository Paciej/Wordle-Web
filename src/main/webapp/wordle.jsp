<%-- 
    Document   : wordle
    Created on : 12 gru 2025, 15:00:00
    Author     : Maciej Porebski
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Wordle 5x5</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Arial', sans-serif;
            background-color: #f5f5f5;
            padding: 20px;
            position: relative;
        }
        
        /* Header with title - CENTERED */
        h1 {
            text-align: center;
            font-size: 28px;
            font-weight: bold;
            color: #333;
            margin-bottom: 20px;
        }
        
        /* Instructions box */
        .instructions {
            position: absolute;
            left: 20px;
            top: 80px;
            max-width: 420px;
            border: 2px solid #ccc;
            padding: 20px;
            background-color: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        
        .instructions p {
            line-height: 1.6;
            color: #555;
            font-size: 14px;
        }
        
        .instructions .legend {
            margin-top: 15px;
            padding-top: 15px;
            border-top: 1px solid #ddd;
        }
        
        .legend-item {
            display: flex;
            align-items: center;
            margin: 8px 0;
        }
        
        .legend-box {
            width: 30px;
            height: 30px;
            margin-right: 10px;
            border-radius: 4px;
        }
        
        .legend-correct { background-color: #4CAF50; }
        .legend-wrong { background-color: #FF9800; }
        .legend-not { background-color: #9E9E9E; }
        
        /* Main game container */
        .game-container {
            max-width: 800px;
            margin: 0 auto;
            text-align: center;
        }
        
        /* Navigation */
        .navigation {
            margin-bottom: 20px;
        }
        
        .navigation a {
            margin: 0 10px;
            text-decoration: none;
            color: #2196F3;
            font-weight: bold;
        }
        
        .navigation a:hover {
            text-decoration: underline;
        }
        
        /* Form styling */
        .input-form {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 10px;
            margin-bottom: 20px;
        }
        
        .input-form label {
            font-size: 16px;
            font-weight: bold;
            color: #333;
        }
        
        .input-form input[type="text"] {
            padding: 10px 15px;
            font-size: 16px;
            width: 250px;
            border: 2px solid #ddd;
            border-radius: 4px;
            text-transform: uppercase;
        }
        
        .input-form input[type="text"]:focus {
            outline: none;
            border-color: #4CAF50;
        }
        
        button {
            padding: 10px 25px;
            font-size: 16px;
            cursor: pointer;
            border: none;
            border-radius: 4px;
            font-weight: bold;
            transition: background-color 0.3s;
        }
        
        .submit-btn {
            background-color: #4CAF50;
            color: white;
        }
        
        .submit-btn:hover {
            background-color: #45a049;
        }
        
        .restart-btn {
            background-color: #f44336;
            color: white;
            margin-left: 10px;
        }
        
        .restart-btn:hover {
            background-color: #da190b;
        }
        
        .history-btn {
            background-color: #2196F3;
            color: white;
            margin-left: 10px;
        }
        
        .history-btn:hover {
            background-color: #1976D2;
        }
        
        /* Message label (error/result) */
        .message-label {
            min-height: 30px;
            margin: 15px 0;
            padding: 10px;
            border-radius: 4px;
            font-weight: bold;
            font-size: 16px;
        }
        
        .message-label.error {
            background-color: #ffebee;
            color: #c62828;
            border: 1px solid #ef5350;
        }
        
        .message-label.success {
            background-color: #e8f5e9;
            color: #2e7d32;
            border: 1px solid #66bb6a;
        }
        
        .message-label.info {
            background-color: #fff3e0;
            color: #e65100;
            border: 1px solid #ff9800;
        }
        
        .message-label.empty {
            background-color: transparent;
            border: none;
        }
        
        /* Attempts counter */
        .attempts-counter {
            margin: 20px 0;
            padding: 15px;
            background-color: #e3f2fd;
            border-left: 4px solid #2196F3;
            border-radius: 4px;
            font-size: 18px;
            font-weight: bold;
            color: #1565c0;
        }
        
        /* Results section */
        .results-section {
            margin-top: 30px;
        }
        
        .results-section h3 {
            font-size: 22px;
            color: #333;
            margin-bottom: 15px;
        }
        
        /* Results list with colored boxes */
        .results-list {
            list-style: none;
            padding: 0;
            max-width: 700px;
            margin: 0 auto;
        }
        
        .result-item {
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 15px 20px;
            margin-bottom: 10px;
            background-color: #fff;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-shadow: 0 2px 3px rgba(0,0,0,0.05);
            gap: 15px;
        }
        
        .result-item:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
            transition: all 0.2s;
        }
        
        .result-item .attempt-number {
            font-weight: bold;
            font-size: 18px;
            color: #666;
            min-width: 80px;
            text-align: left;
        }
        
        /* Colored Feedback Boxes */
        .feedback-boxes {
            display: flex;
            gap: 5px;
        }
        
        .letter-box {
            width: 50px;
            height: 50px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
            font-size: 24px;
            color: white;
            border-radius: 4px;
            text-transform: uppercase;
            box-shadow: 0 2px 4px rgba(0,0,0,0.2);
        }
        
        /* Colors matching LetterStatus enum */
        .status-correct {
            background-color: #4CAF50; /* Green - correct position */
        }
        
        .status-wrong-position {
            background-color: #FF9800; /* Orange - wrong position */
        }
        
        .status-not-present {
            background-color: #9E9E9E; /* Gray - not in word */
        }
        
        /* Empty state */
        .empty-results {
            padding: 40px;
            color: #999;
            font-style: italic;
        }
        
        /* Game status */
        .game-status {
            margin-top: 20px;
            color: #666;
        }
        
        /* Cookie Info Section - Updated styles */
        .cookie-info {
            margin: 40px auto 20px;
            max-width: 700px;
            padding: 25px;
            background: #f9f9f9;
            border: 2px solid #ddd;
            border-radius: 8px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.1);
        }

        .cookie-info h3 {
            text-align: center;
            margin-bottom: 20px;
            font-size: 22px;
            color: #333;
        }

        .cookie-section {
            margin-bottom: 20px;
            padding: 15px;
            background: white;
            border-radius: 6px;
            border-left: 4px solid #2196F3;
        }

        .cookie-section h4 {
            margin-bottom: 10px;
            color: #555;
            font-size: 16px;
        }

        .cookie-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 10px;
        }

        .cookie-item {
            padding: 10px;
            background: #f5f5f5;
            border-radius: 4px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .cookie-label {
            font-weight: bold;
            color: #666;
        }

        .cookie-value {
            font-family: 'Courier New', monospace;
            color: #2196F3;
            font-weight: bold;
        }

        .cookie-note {
            margin-top: 15px;
            padding: 10px;
            background: #fff3cd;
            border-left: 4px solid #ffc107;
            border-radius: 4px;
            text-align: center;
        }

        .cookie-note small {
            color: #856404;
        }
        
        /* Responsive design */
        @media (max-width: 768px) {
            .instructions {
                margin-bottom: 20px;
                max-width: 100%;
            }
            
            .game-container {
                padding-top: 20px;
            }
            
            h1 {
                margin-bottom: 20px;
            }
            
            .input-form {
                flex-direction: column;
            }
            
            .input-form input[type="text"] {
                width: 100%;
            }
            
            .result-item {
                flex-direction: column;
                text-align: center;
            }
            
            .result-item .attempt-number {
                min-width: auto;
                text-align: center;
                margin-bottom: 10px;
            }
            
            .letter-box {
                width: 45px;
                height: 45px;
                font-size: 20px;
            }
        }
    </style>
</head>
<body>
    <!-- Title - CENTERED -->
    <h1>Wordle 5x5</h1>
    
    <!-- Instructions box -->
    <div class="instructions">
        <p>
            Word Game 5x5 is about guessing a 5-letter word in 5 tries. 
            After every word you receive feedback about your letters:
        </p>
        <div class="legend">
            <div class="legend-item">
                <div class="legend-box legend-correct"></div>
                <span>Letter is in the correct position</span>
            </div>
            <div class="legend-item">
                <div class="legend-box legend-wrong"></div>
                <span>Letter is in wrong position</span>
            </div>
            <div class="legend-item">
                <div class="legend-box legend-not"></div>
                <span>Letter is not in the word</span>
            </div>
        </div>
    </div>
    
    <!-- Main game container -->
    <div class="game-container">
                
        <!-- Input form WITHOUT HTML5 validation -->
        <form method="post" action="${pageContext.request.contextPath}/game" class="input-form" novalidate>
            <label for="guess">Insert answer:</label>
            <input type="text" name="guess" id="guess" maxlength="5"
                   autocomplete="off">
            <input type="hidden" name="action" value="guess">
            <button type="submit" class="submit-btn">Submit</button>
        </form>
        
        <!-- Action buttons -->
        <div style="margin: 10px 0;">
            <form method="post" action="${pageContext.request.contextPath}/game" style="display: inline;">
                <input type="hidden" name="action" value="restart">
                <button type="submit" class="restart-btn">Restart</button>
            </form>
            <a href="${pageContext.request.contextPath}/history">
                <button type="button" class="history-btn">View History</button>
            </a>
        </div>
        
        <!-- Message label (shows errors or game results) -->
        <div class="message-label ${not empty errorMessage ? 'error' : (not empty resultMessage ? (gameWon ? 'success' : 'info') : 'empty')}">
            <c:choose>
                <c:when test="${not empty errorMessage}">
                    ${errorMessage}
                </c:when>
                <c:when test="${not empty resultMessage}">
                    ${resultMessage}
                </c:when>
                <c:otherwise>
                    &nbsp;
                </c:otherwise>
            </c:choose>
        </div>
            
        <!-- Attempts counter - CHANGED TO 5 -->
        <c:if test="${not empty results}">
            <div class="attempts-counter">
                Attempts made: ${results.size()} / ${maxAttempts != null ? maxAttempts : 5}
                <c:if test="${attemptsLeft}">
                    | Remaining: ${maxAttempts != null ? maxAttempts - results.size() : 5 - results.size()}
                </c:if>
            </div>
        </c:if>
        
        <!-- Results section with colored boxes -->
        <div class="results-section">
            <h3>Game Board:</h3>
            
            <c:choose>
                <c:when test="${not empty results}">
                    <ul class="results-list">
                        <c:forEach var="result" items="${results}" varStatus="status">
                            <li class="result-item">
                                <span class="attempt-number">Try ${status.index + 1}:</span>
                                <div class="feedback-boxes">
                                    <c:forEach var="letterStatus" items="${result.feedback()}" varStatus="letterIndex">
                                        <c:set var="cssClass" value=""/>
                                        <c:set var="letter" value="${result.attempt().charAt(letterIndex.index)}"/>
                                        <c:choose>
                                            <c:when test="${letterStatus.name() == 'CORRECT'}">
                                                <c:set var="cssClass" value="status-correct"/>
                                            </c:when>
                                            <c:when test="${letterStatus.name() == 'WRONG_POSITION'}">
                                                <c:set var="cssClass" value="status-wrong-position"/>
                                            </c:when>
                                            <c:when test="${letterStatus.name() == 'NOT_PRESENT'}">
                                                <c:set var="cssClass" value="status-not-present"/>
                                            </c:when>
                                        </c:choose>
                                        <div class="letter-box ${cssClass}">${letter}</div>
                                    </c:forEach>
                                </div>
                            </li>
                        </c:forEach>
                    </ul>
                </c:when>
            </c:choose>
        </div>
        
        <!-- Additional game status -->
        <c:if test="${not empty attemptsLeft and not gameWon}">
            <p class="game-status">
                <c:choose>
                    <c:when test="${!attemptsLeft}">
                        Game over! Click Restart to try again.
                    </c:when>
                </c:choose>
            </p>
        </c:if>
        
        <!-- Cookie Data Display Section - BELOW GAME -->
        <div class="cookie-info">
            <h3>Game State and History (from cookies)</h3>
            
            <div class="cookie-section">
                <h4>Current Game State</h4>
                <div class="cookie-grid">
                    <div class="cookie-item">
                        <span class="cookie-label">Attempts Made:</span>
                        <span class="cookie-value">${cookieCurrentAttempts}</span>
                    </div>
                    <div class="cookie-item">
                        <span class="cookie-label">Game Status:</span>
                        <span class="cookie-value">
                            <c:choose>
                                <c:when test="${cookieGameWon == 'true'}">Won</c:when>
                                <c:when test="${cookieCurrentAttempts == '0'}">Not Started</c:when>
                                <c:otherwise>In Progress</c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                </div>
            </div>
            
            <div class="cookie-section">
                <h4>All-Time Statistics</h4>
                <div class="cookie-grid">
                    <div class="cookie-item">
                        <span class="cookie-label">Total Games:</span>
                        <span class="cookie-value">${cookieTotalGames}</span>
                    </div>
                    <div class="cookie-item">
                        <span class="cookie-label">Games Won:</span>
                        <span class="cookie-value">${cookieGamesWon}</span>
                    </div>
                    <div class="cookie-item">
                        <span class="cookie-label">Win Rate:</span>
                        <span class="cookie-value">${cookieWinRate}%</span>
                    </div>
                    <div class="cookie-item">
                        <span class="cookie-label">Last Played:</span>
                        <span class="cookie-value">
                            <c:choose>
                                <c:when test="${not empty cookieLastPlayed}">
                                    ${cookieLastPlayed}
                                </c:when>
                                <c:otherwise>Never</c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                </div>
            </div>
            
            <div class="cookie-note">
                <small>This data is stored in your browser cookies and persists across sessions.</small>
            </div>
        </div>
    </div>        
    
</body>
</html>
