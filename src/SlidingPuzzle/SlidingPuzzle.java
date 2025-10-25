package SlidingPuzzle;

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

public class SlidingPuzzle {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        int n = sc.nextInt();
        int zeroGoalPosition = sc.nextInt();
        int size = (int) Math.sqrt(n + 1);

        int[][] board = new int[size][size];
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
            sc.nextLine();
        }

        if(!checkSolvable(board, size)) {
            System.out.println(-1);
            return;
        }

        int initManhDist = manhattonDistance(board, size, zeroGoalPosition);
        IDAStar(board, size, initManhDist, zeroRow, zeroCol, zeroGoalPosition);
    }

    private static void IDAStar(int[][] board, int size, int initManhDist,
                                int zeroRow, int zeroCol, int zeroGoalPosition) {
        AtomicBoolean solutionFound = new AtomicBoolean(false);
        int limit = 1;
        while(limit < 35) {
            AStarLimit(board, size, initManhDist, zeroRow, zeroCol, zeroGoalPosition, limit, solutionFound);
            if(solutionFound.get()) break;
            limit++;
        }
    }

    private static void AStarLimit(int[][] board, int size, int initManhDist, int zeroRow,
                                   int zeroCol, int zeroGoalPosition,  int limit, AtomicBoolean solutionFound) {
        PriorityQueue<Move> pq = new PriorityQueue<>();

        pq.add(new Move(copyBoard(board), size, initManhDist, zeroRow, zeroCol));

        while(!pq.isEmpty()) {
            Move m = pq.poll();
            // we have found a solution
            if(m.manhattonDistance == 0) {
                solutionFound.set(true);
                recoverSolution(m);
                break;
            }

            zeroRow = m.oldRow;
            zeroCol = m.oldCol;

            if(m.prevMovesCount >= limit) continue;

            // move down
            if(zeroRow > 0) {
                pq.add(new Move(m, copyBoard(m.board), m.board[zeroRow - 1][zeroCol], m.prevMovesCount + 1,
                            zeroRow - 1, zeroCol, zeroRow, zeroCol, size, m.manhattonDistance, zeroGoalPosition));
            }
            // move right
            if(zeroCol > 0) {
                pq.add(new Move(m, copyBoard(m.board), m.board[zeroRow][zeroCol - 1], m.prevMovesCount + 1,
                        zeroRow, zeroCol - 1, zeroRow, zeroCol, size, m.manhattonDistance, zeroGoalPosition));
            }
            // move up
            if(zeroRow < size - 1) {
                pq.add(new Move(m, copyBoard(m.board), m.board[zeroRow + 1][zeroCol], m.prevMovesCount + 1,
                        zeroRow + 1, zeroCol, zeroRow, zeroCol, size, m.manhattonDistance, zeroGoalPosition));
            }
            // move left
            if(zeroCol < size - 1) {
                pq.add(new Move(m, copyBoard(m.board), m.board[zeroRow][zeroCol + 1], m.prevMovesCount + 1,
                        zeroRow, zeroCol + 1, zeroRow, zeroCol, size, m.manhattonDistance, zeroGoalPosition));
            }
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

        System.out.println(stack.size());
        while(!stack.isEmpty()) {
            System.out.println(stack.pop());
        }
    }

    private static int[][] copyBoard(int[][] board) {
        int[][] boardCopy = new int[board.length][];
        for(int i = 0; i < board.length; i++) {
            boardCopy[i] = Arrays.copyOf(board[i], board[i].length);
        }
        return boardCopy;
    }

    // In summary, when n is odd, an n-by-n board is solvable if and only if its number of inversions is even.
    // That is, when n is even, an n-by-n board is solvable if and only if the number of inversions
    // plus the row of the blank square is odd.
    private static boolean checkSolvable(int[][] board, int size) {
        int[] flatArray = new int[size * size - 1];
        int rowOfEmpty = -1;
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                if(board[i][j] == 0)
                    rowOfEmpty = i;
                else
                    flatArray[i * size + j - ((rowOfEmpty == -1)? 0 : 1)] = board[i][j];
            }
        }

        int[] temp = new int[size * size - 1];
        int invCnt = mergeSortAndCount(flatArray, temp, 0, size * size - 1);
        if(size % 2 == 1)
            return invCnt % 2 == 0;
        else
            return invCnt + rowOfEmpty % 2 == 1;
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

    public static int manhattonDistance(int[][] board, int size, int zeroGoalPosition) {
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
