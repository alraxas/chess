package org.example.oopchess.rules;

import org.example.oopchess.enums.PieceColor;
import org.example.oopchess.models.board.Board;
import org.example.oopchess.models.board.Move;
import org.example.oopchess.models.board.Position;
import org.example.oopchess.models.pieces.King;
import org.example.oopchess.models.pieces.Pawn;
import org.example.oopchess.models.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class MoveValidator {
    private Board board;

    public MoveValidator(Board board) {
        this.board = board;
    }

    public boolean isValidMove(Move move) {
        Piece piece = move.getPiece();
        if (piece == null) return false;

        int fr = move.getFromRow();
        int fc = move.getFromCol();
        int tr = move.getToRow();
        int tc = move.getToCol();

        if (!board.isValidPosition(new Position(tr, tc))) return false;
        if (fr == tr && fc == tc) return false;

        Piece target = board.getPiece(new Position(tr, tc));
        if (target != null && target.getColor() == piece.getColor()) return false;

        Position from = new Position(fr, fc);
        Position to = new Position(tr, tc);
        if (!piece.isValidMove(from, to, board)) return false;

        return !wouldLeaveKingInCheck(move);
    }

    private boolean wouldLeaveKingInCheck(Move move) {
        Piece originalPiece = board.getPiece(new Position(move.getFromRow(), move.getFromCol()));
        Piece targetPiece = board.getPiece(new Position(move.getToRow(), move.getToCol()));

        board.setPiece(move.getToRow(), move.getToCol(), originalPiece);
        board.setPiece(move.getFromRow(), move.getFromCol(), null);

        boolean isInCheck = isCheck(move.getPiece().getColor());

        board.setPiece(move.getFromRow(), move.getFromCol(), originalPiece);
        board.setPiece(move.getToRow(), move.getToCol(), targetPiece);

        return isInCheck;
    }

    //TODO: убрать разнести по классам

    public boolean isSquareAttacked(int row, int col, PieceColor color) {
        PieceColor opponent = (color == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board.getPiece(new Position(r, c));
                if (piece != null && piece.getColor() == opponent) {
                    Position from = new Position(r, c);
                    Position to = new Position(row, col);
                    if (piece.isValidMove(from, to, board)) {
                        if (piece instanceof Pawn) {
                            int rowDiff = row - r;
                            int colDiff = Math.abs(col - c);
                            int direction = (piece.getColor() == PieceColor.WHITE) ? -1 : 1;

                            if (rowDiff == direction && colDiff == 1) {
                                return true;
                            }
                        } else {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public List<Move> getValidMoves (Piece piece, int row, int col){
        List<Move> moves = new ArrayList<>();

        List<Move> possiblePositions = piece.getPossibleMoves(new Position(row, col), board);

        for (Move move: possiblePositions) {
            if (move.getPiece() == null) {
                move.setPiece(piece);
            }

            if (isValidMove(move)) {
                moves.add(move);
            }
        }

        return moves;
    }

    public boolean isCheck(PieceColor color) {
        Position kingPosition = findKingPosition(color);
        if (kingPosition == null) return false;

        PieceColor opponentColor = (color == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(new Position(row, col));
                if (piece != null && piece.getColor() == opponentColor) {
                    if (canAttackSquare(piece, row, col, kingPosition.getRow(), kingPosition.getCol())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean canAttackSquare(Piece attacker, int fromRow, int fromCol, int toRow, int toCol) {
        Position from = new Position(fromRow, fromCol);
        Position to = new Position(toRow, toCol);

        if (attacker instanceof Pawn) {
            int direction = (attacker.getColor() == PieceColor.WHITE) ? -1 : 1;
            int rowDiff = toRow - fromRow;
            int colDiff = Math.abs(toCol - fromCol);

            return rowDiff == direction && colDiff == 1;
        }

        return attacker.isValidMove(from, to, board);
    }

    private Position findKingPosition(PieceColor color) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(new Position(row, col));
                if (piece != null && piece instanceof King && piece.getColor() == color) {
                    return new Position(row, col);
                }
            }
        }
        return null;
    }

    public boolean isCheckmate(PieceColor color) {
        if (!isCheck(color)) return false;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(new Position(row, col));
                if (piece != null && piece.getColor() == color) {
                    List<Move> moves = getValidMoves(piece, row, col);
                    if (!moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean isStalemate(PieceColor color) {
        if (isCheck(color)) return false;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(new Position(row, col));
                if (piece != null && piece.getColor() == color) {
                    List<Move> moves = getValidMoves(piece, row, col);
                    if (!moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public List<Move> getAllValidMoves(PieceColor color) {
        List<Move> moves = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(new Position(row, col));
                if (piece != null && piece.getColor() == color) {
                    moves.addAll(getValidMoves(piece, row, col));
                }
            }
        }
        return moves;
    }

    public boolean isPathClear(int fr, int fc, int tr, int tc) {
        int rs = Integer.compare(tr, fr);
        int cs = Integer.compare(tc, fc);
        int r = fr + rs, c = fc + cs;
        while (r != tr || c != tc) {
            if (board.getPiece(new Position(r, c)) != null) return false;
            r += rs;
            c += cs;
        }
        return true;
    }
}
