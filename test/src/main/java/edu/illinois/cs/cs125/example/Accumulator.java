package edu.illinois.cs.cs125.example;

// This is a file under test.
// Students need to create it; their version should be in the overlay.

public class Accumulator {

    private int value;

    public Accumulator(int init) {
        value = init;
    }

    public void add(int extra) {
        value = Provided.add(value, extra);
    }

    public void multiply(int factor) {
        value = ImplementationDetail.multiply(value, factor);
    }

    public int get() {
        return value;
    }

}
