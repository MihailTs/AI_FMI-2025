package NQueens;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

// Fast random access list
public class FRList{

    private final int size;
    private int elCnt;
    private final ArrayList<Integer> container;
    private final Random rnd;
    FRList(int size) {
        this.size = size;
        elCnt = 0;
        container = new ArrayList<>(size);
        for (int i = 0; i < size; i++) container.add(null);
        rnd = new Random(LocalDateTime.now().getNano());
    }

    public Integer get(int index) {
        if(index < 0 || index >= elCnt) throw new ArrayIndexOutOfBoundsException();
        return container.get(index);
    }

    public void add(Integer val) {
        if(elCnt >= size) throw new ArrayIndexOutOfBoundsException();
        container.set(elCnt, val);
        elCnt++;
    }

    // that's the optimization compared to normal lists
    public void remove(int index) {
        if(elCnt == 0) throw new ArrayStoreException();
        container.set(index, container.get(elCnt - 1));
        elCnt--;
    }

    public int pickRandomIndex() {
        if(elCnt <= 0) throw new RuntimeException();
        return rnd.nextInt(elCnt);
    }

    public Integer getRandomElement() {
        return container.get(pickRandomIndex());
    }

    public void reset() {
        elCnt = 0;
    }

    public boolean isEmpty() {
        return elCnt == 0;
    }

    public int size() {
        return elCnt;
    }

    public void increaseBy(int index, int num) {
        if(index < 0 || index >= elCnt) throw new ArrayIndexOutOfBoundsException();
        container.set(index, container.get(index) + num);
    }

    public Integer getLast() {
        if(elCnt == 0) throw new RuntimeException("No element in list");
        return container.get(elCnt - 1);
    }

    public String toString() {
        return container.toString();
    }

}
