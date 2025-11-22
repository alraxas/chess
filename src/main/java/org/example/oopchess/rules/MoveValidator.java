package org.example.oopchess.rules;

import org.example.oopchess.enums.PieceColor;
import org.example.oopchess.models.board.Board;
import org.example.oopchess.models.board.Move;
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

        if (!board.isValidPosition(tr, tc)) return false;
        if (fr == tr && fc == tc) return false;

        Piece target = board.getPiece(tr, tc);
        if (target != null && target.getColor() == piece.getColor()) return false;

        return isValidMoveForPiece(piece, fr, fc, tr, tc);
    }

    private boolean isValidMoveForPiece(Piece piece, int fr, int fc, int tr, int tc) {
        return switch (piece.getType()) {
            case PAWN -> isValidPawnMove(piece, fr, fc, tr, tc);
            case ROOK -> isValidRookMove(fr, fc, tr, tc);
            case KNIGHT -> isValidKnightMove(fr, fc, tr, tc);
            case BISHOP -> isValidBishopMove(fr, fc, tr, tc);
            case QUEEN -> isValidQueenMove(fr, fc, tr, tc);
            case KING -> isValidKingMove(piece, fr, fc, tr, tc);
        };
    }

    private boolean isValidPawnMove(Piece pawn, int fr, int fc, int tr, int tc) {
        // Белые движутся вверх (row--), черные вниз (row++)
        int direction = pawn.getColor() == PieceColor.WHITE ? -1 : 1;
        int startRow = pawn.getColor() == PieceColor.WHITE ? 6 : 1;

        if (fc == tc) {
            if (tr == fr + direction && board.getPiece(tr, tc) == null) return true;
            if (fr == startRow && tr == fr + 2 * direction &&
                    board.getPiece(tr, tc) == null &&
                    board.getPiece(fr + direction, fc) == null)
                return true;
        }

        // Взятие по диагонали
        if (Math.abs(fc - tc) == 1 && tr == fr + direction) {
            Piece target = board.getPiece(tr, tc);
            if (target != null && target.getColor() != pawn.getColor()) return true;
            // en passant добавить
        }

        return false;
    }

    private boolean isValidRookMove(int fr, int fc, int tr, int tc) {
        if (fr != tr && fc != tc) return false;
        return isPathClear(fr, fc, tr, tc);
    }

    private boolean isValidKnightMove(int fr, int fc, int tr, int tc) {
        int dr = Math.abs(fr - tr), dc = Math.abs(fc - tc);
        return (dr == 2 && dc == 1) || (dr == 1 && dc == 2);
    }

    private boolean isValidBishopMove(int fr, int fc, int tr, int tc) {
        if (Math.abs(fr - tr) != Math.abs(fc - tc)) return false;
        return isPathClear(fr, fc, tr, tc);
    }

    private boolean isValidQueenMove(int fr, int fc, int tr, int tc) {
        return isValidRookMove(fr, fc, tr, tc) || isValidBishopMove(fr, fc, tr, tc);
    }

    private boolean isValidKingMove(Piece king, int fr, int fc, int tr, int tc) {
        int dr = Math.abs(fr - tr), dc = Math.abs(fc - tc);
        if (dr <= 1 && dc <= 1) return true;

        if (!king.hasMoved() && fr == tr && Math.abs(fc - tc) == 2) {
            // более строгую проверку (пустые клетки и отсутствие шахов) добавить отдельно
            return true;
        }

        return false;
    }

    private boolean isValidKingMov1(Piece king, int fr, int fc, int tr, int tc) {
        int dr = Math.abs(fr - tr), dc = Math.abs(fc - tc);
        if (dr <= 1 && dc <= 1) return true;

//        if (!king.hasMoved() && )
        return false;
    }

    private boolean isSquareAttacked(int row, int col, PieceColor color) {
        PieceColor opponent = (color == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPiece(r, c);
                if (p != null && p.getColor() == opponent) {

                    Move attack = new Move(r, c, row, col, p);
                    if (isValidMoveForPiece(p, r, c, row, col)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public List<Move> getValidMoves(Piece piece, int row, int col) {
        List<Move> moves = new ArrayList<>();
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                Move m = new Move(row, col, r, c, piece);
                if (isValidMove(m)) moves.add(m);
            }
        return moves;
    }

    private boolean isPathClear(int fr, int fc, int tr, int tc) {
        int rs = Integer.compare(tr, fr);
        int cs = Integer.compare(tc, fc);
        int r = fr + rs, c = fc + cs;
        while (r != tr || c != tc) {
            if (board.getPiece(r, c) != null) return false;
            r += rs;
            c += cs;
        }
        return true;
    }

    // расширить позже
    public boolean isCheck(PieceColor color) {
        return false;
    }

    public boolean isCheckmate(PieceColor color) {
        return isCheck(color) && getAllValidMoves(color).isEmpty();
    }

    public List<Move> getAllValidMoves(PieceColor color) {
        List<Move> moves = new ArrayList<>();
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPiece(r, c);
                if (p != null && p.getColor() == color) moves.addAll(getValidMoves(p, r, c));
            }
        return moves;
    }
}
