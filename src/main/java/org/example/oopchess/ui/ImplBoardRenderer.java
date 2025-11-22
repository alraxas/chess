package org.example.oopchess.ui;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.example.oopchess.models.board.Board;
import org.example.oopchess.models.board.Position;
import org.example.oopchess.models.pieces.Piece;

import java.awt.*;

public class ImplBoardRenderer implements BoardRenderer {
    private final GridPane gridPane;

    public ImplBoardRenderer(GridPane gridPane) {
        this.gridPane = gridPane;
    }

    @Override
    public void render(Board board) {
        gridPane.getChildren().clear();

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Position pos = new Position(r, c);
                StackPane cell = createCell(pos, board.getPiece(r, c));
                GridPane.setRowIndex(cell, r);
                GridPane.setColumnIndex(cell, c);
                gridPane.add(cell, c, r);
            }
        }
    }

    private StackPane createCell(Position position, Piece piece) {
        StackPane cell = new StackPane();
        return cell;
    }
}
