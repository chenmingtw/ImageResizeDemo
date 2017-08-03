package com.example.chenming.imageresizedemo;

import android.graphics.Bitmap;

/**
 * Created by ChenMing on 2017/7/21.
 * This is Nearest Neighbor Interpolation Resizer.
 */

class NNInterpolationResizer implements Resizer {
    private final static String TAG = "ImageResizeDemo/NNI";

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
            /*
            // The complexity O(N*N)
            for (int y = 0; y < newHeight; y++) {
                for (int x = 0; x < newWidth; x++) {
                    bmpOut.setPixel(x, y, bmpIn.getPixel((int)(x / scalePercent), (int)(y / scalePercent)));
                }
            }
            */

            // The complexity O(N*N*n*n), but N >> n, so the complexity is close to O(N*N)
            double scale = Math.ceil((double) scalePercent / 100);
            for (int y = 0; y < oldHeight; y++) {
                for (int x = 0; x < oldWidth; x++) {
                    int oldPixel = bmpIn.getPixel(x, y);
                    int newPointX = x * scalePercent / 100;
                    int newPointY = y * scalePercent / 100;

                    // Nearest Neighbor algorithm
                    for (int fy = 0; fy < scale; fy++) {
                        for (int fx = 0; fx < scale; fx++) {
                            int nx = newPointX + fx;
                            int ny = newPointY + fy;
                            if(nx < newWidth && ny < newHeight)
                                bmpOut.setPixel(nx, ny, oldPixel);
                        }
                    }
                }
            }
        }

        return bmpOut;
    }
}
