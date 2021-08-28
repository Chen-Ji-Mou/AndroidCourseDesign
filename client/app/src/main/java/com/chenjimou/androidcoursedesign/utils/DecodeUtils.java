package com.chenjimou.androidcoursedesign.utils;

import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class DecodeUtils
{
    private static final BitmapFactory.Options options = new BitmapFactory.Options();

    public static String encodeByBase64(String path)
    {
        String result = null;
        try
        {
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(new File(path)));
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
            byte[] bytes = new byte[1024];
            int i;
            while ((i = inputStream.read(bytes)) != -1)
            {
                bos.write(bytes, 0, i);
            }
            inputStream.close();
            byte[] data = bos.toByteArray();
            bos.close();
            result = Base64.encodeToString(data, 0, bytes.length, Base64.DEFAULT);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }

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
