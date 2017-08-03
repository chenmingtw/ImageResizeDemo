package com.example.chenming.imageresizedemo;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by ChenMing on 2017/7/24.
 * This is Bicubic Interpolation Resizer.
 * It must sampling 16 neighbor points at original point.
 *
 *     (-1, -1), (0, -1), (1, -1), (2, -1)
 *     (-1,  0), (0,  0}, (1,  0), (2,  0)
 *     (-1,  1), (0,  1), (1,  1), (2,  1)
 *     (-1,  2), (0,  2), (1,  2), (2,  2)
 *
 */

class BicubicInterpolationResizer implements Resizer {
    private final static String TAG = "ImageResizeDemo/BCI";
    private int[][] bicubicPoints = new int[4][4];

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
            int ox, oy;        // position in old image
            double dx, dy;        // delta_x, delta_y
            double tx = (double)oldWidth / newWidth;
            double ty = (double)oldHeight / newHeight;
            double Bmdx, Bdyn;
            int newPixelR = 0;
            int newPixelG = 0;
            int newPixelB = 0;
            int oxm;
            int oyn;
            int oldPixel;
            double oldPixelR;
            double oldPixelG;
            double oldPixelB;

            for (int ny = 0; ny < newHeight; ny++) {
                for (int nx = 0; nx < newWidth; nx++) {
                    newPixelR = 0;
                    newPixelG = 0;
                    newPixelB = 0;

                    ox = (int)(tx * nx);
                    oy = (int)(ty * ny);
                    dx = tx * nx - ox;
                    dy = ty * ny - oy;

                    // Bicubic algorithm
                    for (int m = -1; m <= 2; m++) {
                        Bmdx = BSpline(m - dx);

                        for (int n = -1; n <= 2; n++) {
                            oxm = ox + m;
                            oyn = oy + n;
                            if(oxm >= 0 && oyn >= 0 && oxm < oldWidth && oyn < oldHeight) {
                                oldPixel = bmpIn.getPixel(oxm, oyn);
                                oldPixelR = Color.red(oldPixel);
                                oldPixelG = Color.green(oldPixel);
                                oldPixelB = Color.blue(oldPixel);

                                Bdyn = BSpline(dy - n);
                                newPixelR += (int)(oldPixelR * Bmdx * Bdyn);
                                newPixelG += (int)(oldPixelG * Bmdx * Bdyn);
                                newPixelB += (int)(oldPixelB * Bmdx * Bdyn);
                            }
                        }
                    }

                    bmpOut.setPixel(nx, ny, Color.rgb(newPixelR, newPixelG, newPixelB));
                }
            }
        }

        return bmpOut;
    }

    private double BSpline(double x) {
        if(x < 0.0)
            x = Math.abs(x);

        if(x >= 0.0 && x <= 1.0)
            return (2.0 / 3.0) + 0.5 * Math.pow(x, 3.0) - Math.pow(x, 2.0);
        else if(x > 1.0 && x <= 2.0)
            return 1.0 / 6.0 * Math.pow(2 - x, 3.0);

        return 1.0;
    }
}
