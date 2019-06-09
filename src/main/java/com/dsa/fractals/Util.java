package com.dsa.fractals;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Util {

    public static void exportImage(final BufferedImage image, final String imageName) throws Exception {
        ImageIO.write(image, "png", new File(imageName));
    }
}
