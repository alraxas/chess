package org.example.oopchess.rules;

import org.example.oopchess.enums.GameState;
import org.example.oopchess.enums.PieceColor;
import org.example.oopchess.models.board.*;
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
        board.initBoard();
        //TODO: сделать цвета в мапе и игроков в очереди и игров занести в мапу с цветами??
        whitePlayer = new Player(PieceColor.WHITE, "Player-1");
        blackPlayer = new Player(PieceColor.BLACK, "Player-2");
        currentPlayer = whitePlayer;
        gameState = GameState.PLAYING;
        lastMove = null;
    }

    public Move makeMove(int fr, int fc, int tr, int tc) {
        if (gameState != GameState.PLAYING && gameState != GameState.CHECK) return null;

        Piece piece = board.getPiece(new Position(fr, fc));
        if (piece == null || piece.getColor() != currentPlayer.getColor()) return null;

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

            if (board.isCheckmate(getOpponentColor())) {
                gameState = (currentPlayer.getColor() == PieceColor.BLACK ? GameState.BLACK_WIN : GameState.WHITE_WIN);
            } else if (board.isCheck(getOpponentColor())) {
                gameState = GameState.CHECK;
            } else if (board.isStalemate(currentPlayer.getColor())) {
                gameState = GameState.STALEMATE;
            } else {
                gameState = GameState.PLAYING;
            }

            switchPlayer();
        }

        return selectedMove;
    }


    private PieceColor getOpponentColor() {
        return currentPlayer.getColor() == PieceColor.WHITE ? PieceColor.BLACK : PieceColor.WHITE;
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

    public List<Position> getValidMovesForPiece(int row, int col) {
        Piece piece = board.getPiece(new Position(row, col));

        if (piece != null && piece.getColor() == currentPlayer.getColor()) {
            return board.getMoveValidator().getValidMoves(piece, row, col).stream()
                    .map(move -> new Position(move.getToRow(), move.getToCol()))
                    .collect(Collectors.toList());
        }

        return List.of();
    }
}
