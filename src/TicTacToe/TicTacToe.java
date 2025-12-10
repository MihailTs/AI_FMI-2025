package TicTacToe;

import java.util.Scanner;

public class TicTacToe {

    static Board board;
    static final String EMPTY = "_";
    static final String X_SYMBOL = "X";
    static final String O_SYMBOL = "O";
    static boolean game;
    // 0 - human, 1 - computer (Only in GAME mode)
    static int playerTurn;
    static String humanSymbol;

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        String mode = sc.nextLine();
        board = new Board(EMPTY, X_SYMBOL, O_SYMBOL);

        try {
            if (mode.equals("JUDGE")) {
                parseJudgeInput(sc);
                game = false;
            } else if (mode.equals("GAME")) {
                parseGameInput(sc);
                game = true;
            } else {
                throw new IllegalArgumentException("Game mode not valid!");
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }

        int endgame = board.endgame();
        if(!game) {
            if(endgame != 0) {
                System.out.print(-1);
                return;
            }
            int[] move = board.nextMove();
            System.out.print(move[0] + " " + move[1]);
        } else {
            while(board.endgame() == 0) {
                if(playerTurn == 0) {
                    playerTurn(sc);
                }
                else if(playerTurn == 1){
                    board.nextMove();
                } else {
                    throw new RuntimeException("Illegal value for playerTurn");
                }
                board.prettyPrint();
                playerTurn = 1 - playerTurn;
                endgame = board.endgame();
            }
            if(endgame == 2) System.out.print("DRAW");
            if(endgame == -1) System.out.print("WINNER: X");
            if(endgame == 1) System.out.print("WINNER: O");
        }
    }

    private static void playerTurn(Scanner sc) {
        int row = sc.nextInt();
        int col = sc.nextInt();
        while(!board.isTaken(row, col)) {
            System.out.println("This cell is already taken. Try another one");
            row = sc.nextInt();
            col = sc.nextInt();
        }
        board.move(row, col);
    }

    public static void parseJudgeInput(Scanner sc) throws IllegalArgumentException {
        sc.next();
        String t = sc.next();
        // the bot turn is the compliment of the human num
        if(t.equals(X_SYMBOL)) board.setHumanNum(1);
        else if(t.equals(O_SYMBOL)) board.setHumanNum(-1);
        else {
            throw new IllegalArgumentException(String.format("Illegal turn name! Either %s or %s", X_SYMBOL, O_SYMBOL));
        }

        parseBoard(sc);
    }

    public static void parseGameInput(Scanner sc) throws IllegalArgumentException {
        sc.next();
        String t1 = sc.next();
        if(!t1.equals(X_SYMBOL) && !t1.equals(O_SYMBOL))
            throw new IllegalArgumentException(String.format("Illegal turn name! Either %s or %s", X_SYMBOL, O_SYMBOL));

        sc.nextLine();
        sc.next();
        String t2 = sc.next();
        if(t2.equals(X_SYMBOL)) {
            board.setHumanNum(-1);
        }
        else if(t2.equals(O_SYMBOL)) {
            board.setHumanNum(1);
        }
        else
            throw new IllegalArgumentException(String.format("Illegal turn name! Either %s or %s", X_SYMBOL, O_SYMBOL));

        if(t1.equals(t2)) playerTurn = 0;
        else playerTurn = 1;

        parseBoard(sc);
    }

    public static void checkBoardCharacter(String m) throws IllegalArgumentException{
        if(!m.equals(EMPTY) && !m.equals(X_SYMBOL) && !m.equals(O_SYMBOL))
            throw new IllegalArgumentException("Illegal board symbol!");
    }

    public static void parseBoard(Scanner sc) {
        sc.nextLine();
        for(int i = 0; i < 3; i++) {
            sc.nextLine();
            sc.next();
            String m = sc.next();
            checkBoardCharacter(m);
            board.set(i + 1, 1, m);
            sc.next();
            m = sc.next();
            board.set(i + 1, 2, m);
            sc.next();
            m = sc.next();
            board.set(i + 1, 3, m);
            sc.nextLine();
        }
        sc.nextLine();
    }

}
