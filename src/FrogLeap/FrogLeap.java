package FrogLeap;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class FrogLeap {

    static StringBuilder state = new StringBuilder(" ");

//    public static void dfs(StringBuilder state, String finalState, int spacePosition, int size,
//                           ArrayList<String> statesPath, AtomicBoolean solutionFound) {
//        statesPath.add(String.valueOf(state));
//        if(state.toString().equals(finalState)) {
//            for(String s : statesPath) {
//                System.out.println(s);
//            }
//            solutionFound.set(true);
//        }
//        if(solutionFound.get()) return;
//
//        if(spacePosition - 1 >= 0 && state.charAt(spacePosition - 1) == '>') {
//            state.setCharAt(spacePosition, '>');
//            state.setCharAt(spacePosition - 1, ' ');
//            dfs(state, finalState, spacePosition - 1, size, statesPath, solutionFound);
//            state.setCharAt(spacePosition, ' ');
//            state.setCharAt(spacePosition - 1, '>');
//        }
//
//        if(spacePosition - 2 >= 0 && state.charAt(spacePosition - 2) == '>') {
//            state.setCharAt(spacePosition, '>');
//            state.setCharAt(spacePosition - 2, ' ');
//            dfs(state, finalState, spacePosition - 2, size, statesPath, solutionFound);
//            state.setCharAt(spacePosition, ' ');
//            state.setCharAt(spacePosition - 2, '>');
//        }
//
//        if(spacePosition + 1 < size && state.charAt(spacePosition + 1) == '<') {
//            state.setCharAt(spacePosition, '<');
//            state.setCharAt(spacePosition + 1, ' ');
//            dfs(state, finalState, spacePosition + 1, size, statesPath, solutionFound);
//            state.setCharAt(spacePosition, ' ');
//            state.setCharAt(spacePosition + 1, '<');
//        }
//
//        if(spacePosition + 2 < size && state.charAt(spacePosition + 2) == '<') {
//            state.setCharAt(spacePosition, '<');
//            state.setCharAt(spacePosition + 2, ' ');
//            dfs(state, finalState, spacePosition + 2, size, statesPath, solutionFound);
//            state.setCharAt(spacePosition, ' ');
//            state.setCharAt(spacePosition + 2, '<');
//        }
//
//        statesPath.remove(statesPath.size() - 1);
//    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n;

        do {
            n = sc.nextInt();
        } while(n % 2 == 0);

        StringBuilder finalState = new StringBuilder(" ");
        for(int i = 0; i < n / 2; i++) {
            state.insert(0, ">");
            state.append("<");
            finalState.insert(0, "<");
            finalState.append(">");
        }

        int stepCnt = 1;
        boolean leftFrTurn = true;
        int spacePosition = n / 2;
        int helpCnt = 3;
        while(stepCnt > 0) {
            for(int i = 0; i < stepCnt; i++) {
                if(leftFrTurn) {
                    if(state.charAt(spacePosition - 1) == '>') {
                        state.setCharAt(spacePosition, '>');
                        state.setCharAt(spacePosition - 1, ' ');
                        spacePosition--;
                    } else if(state.charAt(spacePosition - 2) == '>') {
                        state.setCharAt(spacePosition, '>');
                        state.setCharAt(spacePosition - 2, ' ');
                        spacePosition -= 2;
                    }
                } else {
                    if(state.charAt(spacePosition + 1) == '<') {
                        state.setCharAt(spacePosition, '<');
                        state.setCharAt(spacePosition + 1, ' ');
                        spacePosition++;
                    } else if(state.charAt(spacePosition + 2) == '<') {
                        state.setCharAt(spacePosition, '<');
                        state.setCharAt(spacePosition + 2, ' ');
                        spacePosition += 2;
                    }
                }
                System.out.println(state);
            }

            leftFrTurn = !leftFrTurn;
            if(stepCnt == n / 2) helpCnt--;
            if(helpCnt == 3) stepCnt++;
            if(helpCnt == 0) stepCnt--;
        }

//        int spacePosition = n / 2;
//        ArrayList<String> statesPath = new ArrayList<>();
//        AtomicBoolean solutionFound = new AtomicBoolean(false);
//        dfs(state, finalState.toString(), spacePosition, n, statesPath, solutionFound);
    }
}
