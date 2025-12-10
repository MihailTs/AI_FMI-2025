package FrogLeap;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class FrogLeap {

    static StringBuilder state = new StringBuilder("_");

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
//            state.setCharAt(spacePosition - 1, '_');
//            dfs(state, finalState, spacePosition - 1, size, statesPath, solutionFound);
//            state.setCharAt(spacePosition, '_');
//            state.setCharAt(spacePosition - 1, '>');
//        }
//
//        if(spacePosition - 2 >= 0 && state.charAt(spacePosition - 2) == '>') {
//            state.setCharAt(spacePosition, '>');
//            state.setCharAt(spacePosition - 2, '_');
//            dfs(state, finalState, spacePosition - 2, size, statesPath, solutionFound);
//            state.setCharAt(spacePosition, '_');
//            state.setCharAt(spacePosition - 2, '>');
//        }
//
//        if(spacePosition + 1 < size && state.charAt(spacePosition + 1) == '<') {
//            state.setCharAt(spacePosition, '<');
//            state.setCharAt(spacePosition + 1, '_');
//            dfs(state, finalState, spacePosition + 1, size, statesPath, solutionFound);
//            state.setCharAt(spacePosition, '_');
//            state.setCharAt(spacePosition + 1, '<');
//        }
//
//        if(spacePosition + 2 < size && state.charAt(spacePosition + 2) == '<') {
//            state.setCharAt(spacePosition, '<');
//            state.setCharAt(spacePosition + 2, '_');
//            dfs(state, finalState, spacePosition + 2, size, statesPath, solutionFound);
//            state.setCharAt(spacePosition, '_');
//            state.setCharAt(spacePosition + 2, '<');
//        }
//
//        statesPath.remove(statesPath.size() - 1);
//    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();

//        StringBuilder finalState = new StringBuilder("_");
        for(int i = 0; i < n; i++) {
            state.insert(0, ">");
            state.append("<");
//            finalState.insert(0, "<");
//            finalState.append(">");
        }

        int stepCnt = 1;
        boolean leftFrTurn = true;
        int spacePosition = n;
        int helpCnt = 3;

        System.out.print(state);
        while(stepCnt > 0) {
            for(int i = 0; i < stepCnt; i++) {
                System.out.println();
                if(leftFrTurn) {
                    if(state.charAt(spacePosition - 1) == '>') {
                        state.setCharAt(spacePosition, '>');
                        state.setCharAt(spacePosition - 1, '_');
                        spacePosition--;
                    } else if(state.charAt(spacePosition - 2) == '>') {
                        state.setCharAt(spacePosition, '>');
                        state.setCharAt(spacePosition - 2, '_');
                        spacePosition -= 2;
                    }
                } else {
                    if(state.charAt(spacePosition + 1) == '<') {
                        state.setCharAt(spacePosition, '<');
                        state.setCharAt(spacePosition + 1, '_');
                        spacePosition++;
                    } else if(state.charAt(spacePosition + 2) == '<') {
                        state.setCharAt(spacePosition, '<');
                        state.setCharAt(spacePosition + 2, '_');
                        spacePosition += 2;
                    }
                }
                System.out.print(state);
            }

            leftFrTurn = !leftFrTurn;
            if(stepCnt == n) helpCnt--;
            if(helpCnt == 3) stepCnt++;
            if(helpCnt == 0) stepCnt--;
        }

//        int spacePosition = n;
//        ArrayList<String> statesPath = new ArrayList<>();
//        AtomicBoolean solutionFound = new AtomicBoolean(false);
//        dfs(state, finalState.toString(), spacePosition, 2*n + 1, statesPath, solutionFound);
    }
}
