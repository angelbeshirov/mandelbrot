package com.dsa.fractals;

import org.apache.commons.math3.complex.Complex;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * The worker thread which processes different parts of the output image
 * based on its index and overall threads size. More precisely thread
 * X with index x will process a {@link Chunk} Y with index y if
 * y % maxThread == x.
 */
public class MandelbrotWorker implements Runnable {

    private final MandelbrotSet mandelbrotSet;
    private final int threadIndex;
    private final BufferedImage bufferedImage;
    private final int[] colors;
    private final int width;
    private final int height;
    private final double xLimitUpper;
    private final double xLimitLower;
    private final double yLimitUpper;
    private final double yLimitLower;
    private final List<Chunk> chunks;

    /**
     * Sets up the fields.
     *
     * @param mandelbrotSet contains the fields related to the current configuration of the problem
     * @param chunks        the list of chunks
     * @param threadIndex   the index of the thread
     */
    public MandelbrotWorker(final MandelbrotSet mandelbrotSet, final List<Chunk> chunks, final int threadIndex) {
        this.mandelbrotSet = mandelbrotSet;
        this.threadIndex = threadIndex;
        this.bufferedImage = mandelbrotSet.getBufferedImage();
        colors = mandelbrotSet.getColors();
        this.height = mandelbrotSet.getHeight();
        this.width = mandelbrotSet.getWidth();
        this.xLimitUpper = mandelbrotSet.getxLimitUpper();
        this.xLimitLower = mandelbrotSet.getxLimitLower();
        this.yLimitUpper = mandelbrotSet.getyLimitUpper();
        this.yLimitLower = mandelbrotSet.getyLimitLower();
        this.chunks = chunks;
    }

    /**
     * Iterates the list of chunks and picks up the chunks it has to based on the logic mentioned above.
     * After that processes the chunks by mapping each pixel from the image to a point in the complex
     * plane and then iterating that point.
     */
    @Override
    public void run() {
        int howMany = 0;
        final long startTime = System.currentTimeMillis();
        final int maxThreads = mandelbrotSet.getMaxThreads();
        for (int s = threadIndex; s < chunks.size(); s += maxThreads) {
            final Chunk toProcess = chunks.get(s);
            howMany++;
            for (int row = toProcess.getStartHeight(); row < toProcess.getEndHeight(); row++) {
                for (int col = toProcess.getStartWidth(); col < toProcess.getEndWidth(); col++) {
                    final double cImaginary = row * ((yLimitUpper - yLimitLower) / height) + yLimitLower;
                    final double cReal = col * ((xLimitUpper - xLimitLower) / width) + xLimitLower;
                    final Complex c = new Complex(cReal, cImaginary);
                    bufferedImage.setRGB(col, row, colors[iterate(c)]);
                }
            }
        }

        if (!mandelbrotSet.isQuiet()) {
            System.out.println("Thread " + threadIndex + " processed " + howMany);
            System.out.println("Thread" + threadIndex + " was processing for " + ((System.currentTimeMillis() - startTime)) + " ms");
        }
    }

    /**
     * Iterates the complex number passed as argument to determine whether it is
     * from the mandelbrot set. If it isn't it returns the number of iterations it took
     * to check that.
     *
     * @param c the complex number to iterate
     * @return the number of iterations done to check whether the number is from the mandelbrot set
     */
    private static int iterate(Complex c) {
        Complex z_prev = new Complex(0.0, 0.0);
        Complex z_i = null;
        Double d = null;
        int iterations = 0;
        for (int i = 0; i < MandelbrotSet.MAX_ITERATIONS; i++) {
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
}
