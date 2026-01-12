package org.example.oopchess.models.pieces;

import org.example.oopchess.enums.PieceColor;
import org.example.oopchess.models.board.Board;
import org.example.oopchess.models.board.Move;
import org.example.oopchess.models.board.Position;
import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {
    public Knight(PieceColor color) {
        super(color);
    }

    @Override
    public List<Move> getPossibleMoves(Position current, Board board) {
        List<Move> moves = new ArrayList<>();
        int[][] knightMoves = {
                {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
                {1, -2}, {1, 2}, {2, -1}, {2, 1}
        };

        for (int[] move : knightMoves) {
            Position newPos = new Position(current.getRow() + move[0], current.getCol() + move[1]);
            if (board.isValidPosition(newPos)) {
                Piece target = board.getPiece(newPos);
                if (target == null || target.getColor() != color) {
                    moves.add(new Move(current.getRow(), current.getCol(), newPos.getRow(), newPos.getCol()));
                }
            }
        }

        return moves;
    }

    @Override
    public boolean isValidMove(Position from, Position to, Board board) {
        int rowDiff = Math.abs(from.getRow() - to.getRow());
        int colDiff = Math.abs(from.getCol() - to.getCol());

        if ((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)) {
            Piece target = board.getPiece(to);
            return target == null || target.getColor() != color;
        }

        return false;
    }

    @Override
    public char getSymbol() {
        return (color == PieceColor.WHITE) ? '♘' : '♞';
    }

    @Override
    public String getPieceName() {
        return "Knight";
    }
}