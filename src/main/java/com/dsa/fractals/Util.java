package com.dsa.fractals;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Contains useful utility methods.
 */
public class Util {

    /**
     * Exports {@link BufferedImage} object with the specified filename.
     *
     * @param image    the {@link BufferedImage} object
     * @param filename the filename of the image
     * @throws Exception if there was an error while exporting the image
     */
    public static void exportImage(final BufferedImage image, final String filename) throws Exception {
        ImageIO.write(image, "png", new File(filename));
    }
}
