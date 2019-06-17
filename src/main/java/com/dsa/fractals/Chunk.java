package com.dsa.fractals;

/**
 * Object representation of a part of the image. This is used to distribute
 * the amount of work done by the application more equally among
 * different threads.
 */
public class Chunk {

    private int startHeight;
    private int endHeight;
    private int startWidth;
    private int endWidth;

    public Chunk(final int startHeight, final int endHeight, final int startWidth, final int endWidth) {
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.startWidth = startWidth;
        this.endWidth = endWidth;
    }

    public int getStartHeight() {
        return startHeight;
    }

    public int getEndHeight() {
        return endHeight;
    }

    public int getStartWidth() {
        return startWidth;
    }

    public int getEndWidth() {
        return endWidth;
    }

    @Override
    public String toString() {
        return "Chunk{" +
                "startHeight=" + startHeight +
                ", endHeight=" + endHeight +
                ", startWidth=" + startWidth +
                ", endWidth=" + endWidth +
                '}';
    }
}
