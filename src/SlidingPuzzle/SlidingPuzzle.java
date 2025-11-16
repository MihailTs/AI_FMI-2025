package SlidingPuzzle;

import java.util.Arrays;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

public class SlidingPuzzle {

    public static int[][] board;
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        int n = sc.nextInt();
        int zeroGoalPosition = sc.nextInt();
        if(zeroGoalPosition == -1) {
            zeroGoalPosition = n;
        }
        int size = (int) Math.sqrt(n + 1);
        board = new int[size][size];
        int zeroRow = -1;
        int zeroCol = -1;

        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                board[i][j] = sc.nextInt();
                if(board[i][j] == 0) {
                    zeroRow = i;
                    zeroCol = j;
                }
            }
        }

        if(!checkSolvable(size)) {
            System.out.print(-1);
            return;
        }

        int initManhDist = manhattonDistance(size, zeroGoalPosition);
        IDAStar(size, initManhDist, zeroRow, zeroCol, zeroGoalPosition);
    }

    private static void IDAStar(int size, int initManhDist,
                                int zeroRow, int zeroCol, int zeroGoalPosition) {
        AtomicBoolean solutionFound = new AtomicBoolean(false);
        int limit = initManhDist;
        while(true) {
//            System.out.println(limit);
            Move root = new Move(size, initManhDist, zeroRow, zeroCol);
            HeuristicDFS(root, size, zeroGoalPosition, limit, solutionFound);
//            AStarLimit(board, size, initManhDist, zeroRow, zeroCol, zeroGoalPosition, limit, solutionFound);
            if(solutionFound.get()) break;
            limit++;
        }
    }

