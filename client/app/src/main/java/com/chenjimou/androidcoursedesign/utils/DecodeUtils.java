package com.chenjimou.androidcoursedesign.utils;

import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;

public class DecodeUtils
{
    private static final BitmapFactory.Options options = new BitmapFactory.Options();

    public static byte[] decodeByBase64(String source)
    {
        return Base64.decode(source, Base64.DEFAULT);
    }

    public static int[] getSourceDimensions(byte[] source)
    {
        resetOptions();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(source, 0, source.length, options);
        options.inJustDecodeBounds = false;
        return new int[] {options.outWidth, options.outHeight};
    }

    private static void resetOptions()
    {
        options.inTempStorage = null;
        options.inDither = false;
        options.inScaled = false;
        options.inSampleSize = 1;
        options.inPreferredConfig = null;
        options.inJustDecodeBounds = false;
        options.inDensity = 0;
        options.inTargetDensity = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            options.inPreferredColorSpace = null;
            options.outColorSpace = null;
            options.outConfig = null;
        }
        options.outWidth = 0;
        options.outHeight = 0;
        options.outMimeType = null;
        options.inBitmap = null;
        options.inMutable = true;
    }
}
