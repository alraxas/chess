package org.example.oopchess.rules;

import org.example.oopchess.enums.GameState;
import org.example.oopchess.enums.PieceColor;
import org.example.oopchess.models.board.*;
import org.example.oopchess.models.pieces.Piece;
import org.example.oopchess.models.player.Player;

import java.util.*;
import java.util.stream.Collectors;

public class GameController {
    private Board board;
    private Map<PieceColor, Player> players;
    private Queue<PieceColor> turnQueue;
    private PieceColor currentPlayerColor;
    private GameState gameState;
    private Move lastMove;
    private Map<PieceColor, GameState> playerStatuses;

    public GameController() {
        initGame();
    }

    public void initGame() {
        board = new Board();
        board.initBoard();

        players = new HashMap<>();
        players.put(PieceColor.WHITE, new Player(PieceColor.WHITE, "Игрок 1"));
        players.put(PieceColor.BLACK, new Player(PieceColor.BLACK, "Игрок 2"));

        playerStatuses = new HashMap<>();
        playerStatuses.put(PieceColor.WHITE, GameState.PLAYING);
        playerStatuses.put(PieceColor.BLACK, GameState.PLAYING);

        // Очередь ходов (белые начинают)
        turnQueue = new LinkedList<>();
        turnQueue.offer(PieceColor.WHITE);
        turnQueue.offer(PieceColor.BLACK);

        currentPlayerColor = turnQueue.peek(); // Текущий игрок - первый в очереди
        gameState = GameState.PLAYING;
        lastMove = null;
    }

    public Move makeMove(int fr, int fc, int tr, int tc) {
        if (gameState != GameState.PLAYING && gameState != GameState.CHECK) return null;

        Piece piece = board.getPiece(new Position(fr, fc));
        if (piece == null || piece.getColor() != currentPlayerColor) return null;

        List<Move> possibleMoves = board.getValidMoves(piece, fr, fc);
        Move selectedMove = null;

        for (Move move : possibleMoves) {
            if (move.getToRow() == tr && move.getToCol() == tc) {
                selectedMove = move;
                break;
            }
        }

        if (selectedMove == null) return null;

        if (board.makeMove(selectedMove)) {
            lastMove = selectedMove;
            updateGameState();
            switchPlayer();

            return selectedMove;
        }

        return null;
    }

    private void updateGameState() {
        PieceColor opponentColor = getOpponentColor();

        if (board.isCheckmate(opponentColor)) {
            gameState = GameState.WIN;
//            gameState = (opponentColor == PieceColor.WHITE) ? GameState.BLACK_WIN : GameState.WHITE_WIN;
            playerStatuses.put(opponentColor, GameState.CHECKMATE);
            playerStatuses.put(getCurrentPlayerColor(), GameState.WIN);
            return;
        }

        if (board.isCheck(opponentColor)) {
            gameState = GameState.CHECK;
            playerStatuses.put(opponentColor, GameState.CHECK);
        } else if (board.isStalemate(opponentColor)) {
            gameState = GameState.STALEMATE;
            playerStatuses.put(opponentColor, GameState.STALEMATE);
        } else {
            gameState = GameState.PLAYING;
            playerStatuses.put(opponentColor, GameState.PLAYING);
        }
    }

    private PieceColor getOpponentColor() {
        // Получаем следующего игрока в очереди (противника)
        Iterator<PieceColor> iterator = turnQueue.iterator();
        iterator.next(); // Пропускаем текущего игрока
        return iterator.next();
    }

    private void switchPlayer() {
        PieceColor playedColor = turnQueue.poll(); // Убираем текущего игрока из начала очереди
        turnQueue.offer(playedColor); // Добавляем его в конец очереди
        currentPlayerColor = turnQueue.peek(); // Обновляем текущего игрока

        System.out.println("Switched to player: " + currentPlayerColor);
    }

    public void resign() {
        PieceColor resigningPlayer = currentPlayerColor;
        PieceColor winner = (resigningPlayer == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;

//        gameState = (winner == PieceColor.WHITE) ? GameState.WHITE_WIN : GameState.BLACK_WIN;
        playerStatuses.put(resigningPlayer, GameState.RESIGNED);
        playerStatuses.put(winner, GameState.WIN);
    }

    public void offerDraw() {}

    public void acceptDraw() {
        gameState = GameState.DRAW;
    }

    public void undoLastMove() {
        if (!board.getMoveHistory().isEmpty()) {
            board.undoMove();
            // При отмене хода возвращаем предыдущего игрока
            switchPlayerBack();
            gameState = GameState.PLAYING;
            playerStatuses.put(PieceColor.WHITE, GameState.PLAYING);
            playerStatuses.put(PieceColor.BLACK, GameState.PLAYING);
        }
    }

    private void switchPlayerBack() {
        // Для отмены хода нужно вернуть предыдущего игрока
        // Убираем последнего игрока из конца очереди
        PieceColor lastPlayer = null;
        Iterator<PieceColor> iterator = turnQueue.iterator();
        while (iterator.hasNext()) {
            lastPlayer = iterator.next();
        }

        if (lastPlayer != null) {
            // Создаем новую очередь с правильным порядком
            Queue<PieceColor> newQueue = new LinkedList<>();
            newQueue.offer(lastPlayer); // Последний становится первым
            for (PieceColor color : turnQueue) {
                if (color != lastPlayer) {
                    newQueue.offer(color);
                }
            }
            turnQueue = newQueue;
            currentPlayerColor = turnQueue.peek();
        }
    }

    public Board getBoard() { return board; }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerColor);
    }

    public PieceColor getCurrentPlayerColor() {
        return currentPlayerColor;
    }

    public Player getPlayer(PieceColor color) {
        return players.get(color);
    }

    public GameState getPlayerStatus(PieceColor color) {
        return playerStatuses.get(color);
    }

    public GameState getGameState() { return gameState; }

    public Move getLastMove() { return lastMove; }

    public List<Position> getValidMovesForPiece(int row, int col) {
        Piece piece = board.getPiece(new Position(row, col));

        if (piece != null && piece.getColor() == currentPlayerColor) {
            return board.getValidMoves(piece, row, col).stream()
                    .map(move -> new Position(move.getToRow(), move.getToCol()))
                    .collect(Collectors.toList());
        }

        return List.of();
    }

    public List<PieceColor> getTurnOrder() {
        return new ArrayList<>(turnQueue);
    }
}
