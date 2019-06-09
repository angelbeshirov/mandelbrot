package com.dsa.fractals;

import java.util.List;

public class MandelbrotWorker implements Runnable {
    
    private final MandelbrotSet mandelbrotSet;
    private final List<Chunk> chunks;
    private final int threadIndex;

    public MandelbrotWorker(final MandelbrotSet mandelbrotSet, final List<Chunk> chunks, final int threadIndex) {
        this.mandelbrotSet = mandelbrotSet;
        this.chunks = chunks;
        this.threadIndex = threadIndex;
    }

    @Override
    public void run() {
        final long startTime = System.currentTimeMillis();
        for (int i = 0; i < chunks.size(); i++) {
            if (i % mandelbrotSet.getMaxThreads() == threadIndex) {
                if (!mandelbrotSet.isQuiet()) {
                    System.out.println("Thread" + threadIndex + " is calculating " + chunks.get(i));
                }
                mandelbrotSet.calculateChunk(chunks.get(i));
            }
        }
        if (!mandelbrotSet.isQuiet()) {
            System.out.println("Thread" + threadIndex + " was processing for " + ((System.currentTimeMillis() - startTime)) + " ms");
        }
    }
}
