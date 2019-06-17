package com.dsa.fractals;

import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The main class of the application, parses the command-line arguments,
 * splits the image into chunks, starts the threads and exports the image.
 */
public class Runner {

    public static void main(final String[] args) throws Exception {
        final long start = System.currentTimeMillis();

        final MandelbrotSet mandelbrotSet = initMandelbrotSet(args);
        if (!mandelbrotSet.isQuiet())
            System.out.println(mandelbrotSet.toString());

        List<Chunk> chunks = makeChunks(mandelbrotSet);

        final ExecutorService executorService = Executors.newFixedThreadPool(mandelbrotSet.getMaxThreads());
        for (int i = 0; i < mandelbrotSet.getMaxThreads(); i++) {
            executorService.submit(new MandelbrotWorker(mandelbrotSet, chunks, i));
        }

        shutdownAndAwaitTermination(executorService);
        mandelbrotSet.generate();

        System.out.println("Total time elapsed: " + (System.currentTimeMillis() - start));
    }

    /**
     * Generates list of {@link Chunk}s based on the granularity, number of threads, width
     * and height of the image. In general each chunk will be with width imageWidth/(numberOfThreads * granularity)
     * and height imageHeight/(numberOfThreads * granularity)
     *
     * @param mandelbrotSet contains the problem configuration
     * @return the generated list of chunks
     */
    private static List<Chunk> makeChunks(final MandelbrotSet mandelbrotSet) {
        int granularity = mandelbrotSet.getGranularity();
        final List<Chunk> chunks = new ArrayList<>();
        final int maxThreads = mandelbrotSet.getMaxThreads();
        int i;
        int j;
        int width = mandelbrotSet.getWidth();
        int height = mandelbrotSet.getHeight();

        // round UP integer division
        int stepHeight = (mandelbrotSet.getHeight() + maxThreads * granularity - 1) / (maxThreads * granularity);
        int stepWidth = (mandelbrotSet.getWidth() + maxThreads * granularity - 1) / (maxThreads * granularity);

        for (i = 0; i + stepHeight < height; i += stepHeight) {
            for (j = 0; j + stepWidth < width; j += stepWidth) {
                chunks.add(new Chunk(i, i + stepHeight, j, j + stepWidth));
            }
            chunks.add(new Chunk(i, i + stepHeight, j, width));
        }
        int k;
        for (k = 0; k + stepWidth < width; k += stepWidth) {
            chunks.add(new Chunk(i, height, k, k + stepWidth));
        }
        chunks.add(new Chunk(i, height, k, width));

        for (Chunk chunk : chunks) {
            System.out.println(chunk);
        }
        return chunks;
    }


    /**
     * Shuts down the executor service.
     *
     * @param executorService the {@link ExecutorService} to shutdown
     */
    private static void shutdownAndAwaitTermination(ExecutorService executorService) {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(300, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(300, TimeUnit.SECONDS)) {
                    System.err.println("ExecutorService did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Initializes {@link MandelbrotSet} based on the command-line arguments passed by the user.
     *
     * @param args the array containing the command-line arguments
     * @return initialized {@link MandelbrotSet} object.
     * @throws ParseException if there was error while parsing the arguments
     */
    private static MandelbrotSet initMandelbrotSet(final String[] args) throws ParseException {
        final Options options = new Options();
        options.addOption("s", "size", true, "Sets the width and height of the output image");
        options.addOption("r", "rect", true, "Sets the part of the plane for which the fractal will be created");
        options.addOption("t", "tasks", true, "Sets the number of threads which will be used");
        options.addOption("o", "output", true, "Sets the output name of the generated image");
        options.addOption("g", "gran", true, "Sets the granularity of the image partitioning");
        options.addOption("q", "quiet", false, "Sets the application in quiet mode");

        final CommandLineParser parser = new DefaultParser();
        final CommandLine cmd = parser.parse(options, args);
        final MandelbrotSet mandelbrotSet = new MandelbrotSet();
        mandelbrotSet.setQuiet(cmd.hasOption("q"));

        if (cmd.hasOption("o")) {
            mandelbrotSet.setOutputName(cmd.getOptionValue("o"));
        }

        if (cmd.hasOption("s")) {
            final String[] size = cmd.getOptionValue("s").split("x");
            mandelbrotSet.setWidth(Integer.parseInt(size[0]));
            mandelbrotSet.setHeight(Integer.parseInt(size[1]));
            mandelbrotSet.createBufferedImage();
        }

        if (cmd.hasOption("r")) {
            final String[] limits = cmd.getOptionValue("r").split(":");
            mandelbrotSet.setxLimitLower(Double.parseDouble(limits[0]));
            mandelbrotSet.setxLimitUpper(Double.parseDouble(limits[1]));
            mandelbrotSet.setyLimitLower(Double.parseDouble(limits[2]));
            mandelbrotSet.setyLimitUpper(Double.parseDouble(limits[3]));
        }

        if (cmd.hasOption("t")) {
            mandelbrotSet.setMaxThreads(Integer.parseInt(cmd.getOptionValue("t")));
        }

        if (cmd.hasOption("g")) {
            mandelbrotSet.setGranularity(Integer.parseInt(cmd.getOptionValue("g")));
        }

        return mandelbrotSet;
    }
}
