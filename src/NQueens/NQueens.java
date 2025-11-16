package NQueens;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class NQueens {

    public static int n;
    public static int[] board;
    public static int[] conflicts;
    // number of queens on the ith row
    public static HashSet<Integer>[] rowQueensCnt;
    // number of queens on the ith diagonal from top left to bottom right (Right-Leaning)
    public static HashSet<Integer>[] diagonalRLCnt;
    // number of queens on the ith diagonal from top right to bottom left (Left-Leaning)
    public static HashSet<Integer>[] diagonalLLCnt;

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        n = sc.nextInt();
        if(n == 2 || n == 3) {
            System.out.println(-1);
            return;
        }

        // one queen per column
        board = new int[n];
        conflicts = new int[n];
        rowQueensCnt = new HashSet[n];
        diagonalRLCnt = new HashSet[2 * n - 1];
        diagonalLLCnt = new HashSet[2 * n - 1];

        for (int i = 0; i < n; i++) {
            rowQueensCnt[i] = new HashSet<>();
        }
        for (int i = 0; i < 2 * n - 1; i++) {
            diagonalRLCnt[i] = new HashSet<>();
            diagonalLLCnt[i] = new HashSet<>();
        }

        int minConflictsIterations = 5 * n;

        AtomicBoolean solved = new AtomicBoolean(false);
        minConflicts(minConflictsIterations, solved);
        while (!solved.get()) {
            minConflicts(minConflictsIterations, solved);
        }

        System.out.println(Arrays.toString(board));
    }

    public static void initBoardRandom() {
        for (int i = 0; i < n; i++) rowQueensCnt[i].clear();
        for (int i = 0; i < 2 * n - 1; i++) {
            diagonalRLCnt[i].clear();
            diagonalLLCnt[i].clear();
        }
        Arrays.fill(conflicts, 0);

        Random rnd = new Random();
        rnd.setSeed(LocalDateTime.now().getNano());
        for(int i = 0; i < n; i++) {
            board[i] = rnd.nextInt(n);
            rowQueensCnt[board[i]].add(i);
            diagonalRLCnt[i + board[i]].add(i);
            diagonalLLCnt[n - i - 1 + board[i]].add(i);
        }
    }

    public static void initializeBoardKnightsTour() {
        Random rand = new Random();

        // Clear all tracking structures
        for (int i = 0; i < n; i++) {
            rowQueensCnt[i].clear();
        }
        for (int i = 0; i < 2 * n - 1; i++) {
            diagonalRLCnt[i].clear();
            diagonalLLCnt[i].clear();
        }

        // Knight move offsets (we only care about column changes)
        // These represent: 2 cols forward/back with 1 row up/down
        int[] colOffsets = {-2, -2, -1, -1, 1, 1, 2, 2};
        int[] rowOffsets = {-1, 1, -2, 2, -2, 2, -1, 1};

        boolean[] visited = new boolean[n];
        List<Integer> columnOrder = new ArrayList<>();

        // Start from a random column
        int currentCol = rand.nextInt(n);
        columnOrder.add(currentCol);
        visited[currentCol] = true;

        // Build column visiting order using knight-move-inspired jumps
        while (columnOrder.size() < n) {
            List<Integer> validCols = new ArrayList<>();

            // Try knight moves from current column
            for (int i = 0; i < 8; i++) {
                int newCol = currentCol + colOffsets[i];

                if (newCol >= 0 && newCol < n && !visited[newCol]) {
                    validCols.add(newCol);
                }
            }

            if (validCols.isEmpty()) {
                // No valid knight moves - pick nearest unvisited column
                int minDist = Integer.MAX_VALUE;
                int bestCol = -1;

                for (int col = 0; col < n; col++) {
                    if (!visited[col]) {
                        int dist = Math.abs(col - currentCol);
                        if (dist < minDist) {
                            minDist = dist;
                            bestCol = col;
                        }
                    }
                }

                if (bestCol == -1) break;
                currentCol = bestCol;
            } else {
                // Pick random valid knight move
                currentCol = validCols.get(rand.nextInt(validCols.size()));
            }

            columnOrder.add(currentCol);
            visited[currentCol] = true;
        }

        // Now place queens column by column in the knight-tour order
        // This spreads out the placement pattern
        for (int colIdx : columnOrder) {
            int minConflicts = Integer.MAX_VALUE;
            List<Integer> bestRows = new ArrayList<>();

            // Try each possible row for this column
            for (int row = 0; row < n; row++) {
                int conflictCount = 0;

                conflictCount += rowQueensCnt[row].size();
                int diagRL = row + colIdx;
                conflictCount += diagonalRLCnt[diagRL].size();
                int diagLL = n - 1 - colIdx + row;
                conflictCount += diagonalLLCnt[diagLL].size();

                if (conflictCount < minConflicts) {
                    minConflicts = conflictCount;
                    bestRows.clear();
                    bestRows.add(row);
                } else if (conflictCount == minConflicts) {
                    bestRows.add(row);
                }
            }

            // Pick random row from best options
            int chosenRow = bestRows.get(rand.nextInt(bestRows.size()));

            board[colIdx] = chosenRow;
            rowQueensCnt[chosenRow].add(colIdx);
            int diagRL = chosenRow + colIdx;
            int diagLL = n - 1 - colIdx + chosenRow;
            diagonalRLCnt[diagRL].add(colIdx);
            diagonalLLCnt[diagLL].add(colIdx);
        }

        // Calculate initial conflicts for each queen
        for (int col = 0; col < n; col++) {
            int row = board[col];
            int conflictCount = 0;

            // Row conflicts
            conflictCount += rowQueensCnt[row].size() - 1;

            // Right-leaning diagonal conflicts
            int diagRL = row + col;
            conflictCount += diagonalRLCnt[diagRL].size() - 1;

            // Left-leaning diagonal conflicts
            int diagLL = n - 1 - col + row;
            conflictCount += diagonalLLCnt[diagLL].size() - 1;

            conflicts[col] = conflictCount;
        }
    }

    public static void minConflicts(int maxMoves, AtomicBoolean solved) {
        int mvCnt = 0;
//        initBoardRandom();
        initializeBoardKnightsTour();
        FRList conflicting = new FRList(n);
        // each queens' position in the frlist
        int[] frlistPositions = new int[n];
        calculateConflictingQueens(conflicting, frlistPositions);
        if(conflicting.isEmpty()) solved.set(true);

        while (mvCnt < maxMoves && !conflicting.isEmpty() && !solved.get()) {
            int q = conflicting.getRandomElement();
            minimizeConflicts(q, conflicting, frlistPositions, solved);
            mvCnt++;
        }
    }

    /**
     * @param q column of the conflicting queen
     */
    private static void minimizeConflicts(int q, FRList conflicting,
                                          int[] frlistPositions, AtomicBoolean solved) {
        int qPos = frlistPositions[q];
        frlistPositions[conflicting.getLast()] = qPos;
        conflicting.remove(qPos);

        int minInd = board[q];
        int minCnf = rowQueensCnt[minInd].size() +
                diagonalRLCnt[q + minInd].size() +
                diagonalLLCnt[n - q - 1 + minInd].size() - 3;

        for(int i = 0; i < n; i++) {
            int cnf = rowQueensCnt[i].size() +
                    diagonalRLCnt[q + i].size() +
                    diagonalLLCnt[n - q - 1 + i].size();
            if(board[q] == i) cnf -= 3;
            if(cnf < minCnf) {
                minCnf = cnf;
                minInd = i;
            }
        }

        if(minInd == board[q]) {
            conflicting.add(q);
            frlistPositions[q] = conflicting.size() - 1;
            return;
        }

        int oldRow = board[q];
        int oldDiagRL = board[q] + q;
        int oldDiagLL = n - q - 1 + board[q];

        // remove queen from old row and diagonals - NOW O(1) instead of O(n)!
        rowQueensCnt[oldRow].remove(q);
        diagonalRLCnt[oldDiagRL].remove(q);
        diagonalLLCnt[oldDiagLL].remove(q);

        // Process queens in old position
        for(int i : rowQueensCnt[oldRow]) {
            if(i == q) continue;

            int pos = frlistPositions[i];
            conflicts[i]--;
            if(board[i] == minInd) conflicts[i]++;
            if(board[i] + i == minInd + q) conflicts[i]++;
            if(board[i] - i == minInd - q) conflicts[i]++;

            if(conflicts[i] == 0) {
                if (!conflicting.isEmpty()) {
                    frlistPositions[conflicting.getLast()] = pos;
                }
                conflicting.remove(pos);
            }
        }

        for(int i : diagonalRLCnt[oldDiagRL]) {
            if(i == q) continue;

            int pos = frlistPositions[i];
            conflicts[i]--;
            if(board[i] == minInd) conflicts[i]++;
            if(board[i] + i == minInd + q) conflicts[i]++;
            if(board[i] - i == minInd - q) conflicts[i]++;

            if(conflicts[i] == 0) {
                if (!conflicting.isEmpty()) {
                    frlistPositions[conflicting.getLast()] = pos;
                }
                conflicting.remove(pos);
            }
        }

        for(int i : diagonalLLCnt[oldDiagLL]) {
            if(i == q) continue;

            int pos = frlistPositions[i];
            conflicts[i]--;
            if(board[i] == minInd) conflicts[i]++;
            if(board[i] + i == minInd + q) conflicts[i]++;
            if(board[i] - i == minInd - q) conflicts[i]++;

            if(conflicts[i] == 0) {
                if (!conflicting.isEmpty()) {
                    frlistPositions[conflicting.getLast()] = pos;
                }
                conflicting.remove(pos);
            }
        }

        // update queen position
        board[q] = minInd;
        int newDiagRL = board[q] + q;
        int newDiagLL = n - q - 1 + board[q];

        rowQueensCnt[board[q]].add(q);
        diagonalRLCnt[newDiagRL].add(q);
        diagonalLLCnt[newDiagLL].add(q);


        for(int i : rowQueensCnt[board[q]]) {
            if(i == q) continue;

            conflicts[i]++;
            if(conflicts[i] == 1) {
                conflicting.add(i);
                frlistPositions[i] = conflicting.size() - 1;
            }
        }

        for(int i : diagonalRLCnt[newDiagRL]) {
            if(i == q) continue;

            conflicts[i]++;
            if(conflicts[i] == 1) {
                conflicting.add(i);
                frlistPositions[i] = conflicting.size() - 1;
            }
        }

        for(int i : diagonalLLCnt[newDiagLL]) {
            if(i == q) continue;

            conflicts[i]++;
            if(conflicts[i] == 1) {
                conflicting.add(i);
                frlistPositions[i] = conflicting.size() - 1;
            }
        }

        // recalculate conflicts for moved queen q
        conflicts[q] = rowQueensCnt[board[q]].size() +
                diagonalRLCnt[board[q] + q].size() +
                diagonalLLCnt[n - q - 1 + board[q]].size() - 3;

        if(conflicts[q] != 0) {
            conflicting.add(q);
            frlistPositions[q] = conflicting.size() - 1;
        }

        if(conflicting.isEmpty()) solved.set(true);
    }

    public static void calculateConflictingQueens(FRList list, int[] frlistPositions) {
        list.reset();
        for(int i = 0; i < n; i++) {
            conflicts[i] = rowQueensCnt[board[i]].size() +
                    diagonalLLCnt[n - i - 1 + board[i]].size() +
                    diagonalRLCnt[board[i] + i].size() - 3;
            if(conflicts[i] != 0) {
                list.add(i);
                frlistPositions[i] = list.size() - 1;
            }
        }
    }
}