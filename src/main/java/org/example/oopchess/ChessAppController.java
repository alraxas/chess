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
    private Label gameStateLabel;

    private GridPane chessBoard;
    private GameController gameController;
    private Position selectedPosition;
    private List<Position> legalMoves;
    //TODO: убрать пересоздание листа легал мувс и вынести в другое место не обновлять каждый раз

    @FXML
    public void initialize() {
        gameController = new GameController();
        selectedPosition = null;
        legalMoves = new ArrayList<>();
        setupBoard();
        updateDisplay();
    }

    @FXML
    private void newGame() {
        gameController = new GameController();
        selectedPosition = null;
        legalMoves.clear();
        updateDisplay();
    }

    private void setupBoard() {
        chessBoard = new GridPane();
        chessBoard.setPadding(new Insets(10));

        rootPane.setCenter(chessBoard);
    }

    private void onCellClick(int row, int col) {
        Position clicked = new Position(row, col);
        Piece piece = gameController.getBoard().getPiece(new Position(row, col));

        if (clicked.equals(selectedPosition)) { //отмена выбора повторное нажатие на выбранную фигуру
            selectedPosition = null;
            legalMoves.clear();
        } else if (piece != null && piece.getColor() == gameController.getCurrentPlayer().getColor()) { //выбор фигуры
            selectedPosition = clicked;
            legalMoves = gameController.getValidMovesForPiece(row, col);
        } else if (selectedPosition != null && legalMoves.stream().anyMatch(p -> p.equals(clicked))) { //ход
            Move move =  gameController.makeMove(selectedPosition.getRow(), selectedPosition.getCol(), row, col);
            if (move != null && move.isEnPassant()) {
                System.out.println("En passant move performed!");
            }
            selectedPosition = null;
            legalMoves.clear();
        } else {
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
        String current = (gameController.getCurrentPlayer().getColor() == PieceColor.WHITE)
                ? "Ход: Белые" : "Ход: Черные";
        statusLabel.setText(current);
        gameStateLabel.setText("Статус: " + gameController.getGameState().toString());
    }
}
