package org.example.oopchess.controller;

import org.example.oopchess.enums.GameState;
import org.example.oopchess.enums.PieceColor;
import org.example.oopchess.models.board.Board;
import org.example.oopchess.models.board.Move;
import org.example.oopchess.models.board.Position;
import org.example.oopchess.models.pieces.Piece;
import org.example.oopchess.models.player.Player;

import java.util.List;
import java.util.stream.Collectors;

public class GameController {
    private Board board;
    private Player whitePlayer;
    private Player blackPlayer;
    private Player currentPlayer;
    private GameState gameState;
    private Move lastMove;

    public GameController() {
        initGame();
    }
    public void initGame() {
        board = new Board();
        whitePlayer = new Player(PieceColor.WHITE, "Player-1");
        blackPlayer = new Player(PieceColor.BLACK, "Player-2");
        currentPlayer = whitePlayer;
        gameState = GameState.PLAYING;
        lastMove = null;
    }

    public boolean makeMove(int fr, int fc, int tr, int tc) {
        if (gameState != GameState.PLAYING) return false;

        Piece piece = board.getPiece(fr, fc);
        if (piece == null || piece.getColor() != currentPlayer.getColor()) return false;

        Move move = new Move(fr, fc, tr, tc, piece);
        if (board.makeMove(move)) {
            lastMove = move;

            if (board.getMoveValidator().isCheckmate(getOpponentColor())) {
                gameState = (currentPlayer.getColor() == PieceColor.BLACK ? GameState.WHITE_WIN : GameState.BLACK_WIN);
            } else if (isStalemate()) {
                gameState = GameState.STALEMATE;
            }

            switchPlayer();
            return true;
        }
        return false;
    }

    private PieceColor getOpponentColor() {
        return currentPlayer == whitePlayer ? PieceColor.WHITE : PieceColor.BLACK;
    }

    private boolean isStalemate() {
        return board.getMoveValidator().getAllValidMoves(currentPlayer.getColor()).isEmpty() &&
                !board.getMoveValidator().isCheck(currentPlayer.getColor());
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == whitePlayer) ? blackPlayer : whitePlayer;
    }

    public void undoLastMove() {
        if (!board.getMoveHistory().isEmpty()) {
            board.undoMove();
            switchPlayer();
            gameState = GameState.PLAYING;
        }
    }

    public Board getBoard() { return board; }

    public Player getCurrentPlayer() { return currentPlayer; }

    public GameState getGameState() { return gameState; }

    public Move getLastMove() { return lastMove; }

    public List<Move> getValidMovesForPiece1(int row, int col) {
        Piece piece = board.getPiece(row, col);
        if (piece != null && piece.getColor() == currentPlayer.getColor()) {
            List<Move> validPositions = board.getMoveValidator().getValidMoves(piece, row, col);
            // Конвертируем Position в Move
            return validPositions.stream()
                    .map(pos -> new Move(row, col, pos.getToRow(), pos.getToCol(), piece))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public List<Position> getValidMovesForPiece(int row, int col) {
        Piece piece = board.getPiece(row, col);

        if (piece != null && piece.getColor() == currentPlayer.getColor()) {
            // Возвращаем именно позиции
            return board.getMoveValidator().getValidMoves(piece, row, col).stream()
                    .map(move -> new Position(move.getToRow(), move.getToCol()))
                    .collect(Collectors.toList());
        }

        return List.of();
    }
}
