package NQueens;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class NQueens {

    public static int n;
    public static int[] board;
    // number of queens on the ith row
    public static int[] rowQueensCnt;
    // number of queens on the ith diagonal from top left to bottom right (Right-Leaning)
    public static int[] diagonalRLCnt;
    // number of queens on the ith diagonal from top right to bottom left (Left-Leaning)
    public static int[] diagonalLLCnt;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        n = sc.nextInt();
        if(n == 2 || n == 3) {
            System.out.println(-1);
            return;
        }

        // one queen per column
        board = new int[n];
        rowQueensCnt = new int[n];
        diagonalRLCnt = new int[2 * n - 1];
        diagonalLLCnt = new int[2 * n - 1];

        int minConflictsIterations = Math.min(n * 200, 200000);

        AtomicBoolean solved = new AtomicBoolean(false);
        minConflicts(minConflictsIterations, solved);
        while (!solved.get()) {
            minConflicts(minConflictsIterations, solved);
        }

        System.out.println(Arrays.toString(board));
    }

    public static void initBoardRandom() {
        Arrays.fill(rowQueensCnt, 0);
        Arrays.fill(diagonalRLCnt, 0);
        Arrays.fill(diagonalLLCnt, 0);
        Random rnd = new Random();
        rnd.setSeed(LocalDateTime.now().getNano());
        for(int i = 0; i < n; i++) {
            board[i] = rnd.nextInt(n);
            rowQueensCnt[board[i]]++;
            diagonalRLCnt[i + board[i]]++;
            diagonalLLCnt[n - i - 1 + board[i]]++;
        }
    }

    public static void minConflicts(int maxMoves, AtomicBoolean solved) {
        int mvCnt = 0;
        initBoardRandom();
        while (mvCnt < maxMoves && !solved.get()) {
            int q = calculateMostConflictingQueen();
            if(q == -1) {
                solved.set(true);
                break;
            }
            minimizeConflicts(q);
            mvCnt++;
        }
    }

    private static void minimizeConflicts(int q) {
        rowQueensCnt[board[q]]--;
        diagonalRLCnt[q + board[q]]--;
        diagonalLLCnt[n - q - 1 + board[q]]--;

        int minInd = 0;
        int currMin = Integer.MAX_VALUE;
        int tieCount = 0;
        // random selection of minimum - VERY IMPORTANT
        Random rnd = new Random();

        for (int i = 0; i < n; i++) {
            int currConfl = rowQueensCnt[i] +
                    diagonalRLCnt[q + i] +
                    diagonalLLCnt[n - q - 1 + i];
            if (currConfl < currMin) {
                currMin = currConfl;
                minInd = i;
                tieCount = 1;
            } else if (currConfl == currMin) {
                tieCount++;
                if (rnd.nextInt(tieCount) == 0) {
                    minInd = i;
                }
            }
            if (currMin == 0) break;
        }

        board[q] = minInd;
        rowQueensCnt[board[q]]++;
        diagonalRLCnt[q + board[q]]++;
        diagonalLLCnt[n - q - 1 + board[q]]++;
    }

    private static int calculateMostConflictingQueen() {
        int maxConfInd = -1;
        int currMax = -1;
        for(int i = 0; i < n; i++) {
            int currConfl = rowQueensCnt[board[i]] +
                    diagonalRLCnt[i + board[i]] +
                    diagonalLLCnt[n - i - 1 + board[i]] - 3;
            if(currConfl > currMax && currConfl > 0) {
                maxConfInd = i;
                currMax = currConfl;
            }
        }

        return maxConfInd;
    }

}