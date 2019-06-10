package com.dsa.fractals;

import org.apache.commons.math3.complex.Complex;

import java.awt.image.BufferedImage;
import java.util.List;

public class MandelbrotWorker implements Runnable {
    
    private final MandelbrotSet mandelbrotSet;
    private final List<Chunk> chunks;
    private final int threadIndex;
    private final BufferedImage bufferedImage;
    private final int[] colors;
    private final int width;
    private final int height;
    private final double xLimitUpper;
    private final double xLimitLower;
    private final double yLimitUpper;
    private final double yLimitLower;

    public MandelbrotWorker(final MandelbrotSet mandelbrotSet, final List<Chunk> chunks, final int threadIndex) {
        this.mandelbrotSet = mandelbrotSet;
        this.chunks = chunks;
        this.threadIndex = threadIndex;
        this.bufferedImage = mandelbrotSet.getBufferedImage();
        colors = mandelbrotSet.getColors();
        this.height = mandelbrotSet.getHeight();
        this.width = mandelbrotSet.getWidth();
        this.xLimitUpper = mandelbrotSet.getxLimitUpper();
        this.xLimitLower = mandelbrotSet.getxLimitLower();
        this.yLimitUpper = mandelbrotSet.getyLimitUpper();
        this.yLimitLower = mandelbrotSet.getyLimitLower();
    }

    @Override
    public void run() {
        final long startTime = System.currentTimeMillis();
        int maxThreads = mandelbrotSet.getMaxThreads();
//        for (int i = threadIndex; i < chunks.size(); i+= maxThreads) {
//            if (!mandelbrotSet.isQuiet()) {
//                System.out.println("Thread" + threadIndex + " is calculating " + chunks.get(i));
//            }
//            calculateChunk(chunks.get(i));
//        }

        for (int row = threadIndex; row < height; row+= maxThreads) {
            final double cImaginary = mapToImaginary(row);
            for (int col = 0; col < width; col++) { // this can be change
                final double cReal = mapToReal(col);
                final Complex c = new Complex(cReal, cImaginary);
                int iterations = iterate(c);
                if (iterations >= 0) {
                    bufferedImage.setRGB(col, row, colors[iterations]);
                } else {
                    bufferedImage.setRGB(col, row, 0x000000);
                }
            }
        }

        if (!mandelbrotSet.isQuiet()) {
            System.out.println("Thread" + threadIndex + " was processing for " + ((System.currentTimeMillis() - startTime)) + " ms");
        }
    }

    private double mapToReal(final int x) {
        final double range = xLimitUpper - xLimitLower;
        return x * (range / width) + xLimitLower;
    }

    private double mapToImaginary(final int y) {
        final double range = yLimitUpper - yLimitLower;
        return y * (range / height) + yLimitLower;
    }

    public static int iterate(Complex c) {

        Complex z_prev = new Complex(0.0, 0.0);
        Complex z_i = null;
        Double d = null;
        int iterations = 0;
        for(int i = 0; i < MandelbrotSet.MAX_ITERATIONS; i++) {
//            z_i = apply(z_prev, c);
            z_i = c.multiply(z_prev.cos());
            z_prev = z_i;
            d = z_prev.getReal();
            if (d.isInfinite() || d.isNaN()) {
                iterations = i;
                break;
            }
        }
        return iterations;
    }

    public void calculateChunk(final Chunk chunk) {
        for (int row = chunk.getStartHeight(); row < chunk.getEndHeight(); row++) {
            for (int col = 0; col < width; col++) { // this can be changed
                final double cImaginary = mapToImaginary(row);
                final double cReal = mapToReal(col);
                final Complex c = new Complex(cReal, cImaginary);
                int iterations = iterate(c);
                if (iterations >= 0) {
                    bufferedImage.setRGB(col, row, colors[iterations]);
                } else {
                    bufferedImage.setRGB(col, row, 0x000000);
                }
            }
        }
    }

    public static Complex apply(Complex z, Complex c) {
        return c.multiply(z.cos());
    }
}
