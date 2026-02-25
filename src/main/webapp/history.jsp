<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Game History - Wordle 5x5</title>
    <style>
        body { 
            font-family: Arial, sans-serif; 
            padding: 20px; 
            background-color: #f5f5f5;
        }
        
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        h1 { 
            color: #333; 
            text-align: center;
            margin-bottom: 30px;
        }
        
        h2 {
            color: #555;
            margin-top: 40px;
            margin-bottom: 20px;
        }
        
        /* Statistics Section */
        .stats {
            display: flex;
            justify-content: space-around;
            margin: 30px 0;
            padding: 20px;
            background: #e3f2fd;
            border-radius: 8px;
        }
        
        .stat-item {
            text-align: center;
        }
        
        .stat-value {
            font-size: 36px;
            font-weight: bold;
            color: #2196F3;
        }
        
        .stat-label {
            font-size: 14px;
            color: #666;
            margin-top: 5px;
        }
        
        /* Current Game Section */
        .current-game {
            margin: 30px 0;
            padding: 20px;
            background: #fff3e0;
            border-left: 4px solid #ff9800;
            border-radius: 4px;
        }
        
        /* Game Record Card */
        .game-record {
            margin: 20px 0;
            padding: 20px;
            background: #f9f9f9;
            border: 1px solid #ddd;
            border-radius: 8px;
        }
        
        .game-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
            padding-bottom: 10px;
            border-bottom: 2px solid #ddd;
        }
        
        .game-result {
            font-size: 20px;
            font-weight: bold;
        }
        
        .won { color: #4CAF50; }
        .lost { color: #f44336; }
        
        /* Attempt List */
        .attempt-list {
            list-style: none;
            padding: 0;
        }
        
        .attempt-item {
            padding: 15px;
            margin: 10px 0;
            background: white;
            border: 1px solid #e0e0e0;
            border-radius: 4px;
            display: flex;
            align-items: center;
            gap: 15px;
        }
        
        .attempt-number {
            font-weight: bold;
            color: #666;
            min-width: 70px;
        }
        
        .attempt-word {
            font-weight: bold;
            font-size: 18px;
            letter-spacing: 2px;
            min-width: 120px;
        }
        
        /* Colored Feedback Boxes */
        .feedback-boxes {
            display: flex;
            gap: 5px;
        }
        
        .letter-box {
            width: 40px;
            height: 40px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
            font-size: 18px;
            color: white;
            border-radius: 4px;
            text-transform: uppercase;
        }
        
        /* Colors for LetterStatus enum */
        .status-correct {
            background-color: #4CAF50; /* Green - correct position */
        }
        
        .status-wrong-position {
            background-color: #FF9800; /* Orange - wrong position */
        }
        
        .status-not-present {
            background-color: #9E9E9E; /* Gray - not in word */
        }
        
        /* Back Link */
        .back-link {
            display: inline-block;
            margin-top: 30px;
            padding: 12px 30px;
            background: #4CAF50;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            font-weight: bold;
        }
        
        .back-link:hover {
            background: #45a049;
        }
        
        /* Empty State */
        .empty-history {
            text-align: center;
            padding: 60px 20px;
            color: #999;
            font-style: italic;
        }
        
        /* Game Info */
        .game-info {
            margin: 10px 0;
            color: #666;
        }
        
        .game-info strong {
            color: #333;
        }
        
        /* Responsive Design */
        @media (max-width: 768px) {
            .stats {
                flex-direction: column;
                gap: 15px;
            }
            
            .attempt-item {
                flex-direction: column;
                align-items: flex-start;
            }
            
            .letter-box {
                width: 35px;
                height: 35px;
                font-size: 16px;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Game History</h1>
        
        <!-- Statistics Section -->
        <div class="stats">
            <div class="stat-item">
                <div class="stat-value">${totalGames != null ? totalGames : 0}</div>
                <div class="stat-label">Games Played</div>
            </div>
            <div class="stat-item">
                <div class="stat-value">${gamesWon != null ? gamesWon : 0}</div>
                <div class="stat-label">Games Won</div>
            </div>
            <div class="stat-item">
                <div class="stat-value">${winPercentage != null ? winPercentage : '0.0'}%</div>
                <div class="stat-label">Win Rate</div>
            </div>
        </div>
        
        <!-- Current Game Section -->
        <c:if test="${currentGameActive}">
            <div class="current-game">
                <h3>Current Game (In Progress)</h3>
                <ul class="attempt-list">
                    <c:forEach var="result" items="${currentResults}" varStatus="status">
                        <li class="attempt-item">
                            <span class="attempt-number">Try ${status.index + 1}:</span>
                            <span class="attempt-word">${result.attempt()}</span>
                            <div class="feedback-boxes">
                                <c:forEach var="letterStatus" items="${result.feedback()}">
                                    <c:set var="cssClass" value=""/>
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
                                    <div class="letter-box ${cssClass}">${letterStatus.symbol}</div>
                                </c:forEach>
                            </div>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>
        
        <!-- Game History Section -->
        <h2>Previous Games</h2>
        
        <c:choose>
            <c:when test="${not empty gameHistory}">
                <c:forEach var="game" items="${gameHistory}" varStatus="gameStatus">
                    <div class="game-record">
                        <div class="game-header">
                            <div>
                                <strong>Game #${gameHistory.size() - gameStatus.index}</strong>
                                <span style="color: #666; margin-left: 10px;">
                                    ${game.formattedTimestamp}
                                </span>
                            </div>
                            <div class="game-result ${game.won ? 'won' : 'lost'}">
                                ${game.won ? 'WON' : 'LOST'}
                            </div>
                        </div>
                        
                        <div class="game-info">
                            <strong>Secret Word:</strong> ${game.secretWord} | 
                            <strong>Attempts:</strong> ${game.attemptCount}
                        </div>
                        
                        <ul class="attempt-list">
                            <c:forEach var="attempt" items="${game.attempts}" varStatus="attemptStatus">
                                <li class="attempt-item">
                                    <span class="attempt-number">Try ${attemptStatus.index + 1}:</span>
                                    <span class="attempt-word">${attempt.attempt()}</span>
                                    <div class="feedback-boxes">
                                        <c:forEach var="letterStatus" items="${attempt.feedback()}" varStatus="letterIndex">
                                            <c:set var="cssClass" value=""/>
                                            <c:set var="letter" value="${attempt.attempt().charAt(letterIndex.index)}"/>
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
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="empty-history">
                    <p>No completed games yet. Finish a game to see it here!</p>
                </div>
            </c:otherwise>
        </c:choose>
        
        <a href="${pageContext.request.contextPath}/game" class="back-link">⬅️ Back to Game</a>
    </div>
</body>
</html>
