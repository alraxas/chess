package org.example.oopchess;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.example.oopchess.controller.GameController;
import org.example.oopchess.enums.PieceColor;
import org.example.oopchess.models.board.Position;
import org.example.oopchess.models.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class ChessController {
    @FXML
    private BorderPane rootPane;
    @FXML
    private Label statusLabel;
    @FXML
    private Label gameStateLabel;

    private GridPane chessBoard;
    private GameController gameController;
    private Position selectedPosition;
    private List<Position> legalMoves;

    @FXML
    public void initialize() {
        gameController = new GameController();
        selectedPosition = null;
        legalMoves = new ArrayList<>();
        setupBoard();
        updateDisplay();
    }

    private void setupBoard() {
        chessBoard = new GridPane();
        chessBoard.setPadding(new Insets(10));

        rootPane.setCenter(chessBoard);
    }

    private void onCellClick(int row, int col) {
        Position clicked = new Position(row, col);
        Piece piece = gameController.getBoard().getPiece(row, col);

        if (selectedPosition == null) {
            if (piece != null && piece.getColor() == gameController.getCurrentPlayer().getColor()) {
                selectedPosition = clicked;
                legalMoves = gameController.getValidMovesForPiece(row, col);
            }
        } else {
            boolean isLegal = legalMoves.stream().anyMatch(p -> p.equals(clicked));
            if (isLegal) {
                gameController.makeMove(
                        selectedPosition.getRow(), selectedPosition.getCol(),
                        row, col
                );
            }
            selectedPosition = null;
            legalMoves.clear();
        }
        updateDisplay();
    }

    private void updateDisplay() {
        chessBoard.getChildren().clear();

        for (int row = 7; row > -1; row--) {
            for (int col = 0; col < 8; col++) {
                StackPane cell = buildCell(row, col);
                chessBoard.add(cell, col, row);
            }
        }

        updateStatus();
    }

    private StackPane buildCell(int row, int col) {
        StackPane cell = new StackPane();
        cell.setPrefSize(70, 70);

        Rectangle bg = new Rectangle(70, 70);
        bg.setFill(((row + col) % 2 == 0) ? Color.TAN : Color.SADDLEBROWN);
        cell.getChildren().add(bg);


        // Selected cell highlight
        if (selectedPosition != null && selectedPosition.equals(new Position(row, col))) {
            Rectangle sel = new Rectangle(70, 70);
            sel.setFill(Color.CORNFLOWERBLUE);
            sel.setOpacity(0.4);
            cell.getChildren().add(sel);
        }

        // Move targets
        for (Position p : legalMoves) {
            if (p.getRow() == row && p.getCol() == col) {
                Circle dot = new Circle(10, Color.LIGHTGREEN);
                dot.setOpacity(0.8);
                cell.getChildren().add(dot);
            }
        }

        // Piece
        Piece piece = gameController.getBoard().getPiece(row, col);
        if (piece != null) {
            Label lbl = new Label(pieceSymbol(piece));
            lbl.setStyle("-fx-font-size: 34; -fx-font-weight: bold;");
            lbl.setTextFill(piece.getColor() == PieceColor.WHITE ? Color.WHITE : Color.BLACK);
            cell.getChildren().add(lbl);
        }

        cell.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY)
                onCellClick(row, col);
        });

        return cell;
    }

    private String pieceSymbol(Piece piece) {
        return switch (piece.getType()) {
            case KING -> piece.getColor() == PieceColor.WHITE ? "♔" : "♚";
            case QUEEN -> piece.getColor() == PieceColor.WHITE ? "♕" : "♛";
            case ROOK -> piece.getColor() == PieceColor.WHITE ? "♖" : "♜";
            case BISHOP -> piece.getColor() == PieceColor.WHITE ? "♗" : "♝";
            case KNIGHT -> piece.getColor() == PieceColor.WHITE ? "♘" : "♞";
            case PAWN -> piece.getColor() == PieceColor.WHITE ? "♙" : "♟";
        };
    }

    private void updateStatus() {
        String current = (gameController.getCurrentPlayer().getColor() == PieceColor.WHITE)
                ? "Ход: Белые" : "Ход: Черные";
        statusLabel.setText(current);
        gameStateLabel.setText("Статус: " + gameController.getGameState().toString());
    }

    @FXML
    private void newGame() {
        gameController = new GameController();
        selectedPosition = null;
        legalMoves.clear();
        updateDisplay();
    }
}
