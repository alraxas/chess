package org.example.oopchess.models.pieces;

import org.example.oopchess.enums.PieceColor;
import org.example.oopchess.models.board.Board;
import org.example.oopchess.models.board.Move;
import org.example.oopchess.models.board.Position;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {

    public Pawn(PieceColor color) {
        super(color);
    }

    @Override
    public List<Move> getPossibleMoves(Position current, Board board) {
        List<Move> moves = new ArrayList<>();
        int direction = color == PieceColor.WHITE ? -1 : 1;
        int startRow = color == PieceColor.WHITE ? 6 : 1;

        Position forward = new Position(current.getRow() + direction, current.getCol()); // Ход вперед на 1 клетку
        if (board.isValidPosition(forward) && board.getPiece(forward) == null) {
            moves.add(new Move(current.getRow(), current.getCol(), forward.getRow(), forward.getCol()));
            if (current.getRow() == startRow) { // Ход вперед на 2 клетки из начальной позиции
                Position doubleForward = new Position(current.getRow() + 2 * direction, current.getCol());
                if (board.isValidPosition(doubleForward) && board.getPiece(doubleForward) == null) {
                    moves.add(new Move(current.getRow(), current.getCol(), doubleForward.getRow(), doubleForward.getCol()));
                }
            }
        }

        int[] captureCols = {-1, 1}; // Взятие по диагонали
        for (int colOffset : captureCols) {
            Position capturePos = new Position(current.getRow() + direction, current.getCol() + colOffset);
            if (board.isValidPosition(capturePos)) {
                Piece target = board.getPiece(capturePos);
                if (target != null && target.getColor() != color) {
                    moves.add(new Move(current.getRow(), current.getCol(), capturePos.getRow(), capturePos.getCol()));
                }
            }
        }

        addEnPassantMoves(current, board, moves, direction);

        return moves;
    }

    private void addEnPassantMoves(Position current, Board board, List<Move> moves, int direction) {
        Position enPassantTarget = board.getEnPassantTarget(); // наличие цели для взятия на проходе
        if (enPassantTarget == null) return;

        int enPassantRow = (color == PieceColor.WHITE) ? 3 : 4;
        if (current.getRow() != enPassantRow) return;

        int[] sideCols = {-1, 1};

        for (int colOffset : sideCols) {
            int targetCol = current.getCol() + colOffset;
            if (enPassantTarget.getCol() == targetCol) {
                Position enPassantPos = new Position(current.getRow() + direction, targetCol);
                if (board.isValidPosition(enPassantPos) && board.getPiece(enPassantPos) == null) {
                    Move enPassantMove = new Move(current.getRow(), current.getCol(),
                            enPassantPos.getRow(), enPassantPos.getCol(), this);
                    enPassantMove.setEnPassant(true);
                    moves.add(enPassantMove);
                }
            }
        }
    }

    @Override
    public boolean isValidMove(Position from, Position to, Board board) {
        int direction = color == PieceColor.WHITE ? -1 : 1;
        int startRow = color == PieceColor.WHITE ? 6 : 1;
        int fc = from.getCol();
        int fr = from.getRow();
        int tc = to.getCol();
        int tr = to.getRow();

        if (fc == tc) {
            if (tr == fr + direction && board.getPiece(new Position(tr, tc)) == null) return true;
            if (fr == startRow && tr == fr + 2 * direction &&
                    board.getPiece(new Position(tr, tc)) == null &&
                    board.getPiece(new Position(fr + direction, fc)) == null)
                return true;
        }

        // Взятие по диагонали
        if (Math.abs(fc - tc) == 1 && tr == fr + direction) {
            Piece target = board.getPiece((new Position(tr, tc)));
            if (target != null && target.getColor() != color) return true;
        }
        return isValidEnPassant(from, to, board);
    }

    private boolean isValidEnPassant(Position from, Position to, Board board) {
        int direction = color == PieceColor.WHITE ? -1 : 1;
        int fr = from.getRow();
        int fc = from.getCol();
        int tr = to.getRow();
        int tc = to.getCol();

        if (Math.abs(fc - tc) != 1 || tr != fr + direction) {
            return false;
        }

        if (board.getPiece(to) != null) {
            return false;
        }

        Position sidePosition = new Position(fr, tc);
        Piece sidePiece = board.getPiece(sidePosition);

        return sidePiece instanceof Pawn && sidePiece.getColor() != color;
    }

    @Override
    public char getSymbol() {
        return (color == PieceColor.WHITE) ? '♙' : '♟';
    }

    @Override
    public String getPieceName() {
        return "Pawn";
    }
}
