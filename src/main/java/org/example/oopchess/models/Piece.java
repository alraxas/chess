package org.example.oopchess.models;

import org.example.oopchess.enums.PieceColor;
import org.example.oopchess.enums.PieceType;

public class Piece {
    private PieceType type;
    private PieceColor color;
    private boolean hasMoved;

    public Piece(PieceType type, PieceColor color, boolean hasMoved) {
        this.type = type;
        this.color = color;
        this.hasMoved = hasMoved;
    }

    public Piece(PieceType type, PieceColor color) {
        this(type, color, false);
    }

    public void setMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public PieceType getType() {
        return type;
    }

    public PieceColor getColor() {
        return color;
    }

    public boolean hasMoved() {
        return hasMoved;
    }
}
