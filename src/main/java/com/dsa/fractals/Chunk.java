package com.dsa.fractals;

public class Chunk {

    private int startHeight;
    private int endHeight;
    private int width;

    Chunk(final int startHeight, final int endHeight, final int width) {
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.width = width;
    }

    public int getStartHeight() {
        return startHeight;
    }

    public int getEndHeight() {
        return endHeight;
    }

    public int getWidth() {
        return width;
    }

    @Override
    public String toString() {
        return "Chunk{" +
                "startHeight=" + startHeight +
                ", endHeight=" + endHeight +
                ", width=" + width +
                '}';
    }
}
