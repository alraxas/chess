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
import org.example.oopchess.enums.GameState;
import org.example.oopchess.models.board.Move;
import org.example.oopchess.models.pieces.Piece;
import org.example.oopchess.rules.GameController;
import org.example.oopchess.enums.PieceColor;
import org.example.oopchess.models.board.Position;

import java.util.ArrayList;
import java.util.List;

public class ChessAppController {
    @FXML
    private BorderPane rootPane;
    @FXML
    private Label statusLabel;
    @FXML
    private Label whitePlayerLabel;
    @FXML
    private Label blackPlayerLabel;
    @FXML
    private Label whiteStatusLabel;
    @FXML
    private Label blackStatusLabel;

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
        updatePlayerInfo();
    }

    @FXML
    private void newGame() {
        gameController = new GameController();
        selectedPosition = null;
        legalMoves.clear();
        updateDisplay();
        updatePlayerInfo();
    }

    @FXML
    private void resign() {
        gameController.resign();
        updateDisplay();
        updatePlayerInfo();
    }

    @FXML
    private void undoMove() {
        gameController.undoLastMove();
        updateDisplay();
        updatePlayerInfo();
    }

    private void updatePlayerInfo() {
        whitePlayerLabel.setText("Белые: " + gameController.getPlayer(PieceColor.WHITE).getName());
        blackPlayerLabel.setText("Черные: " + gameController.getPlayer(PieceColor.BLACK).getName());

        if (whiteStatusLabel != null && blackStatusLabel != null) {
            whiteStatusLabel.setText(gameController.getPlayerStatus(PieceColor.WHITE).toString());
            blackStatusLabel.setText(gameController.getPlayerStatus(PieceColor.BLACK).toString());

            setStatusLabelColor(whiteStatusLabel, gameController.getPlayerStatus(PieceColor.WHITE));
            setStatusLabelColor(blackStatusLabel, gameController.getPlayerStatus(PieceColor.BLACK));
        }
    }

    private void setStatusLabelColor(Label label, GameState status) {
        switch (status) {
            case PLAYING:
                label.setStyle("-fx-text-fill: #4CAF50;"); // Зеленый
                break;
            case CHECK:
                label.setStyle("-fx-text-fill: #FF9800;"); // Оранжевый
                break;
            case CHECKMATE:
                label.setStyle("-fx-text-fill: #F44336;"); // Красный
                break;
            case STALEMATE:
                label.setStyle("-fx-text-fill: #9E9E9E;"); // Серый
                break;
            case RESIGNED:
                label.setStyle("-fx-text-fill: #795548;"); // Коричневый
                break;
            default:
                label.setStyle("-fx-text-fill: #333333;"); // Черный
        }
    }

    private void setupBoard() {
        chessBoard = new GridPane();
        chessBoard.setPadding(new Insets(10));
        rootPane.setCenter(chessBoard);
    }

    private void onCellClick(int row, int col) {
        Position clicked = new Position(row, col);
        Piece piece = gameController.getBoard().getPiece(new Position(row, col));

        if (clicked.equals(selectedPosition)) {
            selectedPosition = null;
            legalMoves.clear();
        } else if (piece != null && piece.getColor() == gameController.getCurrentPlayerColor()) {
            selectedPosition = clicked;
            legalMoves = gameController.getValidMovesForPiece(row, col);
        } else if (selectedPosition != null && legalMoves.stream().anyMatch(p -> p.equals(clicked))) {
            Move move = gameController.makeMove(selectedPosition.getRow(), selectedPosition.getCol(), row, col);
            selectedPosition = null;
            legalMoves.clear();
        } else {
            selectedPosition = null;
            legalMoves.clear();
        }

        updateDisplay();
        updatePlayerInfo();
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

        if (selectedPosition != null && selectedPosition.equals(new Position(row, col))) {
            Rectangle sel = new Rectangle(70, 70);
            sel.setFill(Color.CORNFLOWERBLUE);
            sel.setOpacity(0.4);
            cell.getChildren().add(sel);
        }

        for (Position p : legalMoves) {
            if (p.getRow() == row && p.getCol() == col) {
                Circle dot = new Circle(10, Color.LIGHTGREEN);
                dot.setOpacity(0.8);
                cell.getChildren().add(dot);
            }
        }

        Piece piece = gameController.getBoard().getPiece(new Position(row, col));
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
        return String.valueOf(piece.getSymbol());
    }

    private void updateStatus() {
        String current = (gameController.getCurrentPlayerColor() == PieceColor.WHITE)
                ? "Ход: Белые" : "Ход: Черные";
        statusLabel.setText(current);
    }
}