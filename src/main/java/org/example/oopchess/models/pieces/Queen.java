package org.example.oopchess.models.pieces;

import org.example.oopchess.enums.PieceColor;
import org.example.oopchess.models.board.Board;
import org.example.oopchess.models.board.Move;
import org.example.oopchess.models.board.Position;
import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {
    public Queen(PieceColor color) {
        super(color);
    }

    @Override
    public List<Move> getPossibleMoves(Position current, Board board) {
        List<Move> moves = new ArrayList<>();
        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1}, {0, 1},
                {1, -1}, {1, 0}, {1, 1}
        };

        for (int[] dir : directions) {
            for (int i = 1; i < 8; i++) {
                Position newPos = new Position(current.getRow() + dir[0] * i,
                        current.getCol() + dir[1] * i);
                if (!board.isValidPosition(newPos)) break;

                Piece target = board.getPiece(newPos);
                if (target == null) {
                    moves.add(new Move(current.getRow(), current.getCol(), newPos.getRow(), newPos.getCol()));
                } else {
                    if (target.getColor() != color) {
                        moves.add(new Move(current.getRow(), current.getCol(), newPos.getRow(), newPos.getCol()));
                    }
                    break;
                }
            }
        }

        return moves;
    }

    @Override
    public boolean isValidMove(Position from, Position to, Board board) {
        int rowDiff = Math.abs(from.getRow() - to.getRow());
        int colDiff = Math.abs(from.getCol() - to.getCol());

        // Должна двигаться по прямой или диагонали
        if (rowDiff != 0 && colDiff != 0 && rowDiff != colDiff) {
            return false;
        }

        int rowStep = Integer.compare(to.getRow(), from.getRow());
        int colStep = Integer.compare(to.getCol(), from.getCol());

        // Проверяем что путь свободен
        int currentRow = from.getRow() + rowStep;
        int currentCol = from.getCol() + colStep;

        while (currentRow != to.getRow() || currentCol != to.getCol()) {
            if (board.getPiece(new Position(currentRow, currentCol)) != null) {
                return false;
            }
            currentRow += rowStep;
            currentCol += colStep;
        }

        Piece target = board.getPiece(to);
        return target == null || target.getColor() != color;
    }

    @Override
    public char getSymbol() {
        return (color == PieceColor.WHITE) ? '♕' : '♛';
    }

    @Override
    public String getPieceName() {
        return "Queen";
    }
}