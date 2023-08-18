package com.github.lfeagan.monitoring;

public class MemoryAllocationWorkload {

    public void allocateIntArrays(short maxPowerOfTwoSize) {
        for (int i=0; i < maxPowerOfTwoSize; ++i) {
            int size = 1 << i;
            allocateIntArray(size);
        }
    }

    public int[] allocateIntArray(int size) {
        int[] values = new int[size];
        return values;
    }

    public void allocateStrings(short maxPowerOfTwoSize) {
        for (int i=0; i < maxPowerOfTwoSize; ++i) {
            int size = 1 << i;
            allocateString(size);
        }
    }

    public String allocateString(int size) {
        char[] chars = new char[size];
        return String.valueOf(chars);
    }
}
