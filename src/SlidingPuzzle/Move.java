package SlidingPuzzle;

public class Move implements Comparable<Move> {

    static int cnt = 0;
    int selfCnt;
    int[][] board;
    int numberChanged;
    int oldRow, oldCol, newRow, newCol;
    // manhatton distance after move
    int manhattonDistance;
    // count of moves before this one
    int prevMovesCount;
    int boardSize;
    Move previous;
    int zeroGoalPosition;

    // to create the initial move
    public Move(int[][] board, int boardSize, int manhattonDistance, int zeroRow, int zeroCol) {
        this.board = board;
        this.previous = null;
        this.numberChanged = -1;
        this.prevMovesCount = 0;
        this.oldRow = zeroRow;
        this.oldCol = zeroCol;
        // newRow and newCol are irrelevant in initial move
        this.newRow = zeroRow;
        this.newCol = zeroCol;
        this.boardSize = boardSize;
        this.manhattonDistance = manhattonDistance;
    }

    public Move(Move previous, int[][] board, int numberChanged, int prevMovesCount, int oldRow, int oldCol,
                    int newRow, int newCol, int boardSize, int manhattonDistance, int zeroGoalPosition) {
        this.board = board;
        // make the move on the board
        this.board[oldRow][oldCol] = 0;
        this.board[newRow][newCol] = numberChanged;

        this.previous = previous;
        this.numberChanged = numberChanged;
        this.prevMovesCount = prevMovesCount;
        this.oldRow = oldRow;
        this.oldCol = oldCol;
        this.newRow = newRow;
        this.newCol = newCol;
        this.boardSize = boardSize;
        cnt++;
        this.selfCnt = cnt;

        // calculating the new manhatton distance
        if(zeroGoalPosition >= numberChanged) {
            this.manhattonDistance = manhattonDistance - ((Math.abs(oldRow - (numberChanged - 1) / boardSize)) +
                                                            (Math.abs(oldCol - (numberChanged - 1) % boardSize)));
            this.manhattonDistance += (Math.abs(newRow - (numberChanged - 1) / boardSize)) +
                                        (Math.abs(newCol - (numberChanged - 1) % boardSize));
        } else {
            this.manhattonDistance = manhattonDistance - ((Math.abs(oldRow - numberChanged / boardSize)) +
                                                            (Math.abs(oldCol - numberChanged % boardSize)));
            this.manhattonDistance += (Math.abs(newRow - numberChanged / boardSize)) +
                                        (Math.abs(newCol - numberChanged % boardSize));
        }
    }

    @Override
    public int compareTo(Move other) {
        return Integer.compare(prevMovesCount + manhattonDistance, other.prevMovesCount + other.manhattonDistance);
    }

    public String toString() {
        return selfCnt +  "  Move " + this.numberChanged + "  from (" + oldRow + ", " + oldCol +
                ") to (" + newRow + ", " + newCol + ")    prev: " + ((this.previous == null)? "" : this.previous.selfCnt) + "   " + this.manhattonDistance;
    }

}