//    private static void AStarLimit(int size, int initManhDist, int zeroRow,
//                                   int zeroCol, int zeroGoalPosition,  int limit, AtomicBoolean solutionFound) {
//        PriorityQueue<Move> pq = new PriorityQueue<>();
//
//        pq.add(new Move(copyBoard(board), size, initManhDist, zeroRow, zeroCol));
//
//        while(!pq.isEmpty()) {
//            Move m = pq.poll();
//            // we have found a solution
//            if(m.manhattonDistance == 0) {
//                solutionFound.set(true);
//                recoverSolution(m);
//                break;
//            }
//
//            zeroRow = m.oldRow;
//            zeroCol = m.oldCol;
//
//            if(m.prevMovesCount >= limit) continue;
//
//            // move down
//            if(zeroRow > 0 && m.previous != null && ) {
//                pq.add(new Move(m, copyBoard(m.board), m.board[zeroRow - 1][zeroCol], m.prevMovesCount + 1,
//                            zeroRow - 1, zeroCol, zeroRow, zeroCol, size, m.manhattonDistance, zeroGoalPosition));
//            }
//            // move right
//            if(zeroCol > 0) {
//                pq.add(new Move(m, copyBoard(m.board), m.board[zeroRow][zeroCol - 1], m.prevMovesCount + 1,
//                        zeroRow, zeroCol - 1, zeroRow, zeroCol, size, m.manhattonDistance, zeroGoalPosition));
//            }
//            // move up
//            if(zeroRow < size - 1) {
//                pq.add(new Move(m, copyBoard(m.board), m.board[zeroRow + 1][zeroCol], m.prevMovesCount + 1,
//                        zeroRow + 1, zeroCol, zeroRow, zeroCol, size, m.manhattonDistance, zeroGoalPosition));
//            }
//            // move left
//            if(zeroCol < size - 1) {
//                pq.add(new Move(m, copyBoard(m.board), m.board[zeroRow][zeroCol + 1], m.prevMovesCount + 1,
//                        zeroRow, zeroCol + 1, zeroRow, zeroCol, size, m.manhattonDistance, zeroGoalPosition));
//            }
//        }
//    }

    public static void HeuristicDFS(Move root, int size, int zeroGoalPosition,
                                    int limit, AtomicBoolean solutionFound) {
        if(solutionFound.get()) return;

        if(root.prevMovesCount + root.manhattonDistance > limit) {
            return;
        }
        if(root.manhattonDistance == 0) {
            solutionFound.set(true);
            recoverSolution(root);
            return;
        }

        int zeroRow = root.oldRow;
        int zeroCol = root.oldCol;

        // move down
        if(!solutionFound.get() && zeroRow > 0 && !(root.oldRow > root.newRow)) {
            Move next = new Move(root, board[zeroRow - 1][zeroCol], root.prevMovesCount + 1,
                    zeroRow - 1, zeroCol, zeroRow, zeroCol, size, root.manhattonDistance, zeroGoalPosition);
            board[zeroRow][zeroCol] = board[zeroRow - 1][zeroCol];
            board[zeroRow - 1][zeroCol] = 0;
            if(next.manhattonDistance + next.prevMovesCount <= limit) {
                HeuristicDFS(next, size, zeroGoalPosition, limit, solutionFound);
            }
            board[zeroRow - 1][zeroCol] = board[zeroRow][zeroCol];
            board[zeroRow][zeroCol] = 0;
        }
        // move right
        if(!solutionFound.get() && zeroCol > 0 && !(root.oldCol > root.newCol)) {
            Move next = new Move(root, board[zeroRow][zeroCol - 1], root.prevMovesCount + 1,
                    zeroRow, zeroCol - 1, zeroRow, zeroCol, size, root.manhattonDistance, zeroGoalPosition);
            board[zeroRow][zeroCol] = board[zeroRow][zeroCol - 1];
            board[zeroRow][zeroCol - 1] = 0;
            if(next.manhattonDistance + next.prevMovesCount <= limit) {
                HeuristicDFS(next, size, zeroGoalPosition, limit, solutionFound);
            }
            board[zeroRow][zeroCol - 1] = board[zeroRow][zeroCol];
            board[zeroRow][zeroCol] = 0;
        }
        // move up
        if(!solutionFound.get() && zeroRow < size - 1 && !(root.oldRow < root.newRow)) {
            Move next = new Move(root, board[zeroRow + 1][zeroCol], root.prevMovesCount + 1,
                    zeroRow + 1, zeroCol, zeroRow, zeroCol, size, root.manhattonDistance, zeroGoalPosition);
            board[zeroRow][zeroCol] = board[zeroRow + 1][zeroCol];
            board[zeroRow + 1][zeroCol] = 0;
            if(next.manhattonDistance + next.prevMovesCount <= limit) {
                HeuristicDFS(next, size, zeroGoalPosition, limit, solutionFound);
            }
            board[zeroRow + 1][zeroCol] = board[zeroRow][zeroCol];
            board[zeroRow][zeroCol] = 0;
        }
        // move left
        if(!solutionFound.get() && zeroCol < size - 1 && !(root.oldCol < root.newCol)) {
            Move next = new Move(root, board[zeroRow][zeroCol + 1], root.prevMovesCount + 1,
                    zeroRow, zeroCol + 1, zeroRow, zeroCol, size, root.manhattonDistance, zeroGoalPosition);
            board[zeroRow][zeroCol] = board[zeroRow][zeroCol + 1];
            board[zeroRow][zeroCol + 1] = 0;
            if(next.manhattonDistance + next.prevMovesCount <= limit) {
                HeuristicDFS(next, size, zeroGoalPosition, limit, solutionFound);
            }
            board[zeroRow][zeroCol + 1] = board[zeroRow][zeroCol];
            board[zeroRow][zeroCol] = 0;
        }
    }

    // display the solution steps
    private static void recoverSolution(Move m) {
        Stack<String> stack = new Stack<String>();

        while(m.previous != null) {
            if(m.oldRow < m.newRow) stack.push("down");
            else if(m.oldRow > m.newRow) stack.push("up");
            else if(m.oldCol < m.newCol) stack.push("right");
            else stack.push("left");
            m = m.previous;
        }

        System.out.print(stack.size());
        while(!stack.isEmpty()) {
            System.out.print("\n" + stack.pop());
        }
    }

    // In summary, when n is odd, an n-by-n board is solvable if and only if its number of inversions is even.
    // That is, when n is even, an n-by-n board is solvable if and only if the number of inversions
    // plus the row of the blank square (from bottom up) is odd.
    private static boolean checkSolvable(int size) {
        int[] flatArray = new int[size * size - 1];
        // counting from the bottom up
        int rowOfEmpty = -1;
        int idx = 0;
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                if(board[i][j] == 0)
                    rowOfEmpty = size - i - 1;
                else
                    flatArray[idx++] = board[i][j];
            }
        }

        int[] temp = new int[size * size - 1];
        int invCnt = mergeSortAndCount(flatArray, temp, 0, flatArray.length);

        if(size % 2 == 1)
            return invCnt % 2 == 0;
        else
            return (invCnt + rowOfEmpty) % 2 == 0;
    }

    // Merge sort modification that counts inversions
    private static int mergeSortAndCount(int[] arr, int[] temp, int l, int r) {
        int count = 0;

        if (r - l >= 2) {
            int m = (l + r) / 2;
            count += mergeSortAndCount(arr, temp, l, m);
            count += mergeSortAndCount(arr, temp, m, r);
            count += mergeAndCount(arr, temp, l, m, r);
        }

        return count;
    }

    private static int mergeAndCount(int[] arr, int[] temp, int l, int m, int r) {
        int i = l;
        int j = m;
        int k = l;
        int invCnt = 0;
        while(k < r) {
            if(j >= r || (i < m && arr[i] < arr[j])) {
                temp[k] = arr[i];
                i++;
            } else {
                temp[k] = arr[j];
                j++;
                invCnt += m - i;
            }
            k++;
        }
        System.arraycopy(temp, l, arr, l, r - l);
        return invCnt;
    }

    public static int manhattonDistance(int size, int zeroGoalPosition) {
        int dist = 0;
        for(int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if(board[i][j] == 0) continue;
                if(zeroGoalPosition >= board[i][j]) {
                    dist += (Math.abs(i - (board[i][j] - 1) / size)) + (Math.abs(j - (board[i][j] - 1) % size));
                } else {
                    dist += (Math.abs(i - board[i][j] / size)) + (Math.abs(j - board[i][j] % size));
                }
            }
        }

        return dist;
    }

}
