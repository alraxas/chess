package org.example.oopchess.models.pieces;

import org.example.oopchess.enums.PieceColor;
import org.example.oopchess.models.board.Board;
import org.example.oopchess.models.board.Move;
import org.example.oopchess.models.board.Position;

import java.util.List;
//TODO: создать отдельные классы для фигур с логикой ходов
public abstract class Piece {
    protected PieceColor color;
    protected boolean hasMoved;

    public Piece(PieceColor color) {
        this.color = color;
        this.hasMoved = false;
    }

    public PieceColor getColor() { return color; }
    public boolean hasMoved() { return hasMoved; }
    public void setMoved(boolean hasMoved) { this.hasMoved = hasMoved; }

    public abstract List<Move> getPossibleMoves(Position current, Board board);
    public abstract boolean isValidMove(Position from, Position to, Board board);
    public abstract char getSymbol();
    public abstract String getPieceName();
}
