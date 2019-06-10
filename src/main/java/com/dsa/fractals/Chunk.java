package com.dsa.fractals;

public class Chunk {

    private int startHeight;
    private int endHeight;

    Chunk(final int startHeight, final int endHeight) {
        this.startHeight = startHeight;
        this.endHeight = endHeight;
    }

    public int getStartHeight() {
        return startHeight;
    }

    public int getEndHeight() {
        return endHeight;
    }

    @Override
    public String toString() {
        return "Chunk{" +
                "startHeight=" + startHeight +
                ", endHeight=" + endHeight +
                '}';
    }
}
