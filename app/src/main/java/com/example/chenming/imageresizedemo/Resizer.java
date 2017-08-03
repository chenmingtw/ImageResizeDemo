package com.example.chenming.imageresizedemo;

import android.graphics.Bitmap;

/**
 * Created by ChenMing on 2017/7/21.
 */

interface Resizer {
    Bitmap doScale(Bitmap bmpIn, int scalePercent);
}
