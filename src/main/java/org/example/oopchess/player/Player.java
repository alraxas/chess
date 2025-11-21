package org.example.oopchess.player;

import org.example.oopchess.enums.PieceColor;
import org.example.oopchess.models.Piece;

public class Player {
    private PieceColor color;
    private String name;
    private int rating;

    public Player(PieceColor color, String name) {
        this.color = color;
        this.name = name;
        this.rating = 1200;
    }

    public  Player(PieceColor color) {
        this(color, "");
    }

    public PieceColor getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
