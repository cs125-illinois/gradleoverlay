package edu.illinois.cs.cs125.example;

// This is the student's version of the file under test. The changes should appear in the overlay.

public class Accumulator {

    private int value;

    public Accumulator(int init) {
        value = init;
    }

    public void add(int extra) {
        value = Provided.add(value, extra);
    }

    public void multiply(int factor) {
        // BROKEN: no-op
    }

    public int get() {
        return value;
    }

}
