package com.nnv.application.ultil;

import org.jcodec.api.JCodecException;
import org.jcodec.api.awt.AWTFrameGrab;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class VideoThumbTaker {
    public static boolean generatePreviewImage(String filePath, String previewFileName) throws IOException, JCodecException {
        boolean isPreviewGenerated = false;
        try {
            double sec = 1;
            BufferedImage dst = AWTFrameGrab.getFrame(new File(filePath), sec);
            ImageIO.write(dst, "png", new File(previewFileName));
            isPreviewGenerated = true;
            //where filePath is the path of video file and previeFileName is the name of preview image file.
        } catch (Exception e) {
            System.out.println("Exception while creating video thumbnail : " + previewFileName + " - exception - " + e);
//            e.printStackTrace();
        }
        System.out.println("Image written successfully? " + previewFileName);
        return isPreviewGenerated;
    }
}
