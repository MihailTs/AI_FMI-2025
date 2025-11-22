package TicTacToe;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Board {

    private String rowSeparator = "";
    private final String EMPTY;
    private final String X_SYMBOL;
    private final String O_SYMBOL;
    // -1 - X_SYMBOL, 1 - O_SYMBOL
    private int humanNum;
    // -1 - X_SYMBOL, 1 - O_SYMBOL, 0 - EMPTY
    private final int[][] board;
    private final int size = 3;

    public Board(String empty, String x, String o) {
        EMPTY = empty;
        X_SYMBOL = x;
        O_SYMBOL = o;
        board = new int[size][size];
        rowSeparator += "+";
        for(int i = 0; i < size; i++) {
            rowSeparator += "---+";
        }
    }

    public void set(int row, int col, String s) {
        if(s.equals(EMPTY)) board[row - 1][col - 1] = 0;
        if(s.equals(X_SYMBOL)) board[row - 1][col - 1] = -1;
        if(s.equals(O_SYMBOL)) board[row - 1][col - 1] = 1;
    }

    public void setHumanNum(int num) {
        if(num != -1 && num != 1) throw new IllegalArgumentException("Illegal turn player number passed!");
        this.humanNum = num;
    }

    public void move(int row, int col) {
        board[row - 1][col - 1] = humanNum;
    }

    /**
     * returns 0 if game is not finished,
     *         -1 if X player wins,
     *         1 if O player wins,
     *         2 if it's a draw
     */
    public int endgame() {
        int first;
        for(int i = 0; i < size; i++) {
            first = board[i][0];
            if(first != 0)
                for(int j = 1;; j++) {
                    if(board[i][j] == first && j == size - 1) return first;
                    else if(board[i][j] != first) break;
                }
            first = board[0][i];
            if(first != 0)
                for(int j = 1;; j++) {
                    if(board[j][i] == first && j == size - 1) return first;
                    else if(board[j][i] != first) break;
                }
        }

        first = board[0][0];
        if(first != 0)
            for(int i = 1; ; i++)
                if(board[i][i] == first && i == size - 1) return first;
                else if(board[i][i] != first) break;

        first = board[0][size - 1];
        if(first != 0) {
            for(int i = 1; ; i++) {
                if(board[i][size - 1 - i] == first && i == size - 1) return first;
                else if(board[i][size - 1 - i] != first) break;
            }
        }
        // check for empty square
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                if(board[i][j] == 0) return 0;
            }
        }

        return 2;
    }

    /** plays the next best move for the computer with the opposite of humanNum
     */
    public int[] nextMove() {
        int botNum = -humanNum;
        AtomicInteger bestMove = new AtomicInteger(-1);
        Pair<Integer> m = Max(Integer.MIN_VALUE, Integer.MAX_VALUE, 0, bestMove);
        board[bestMove.get() / size][bestMove.get() % size] = botNum;
        return new int[]{bestMove.get() / size + 1, bestMove.get() % size + 1};
    }

    // supposing the game is not finished when called
    // goal - we are searching for a branch with higher value than goal
    private Pair<Integer> Max(int alpha, int beta, int depth, AtomicInteger bestMove) {
        int endgame = endgame();
        if(endgame == -1) return (humanNum == -1)? new Pair<Integer>(-1, depth): new Pair<Integer>(1, depth);
        if(endgame == 1) return (humanNum == -1)? new Pair<Integer>(1, depth): new Pair<Integer>(-1, depth);
        if(endgame == 2) return new Pair<Integer>(0, depth);

        Pair<Integer> best = new Pair<Integer>(Integer.MIN_VALUE, Integer.MAX_VALUE);
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                if(board[i][j] == 0) {
                    board[i][j] = -humanNum;
                    Pair<Integer> m = Min(alpha, beta, depth + 1);
                    // if it's a better move - keep it
                    if(m.getFirst() > best.getFirst()) {
                        if (depth == 0) bestMove.set(i * size + j);
                        best.setFirst(m.getFirst());
                        best.setSecond(m.getSecond());
                        alpha = Math.max(alpha, best.getFirst());
                    } else if (m.getFirst().equals(best.getFirst()) && m.getFirst() > 0 && m.getSecond() < best.getSecond()) {
                        best.setSecond(m.getSecond());
                        if (depth == 0) bestMove.set(i * size + j);
                    } else if (m.getFirst().equals(best.getFirst()) && m.getFirst() == 0 && m.getSecond() > best.getSecond()) {
                        best.setSecond(m.getSecond());
                    } else if(m.getFirst().equals(best.getFirst()) && m.getFirst() < 0 && m.getSecond() > best.getSecond()) {
                        best.setSecond(m.getSecond());
                        if (depth == 0) bestMove.set(i * size + j);
                    }
                    board[i][j] = 0;
                    if(beta <= alpha) return best;
                }
            }
        }
        return best;
    }

    private Pair<Integer> Min(int alpha, int beta, int depth) {
        int endgame = endgame();
        if(endgame == -1) return (humanNum == -1)? new Pair<Integer>(-1, depth): new Pair<Integer>(1, depth);
        if(endgame == 1) return (humanNum == -1)? new Pair<Integer>(1, depth): new Pair<Integer>(-1, depth);
        if(endgame == 2) return new Pair<Integer>(0, depth);

        Pair<Integer> best = new Pair<Integer>(Integer.MAX_VALUE, Integer.MAX_VALUE);
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                if(board[i][j] == 0) {
                    board[i][j] = humanNum;
                    Pair<Integer> m = Max(alpha, beta, depth + 1, null);
                    if(m.getFirst() < best.getFirst()) {
                        best.setFirst(m.getFirst());
                        best.setSecond(m.getSecond());
                        beta = Math.min(best.getFirst(), beta);
                    } else if (m.getFirst().equals(best.getFirst()) && m.getFirst() == 0 && m.getSecond() > best.getSecond()) {
                        best.setSecond(m.getSecond());
                    } else if(m.getFirst().equals(best.getFirst()) && m.getFirst() < 0 && m.getSecond() > best.getSecond()) {
                        best.setSecond(m.getSecond());
                    }
                    board[i][j] = 0;
                    if(beta <= alpha) return best;
                }
            }
        }
        return best;
    }

    private String getSymbol(int s) {
        if(s == 0) return EMPTY;
        if(s == 1) return O_SYMBOL;
        if(s == -1) return X_SYMBOL;
        return "";
    }

    public void prettyPrint() {
        for(int i = 0; i < size; i++) {
            System.out.println(rowSeparator);
            StringBuilder s = new StringBuilder("|");
            for(int j = 0; j < size; j++) {
                s.append(" ").append(getSymbol(board[i][j])).append(" |");
            }
            System.out.println(s);
        }
        System.out.println(rowSeparator);
    }

    public boolean isTaken(int row, int col) {
        return board[row - 1][col - 1] == 0;
    }
}
