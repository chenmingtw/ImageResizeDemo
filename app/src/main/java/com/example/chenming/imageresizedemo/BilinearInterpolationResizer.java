package com.example.chenming.imageresizedemo;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by ChenMing on 2017/7/24.
 * This is Bilinear Interpolation Resizer.
 * It must sampling 4 neighbor points at original point.
 *
 *     (0, 0}, (1, 0)
 *     (0, 1), (1, 1)
 *
 */

class BilinearInterpolationResizer implements Resizer {
    private final static String TAG = "ImageResizeDemo/BLI";

    @Override
    public Bitmap doScale(Bitmap bmpIn, int scalePercent) {
        int oldWidth = bmpIn.getWidth();
        int oldHeight = bmpIn.getHeight();
        int newWidth = oldWidth * scalePercent / 100;
        int newHeight = oldHeight * scalePercent / 100;
        Bitmap bmpOut = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        if(scalePercent < 100) {
            // Do shrink
            for (int y = 0; y < newHeight; y++) {
                for (int x = 0; x < newWidth; x++) {
                    bmpOut.setPixel(x, y, bmpIn.getPixel(x * 100 / scalePercent, y * 100 / scalePercent));
                }
            }
        } else if(scalePercent == 100) {
            // Return original image
            bmpOut = bmpIn;
        } else {
            // Do enlarge
            double scale = Math.ceil((double)scalePercent / 100);
            for (int y = 0; y < oldHeight - 1; y++) {
                for (int x = 0; x < oldWidth - 1; x++) {
                    int oldPixel = bmpIn.getPixel(x, y);
                    int newPointX = x * scalePercent / 100;
                    int newPointY = y * scalePercent / 100;

                    // Bilinear algorithm
                    for (int fy = 0; fy < scale; fy++) {
                        for (int fx = 0; fx < scale; fx++) {
                            int nx = newPointX + fx;
                            int ny = newPointY + fy;

                            // need to be optimized yet...
                            if(nx < newWidth && ny < newHeight && x+1 < oldWidth && y+1 < oldHeight) {
                                double gx0 = (scale - fx) / scale;
                                double gx1 = fx / scale;
                                double gy0 = (scale - fy) / scale;
                                double gy1 = fy / scale;

                                int x00 = bmpIn.getPixel(x, y);
                                int x00r = Color.red(x00);
                                int x00g = Color.green(x00);
                                int x00b = Color.blue(x00);

                                int x10 = bmpIn.getPixel(x+1, y);
                                int x10r = Color.red(x10);
                                int x10g = Color.green(x10);
                                int x10b = Color.blue(x10);

                                int x01 = bmpIn.getPixel(x, y+1);
                                int x01r = Color.red(x01);
                                int x01g = Color.green(x01);
                                int x01b = Color.blue(x01);

                                int x11 = bmpIn.getPixel(x+1, y+1);
                                int x11r = Color.red(x11);
                                int x11g = Color.green(x11);
                                int x11b = Color.blue(x11);

                                double valueX0r = x00r * gx0 + x10r * gx1;
                                double valueX0g = x00g * gx0 + x10g * gx1;
                                double valueX0b = x00b * gx0 + x10b * gx1;
                                double valueX1r = x01r * gx0 + x11r * gx1;
                                double valueX1g = x01g * gx0 + x11g * gx1;
                                double valueX1b = x01b * gx0 + x11b * gx1;

                                int bilinearPixelR = (int) (valueX0r * gy0 + valueX1r * gy1);
                                int bilinearPixelG = (int) (valueX0g * gy0 + valueX1g * gy1);
                                int bilinearPixelB = (int) (valueX0b * gy0 + valueX1b * gy1);
                                int bilinearPixel = Color.rgb(bilinearPixelR, bilinearPixelG, bilinearPixelB);
                                bmpOut.setPixel(nx, ny, bilinearPixel);
                            }
                        }
                    }
                }
            }
        }

        return bmpOut;
    }
}
