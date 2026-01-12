package org.example.oopchess.models.pieces;

import org.example.oopchess.enums.PieceColor;
import org.example.oopchess.models.board.Board;
import org.example.oopchess.models.board.Move;
import org.example.oopchess.models.board.Position;
import org.example.oopchess.rules.MoveValidator;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    public King(PieceColor color) {
        super(color);
    }

    @Override
    public List<Move> getPossibleMoves(Position current, Board board) {
        List<Move> moves = new ArrayList<>();

        for (int row = -1; row <= 1; row++) {
            for (int col = -1; col <= 1; col++) {
                if (row == 0 && col == 0) continue;

                Position newPos = new Position(current.getRow() + row, current.getCol() + col);
                if (board.isValidPosition(newPos)) {
                    Piece target = board.getPiece(newPos);
                    if (target == null || target.getColor() != color) {
                        moves.add(new Move(current.getRow(), current.getCol(), newPos.getRow(), newPos.getCol()));
                    }
                }
            }
        }

        if (!hasMoved) {
            if (canCastle(current, board, true)) { // короткая рокировка
                moves.add(new Move(current.getRow(), current.getCol(), current.getRow(), current.getCol() + 2));
            }
            if (canCastle(current, board, false)) { // длинная рокировка
                moves.add(new Move(current.getRow(), current.getCol(), current.getRow(), current.getCol() - 2));
            }
        }

        return moves;
    }

    @Override
    public boolean isValidMove(Position from, Position to, Board board) {
        int rowDiff = Math.abs(from.getRow() - to.getRow());
        int colDiff = Math.abs(from.getCol() - to.getCol());

        if (rowDiff <= 1 && colDiff <= 1) {
            Piece target = board.getPiece(to);
            return target == null || target.getColor() != color;
        }

        if (!hasMoved && rowDiff == 0 && colDiff == 2) { // рокировка
            boolean isKingSide = to.getCol() > from.getCol();
            return canCastle(from, board, isKingSide);
        }

        return false;
    }

    private boolean canCastle(Position kingPos, Board board, boolean isKingSide) {
        int rookCol = isKingSide ? 7 : 0;
        int step = isKingSide ? 1 : -1;

        Piece rook = board.getPiece(new Position(kingPos.getRow(), rookCol));
        if (rook == null || !(rook instanceof Rook) || rook.hasMoved()) {
            return false;
        }

        for (int col = kingPos.getCol() + step; col != rookCol; col += step) {
            if (board.getPiece(new Position(kingPos.getRow(), col)) != null) {
                return false;
            }
        }

        MoveValidator mv = new MoveValidator(board);
        for (int col = kingPos.getCol(); col != kingPos.getCol() + 2 * step + step; col += step) {
            Position checkPos = new Position(kingPos.getRow(), col);
            if (mv.isSquareAttacked(checkPos.getRow(), checkPos.getCol(), color)) { // король не под шахом
                return false;
            }
        }

        return true;
    }

    @Override
    public char getSymbol() {
        return (color == PieceColor.WHITE) ? '♔' : '♚';
    }

    @Override
    public String getPieceName() {
        return "King";
    }
}