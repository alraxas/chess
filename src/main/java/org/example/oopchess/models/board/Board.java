package org.example.oopchess.models.board;

import org.example.oopchess.enums.PieceColor;
import org.example.oopchess.models.pieces.*;
import org.example.oopchess.rules.MoveValidator;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private Piece[][] squares;
    private MoveValidator moveValidator;
    private List<Move> moveHistory;
    private Position enPassantTarget;

    public Board() {
        squares = new Piece[8][8];
        moveHistory = new ArrayList<>();
        moveValidator = new MoveValidator(this);
        initBoard();
    }

    public void initBoard() {
        for (int i = 0; i < 8; i++) {
            squares[1][i] = new Pawn(PieceColor.BLACK);
            squares[6][i] = new Pawn(PieceColor.WHITE);
        }

        squares[0][0] = new Rook(PieceColor.BLACK);
        squares[0][7] = new Rook(PieceColor.BLACK);
        squares[7][0] = new Rook(PieceColor.WHITE);
        squares[7][7] = new Rook(PieceColor.WHITE);

        squares[0][1] = new Knight(PieceColor.BLACK);
        squares[0][6] = new Knight(PieceColor.BLACK);
        squares[7][1] = new Knight(PieceColor.WHITE);
        squares[7][6] = new Knight(PieceColor.WHITE);

        squares[0][2] = new Bishop(PieceColor.BLACK);
        squares[0][5] = new Bishop(PieceColor.BLACK);
        squares[7][2] = new Bishop(PieceColor.WHITE);
        squares[7][5] = new Bishop(PieceColor.WHITE);

        squares[0][3] = new Queen(PieceColor.BLACK);
        squares[7][3] = new Queen(PieceColor.WHITE);

        squares[0][4] = new King(PieceColor.BLACK);
        squares[7][4] = new King(PieceColor.WHITE);
    }

    public Piece getPiece(Position pos) {
        if (isValidPosition(pos)) {
            return squares[pos.getRow()][pos.getCol()];
        }
        return null;
    }

    public void setPiece(Position pos, Piece piece) {
        if (isValidPosition(pos)) {
            squares[pos.getRow()][pos.getCol()] = piece;
        }
    }

    public boolean isValidPosition(Position pos) {
        return pos != null &&
                pos.getRow() >= 0 && pos.getRow() < 8 &&
                pos.getCol() >= 0 && pos.getCol() < 8;
    }

    public void setPiece(int row, int col, Piece piece) {
        if (isValidPosition(new Position(row, col))) {
            squares[row][col] = piece;
        }
    }

    public Position getEnPassantTarget() {
        return enPassantTarget;
    }

    public void setEnPassantTarget(Position target) {
        this.enPassantTarget = target;
    }

//    public boolean isEnPassantPossible(Position pawnPosition, int targetCol) {
//        if (enPassantTarget == null) return false;
//
//        // Проверяем, совпадает ли позиция с целью взятия на проходе
//        return enPassantTarget.getRow() == pawnPosition.getRow() &&
//                enPassantTarget.getCol() == targetCol;
//    }

    public boolean makeMove(Move move) {
        if (!moveValidator.isValidMove(move)) return false;

        Piece piece = move.getPiece();

        if (move.isEnPassant()) {
            performEnPassant(move);
        } else if (piece instanceof King && Math.abs(move.getFromCol() - move.getToCol()) == 2) {
            performCastling(move);
        } else {
            Piece target = getPiece(new Position(move.getToRow(), move.getToCol()));
            move.setCapturedPiece(target);

            setPiece(move.getToRow(), move.getToCol(), piece);
            setPiece(move.getFromRow(), move.getFromCol(), null);

            handlePawnPromotion(move);
        }

        piece.setMoved(true);
        moveHistory.add(move);

        // устанавливаем цель для взятия на проходе если пешка пошла на 2 клетки
        if (piece instanceof Pawn && Math.abs(move.getFromRow() - move.getToRow()) == 2) {
            int direction = (piece.getColor() == PieceColor.WHITE) ? -1 : 1;
            int enPassantRow = move.getFromRow() + direction;
            setEnPassantTarget(new Position(enPassantRow, move.getToCol()));
        } else { // сбрасываем цель после любого другого хода
            setEnPassantTarget(null);
        }

        return true;
    }

    private void performEnPassant(Move move) {
        Piece pawn = move.getPiece();

        setPiece(move.getToRow(), move.getToCol(), pawn);
        setPiece(move.getFromRow(), move.getFromCol(), null);

        setPiece(move.getFromRow(), move.getToCol(), null);
        System.out.println("убрали " + move.getFromRow() + " " + move.getToCol());
    }

    private void performCastling(Move move) {
        King king = (King) move.getPiece();
        int kingRow = move.getFromRow();
        int kingFromCol = move.getFromCol();
        int kingToCol = move.getToCol();

        boolean isKingSide = kingToCol > kingFromCol;
        int rookFromCol = isKingSide ? 7 : 0;
        int rookToCol = isKingSide ? kingToCol - 1 : kingToCol + 1;

        setPiece(kingRow, kingToCol, king);
        setPiece(kingRow, kingFromCol, null);

        Piece rook = getPiece(new Position(kingRow, rookFromCol));
        setPiece(kingRow, rookToCol, rook);
        setPiece(kingRow, rookFromCol, null);

        king.setMoved(true);
        if (rook != null) {
            rook.setMoved(true);
        }

        move.setCastling(true);
    }

    private void handlePawnPromotion(Move move) {
        Piece piece = move.getPiece();

        if (piece instanceof Pawn) {
            int promotionRow = (piece.getColor() == PieceColor.WHITE) ? 0 : 7;

            if (move.getToRow() == promotionRow) {
                // Здесь будет логика превращения пешки
            }
        }
    }

    public MoveValidator getMoveValidator() {
        return moveValidator;
    }

    public void undoMove() {
        if (moveHistory.isEmpty()) return;

        Move lastMove = moveHistory.remove(moveHistory.size() - 1);
        Piece movedPiece = lastMove.getPiece();

        setPiece(lastMove.getFromRow(), lastMove.getFromCol(), movedPiece);
        setPiece(lastMove.getToRow(), lastMove.getToCol(), lastMove.getCapturedPiece());

        movedPiece.setMoved(false);

        // Если это была рокировка, возвращаем и ладью
        if (lastMove.isCastling()) {
            int kingRow = lastMove.getFromRow();
            int kingFromCol = lastMove.getFromCol();
            int kingToCol = lastMove.getToCol();
            boolean isKingSide = kingToCol > kingFromCol;
            int rookFromCol = isKingSide ? 7 : 0;
            int rookToCol = isKingSide ? kingToCol - 1 : kingToCol + 1;

            Piece rook = getPiece(new Position(kingRow, rookToCol));
            setPiece(kingRow, rookFromCol, rook);
            setPiece(kingRow, rookToCol, null);

            if (rook != null) {
                rook.setMoved(false);
            }
        }
    }

    public List<Move> getMoveHistory() {
        return new ArrayList<>(moveHistory);
    }

    public List<Move> getValidMoves(Piece piece, int row, int col) {
        return moveValidator.getValidMoves(piece, row, col);
    }

    public boolean isCheck(PieceColor color) {
        return moveValidator.isCheck(color);
    }

    public boolean isCheckmate(PieceColor color) {
        return moveValidator.isCheckmate(color);
    }

    public boolean isStalemate(PieceColor color) {
        return moveValidator.isStalemate(color);
    }
}