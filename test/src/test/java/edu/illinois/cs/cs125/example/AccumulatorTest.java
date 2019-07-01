package edu.illinois.cs.cs125.example;

// This is a test suite. Students get a copy of it but cannot modify it.

import org.junit.Assert;
import org.junit.Test;

public class AccumulatorTest {

    @Test
    public void testAdding() {
        Accumulator acc = new Accumulator(0);
        acc.add(5);
        acc.add(10);
        Assert.assertEquals(15, acc.get());
    }

    @Test
    public void testMultiplying() {
        Accumulator acc = new Accumulator(1);
        acc.multiply(2);
        acc.multiply(3);
        acc.multiply(2);
        Assert.assertEquals(12, acc.get());
    }

    @Test
    public void testBoth() {
        Accumulator acc = new Accumulator(5);
        acc.multiply(2);
        acc.add(1);
        acc.multiply(3);
        Assert.assertEquals(33, acc.get());
    }

}
