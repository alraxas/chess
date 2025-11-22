package org.example.oopchess.models.board;

import org.example.oopchess.enums.PieceColor;
import org.example.oopchess.enums.PieceType;
import org.example.oopchess.rules.MoveValidator;
import org.example.oopchess.models.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private Piece[][] squares;
    private List<Move> moveHistory;
    private MoveValidator moveValidator;

    public Board() {
        this.squares = new Piece[8][8];
        this.moveHistory = new ArrayList<>();
        this.moveValidator = new MoveValidator(this);
        initBoard();
    }

    private void initBoard() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                squares[r][c] = null;
            }
        }

        for (int i = 0; i < 8; i++) {
            squares[1][i] = new Piece(PieceType.PAWN, PieceColor.BLACK);
            squares[6][i] = new Piece(PieceType.PAWN, PieceColor.WHITE);
        }

        squares[0][0] = new Piece(PieceType.ROOK, PieceColor.BLACK);
        squares[0][7] = new Piece(PieceType.ROOK, PieceColor.BLACK);
        squares[7][0] = new Piece(PieceType.ROOK, PieceColor.WHITE);
        squares[7][7] = new Piece(PieceType.ROOK, PieceColor.WHITE);

        squares[0][1] = new Piece(PieceType.KNIGHT, PieceColor.BLACK);
        squares[0][6] = new Piece(PieceType.KNIGHT, PieceColor.BLACK);
        squares[7][1] = new Piece(PieceType.KNIGHT, PieceColor.WHITE);
        squares[7][6] = new Piece(PieceType.KNIGHT, PieceColor.WHITE);

        squares[0][2] = new Piece(PieceType.BISHOP, PieceColor.BLACK);
        squares[0][5] = new Piece(PieceType.BISHOP, PieceColor.BLACK);
        squares[7][2] = new Piece(PieceType.BISHOP, PieceColor.WHITE);
        squares[7][5] = new Piece(PieceType.BISHOP, PieceColor.WHITE);

        squares[0][3] = new Piece(PieceType.QUEEN, PieceColor.BLACK);
        squares[7][3] = new Piece(PieceType.QUEEN, PieceColor.WHITE);

        squares[0][4] = new Piece(PieceType.KING, PieceColor.BLACK);
        squares[7][4] = new Piece(PieceType.KING, PieceColor.WHITE);
    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    public Piece getPiece(int row, int col) {
        if (isValidPosition(row, col)) return squares[row][col];
        return null;
    }

    public void setPiece(int row, int col, Piece piece) {
        if (isValidPosition(row, col)) squares[row][col] = piece;
    }

    public boolean makeMove(Move move) {
        if (!moveValidator.isValidMove(move)) return false;

        Piece piece = move.getPiece();
        Piece target = getPiece(move.getToRow(), move.getToCol());

        move.setCapturedPiece(target);

        setPiece(move.getToRow(), move.getToCol(), piece);
        setPiece(move.getFromRow(), move.getFromCol(), null);
        piece.setMoved(true);

        moveHistory.add(move);
        return true;
    }

    public void undoMove() {
        if (moveHistory.isEmpty()) return;

        Move last = moveHistory.remove(moveHistory.size() - 1);
        Piece moved = last.getPiece();

        setPiece(last.getFromRow(), last.getFromCol(), moved);
        setPiece(last.getToRow(), last.getToCol(), last.getCapturedPiece());
        moved.setMoved(false);
    }

    public List<Move> getMoveHistory() {
        return new ArrayList<>(moveHistory);
    }

    public MoveValidator getMoveValidator() {
        return moveValidator;
    }
}
