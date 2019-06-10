package com.dsa.fractals;

import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Runner {

    public static void main(final String[] args) throws Exception {
        final long start = System.currentTimeMillis();

        final List<Chunk> chunks = new ArrayList<>();
        final MandelbrotSet mandelbrotSet = initMandelbrotSet(args);
        System.out.println(mandelbrotSet.toString());
//        final int chunksSize = mandelbrotSet.getMaxThreads() * mandelbrotSet.getGranularity();
//        final int step = (mandelbrotSet.getHeight() + chunksSize - 1) / (chunksSize); // this is needed for rounding up the integer division

//        int part = 0;
//        while (part + step <= mandelbrotSet.getHeight()) {
//            chunks.add(new Chunk(part, part + step));
//            part += step;
//        }
//
//        if (part != mandelbrotSet.getHeight()) {
//            chunks.add(new Chunk(part, mandelbrotSet.getHeight()));
//        }

        final ExecutorService executorService = Executors.newFixedThreadPool(mandelbrotSet.getMaxThreads());
        for (int i = 0; i < mandelbrotSet.getMaxThreads(); i++) {
            executorService.submit(new MandelbrotWorker(mandelbrotSet, chunks, i));
        }

        shutdownAndAwaitTermination(executorService);
        mandelbrotSet.generate();

        System.out.println("Total time elapsed: " + (System.currentTimeMillis() - start));
    }

    private static void shutdownAndAwaitTermination(ExecutorService executorService) {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(120, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(120, TimeUnit.SECONDS)) {
                    System.err.println("ExecutorService did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private static MandelbrotSet initMandelbrotSet(final String[] args) throws ParseException {
        final Options options = new Options();
        options.addOption("s", "size", true, "Sets the width and height of the output image");
        options.addOption("r", "rect", true, "Sets the part of the plane for which the fractal will be created");
        options.addOption("t", "tasks", true, "Sets the number of threads which will be used");
        options.addOption("o", "output", true, "Sets the output name of the generated image");
        options.addOption("g", "gran", true, "Sets the granularity of image partitioning");
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
