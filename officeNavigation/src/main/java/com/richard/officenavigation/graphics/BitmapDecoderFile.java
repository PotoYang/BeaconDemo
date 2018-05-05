package com.richard.officenavigation.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.qozix.tileview.graphics.BitmapDecoder;

public class BitmapDecoderFile implements BitmapDecoder {

    private static final BitmapFactory.Options OPTIONS = new BitmapFactory.Options();

    static {
        OPTIONS.inPreferredConfig = Bitmap.Config.RGB_565;
    }

    @Override
    public Bitmap decode(String fileName, Context context) {
        return BitmapFactory.decodeFile(fileName, OPTIONS);
    }
}
