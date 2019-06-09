package com.dsa.fractals;

import org.apache.commons.math3.complex.Complex;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MandelbrotSet {

    private static final int MAX_ITERATIONS = 256;
    private static final double RADIUS = 2;
    private static final double DIVERGENCE = RADIUS * RADIUS;

    private BufferedImage bufferedImage;
    private int width;
    private int height;
    private double xLimitLower;
    private double xLimitUpper;
    private double yLimitLower;
    private double yLimitUpper;
    private int maxThreads;
    private String outputName;
    private boolean isQuiet;
    private int granularity;
    private int[] colors;

    public MandelbrotSet() {
        height = 480;
        width = 640;
        xLimitLower = -2.0;
        xLimitUpper = 2.0;
        yLimitLower = -2.0;
        yLimitUpper = 2.0;
        maxThreads = 1;
        outputName = "zad18.png";
        isQuiet = false;
        granularity = 1;
        colors = new int[MAX_ITERATIONS];
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            colors[i] = Color.HSBtoRGB(i / 256f, 1, i / (i + 8f));
        }
        createBufferedImage();
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double getxLimitLower() {
        return xLimitLower;
    }

    public double getxLimitUpper() {
        return xLimitUpper;
    }

    public double getyLimitLower() {
        return yLimitLower;
    }

    public double getyLimitUpper() {
        return yLimitUpper;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public String getOutputName() {
        return outputName;
    }

    public boolean isQuiet() {
        return isQuiet;
    }

    public int getGranularity() {
        return granularity;
    }

    public int[] getColors() {
        return colors;
    }

    public void createBufferedImage() {
        this.bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public void setWidth(final int width) {
        this.width = width;
    }

    public void setHeight(final int height) {
        this.height = height;
    }

    public void setxLimitLower(final double xLimitLower) {
        this.xLimitLower = xLimitLower;
    }

    public void setxLimitUpper(final double xLimitUpper) {
        this.xLimitUpper = xLimitUpper;
    }

    public void setyLimitLower(final double yLimitLower) {
        this.yLimitLower = yLimitLower;
    }

    public void setyLimitUpper(final double yLimitUpper) {
        this.yLimitUpper = yLimitUpper;
    }

    public void setMaxThreads(final int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public void setOutputName(final String outputName) {
        this.outputName = outputName;
    }

    public void setQuiet(final boolean quiet) {
        this.isQuiet = quiet;
    }

    public void setGranularity(final int granularity) {
        this.granularity = granularity;
    }

    public void generate() throws Exception {
        Util.exportImage(bufferedImage, outputName);
    }

    private double mapToReal(final int x) {
        final double range = xLimitUpper - xLimitLower;
        return x * (range / width) + xLimitLower;
    }

    private double mapToImaginary(final int y) {
        final double range = yLimitUpper - yLimitLower;
        return y * (range / height) + yLimitLower;
    }

    public void calculateChunk(final Chunk chunk) {
        for (int row = chunk.getStartHeight(); row < chunk.getEndHeight(); row++) {
            for (int col = 0; col < chunk.getWidth(); col++) { // this can be changed
                final double cReal = mapToReal(col);
                final double cImaginary = mapToImaginary(row);

                Complex z = new Complex(0, 0);
                final Complex c = new Complex(cReal, cImaginary);

                int iterationCounter = MAX_ITERATIONS;
                while (z.getReal() * z.getReal() + z.getImaginary() * z.getImaginary() <= DIVERGENCE && iterationCounter-- >= 0) {
                    z = c.multiply(z.cos());
                }
                if (iterationCounter >= 0) {
                    bufferedImage.setRGB(col, row, colors[iterationCounter]);
                } else {
                    bufferedImage.setRGB(col, row, 0x000000);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "MandelbrotSet{" +
                "bufferedImage=" + bufferedImage +
                ", width=" + width +
                ", height=" + height +
                ", xLimitLower=" + xLimitLower +
                ", xLimitUpper=" + xLimitUpper +
                ", yLimitLower=" + yLimitLower +
                ", yLimitUpper=" + yLimitUpper +
                ", maxThreads=" + maxThreads +
                ", outputName='" + outputName + '\'' +
                ", isQuiet=" + isQuiet +
                '}';
    }
}
