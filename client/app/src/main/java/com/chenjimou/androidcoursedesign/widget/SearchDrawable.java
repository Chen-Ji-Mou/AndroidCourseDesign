package com.chenjimou.androidcoursedesign.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import com.chenjimou.androidcoursedesign.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SearchDrawable extends Drawable
{
    Paint mPaint;
    Bitmap mView;

    public SearchDrawable(Context context)
    {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setARGB(110, 244, 92, 71);

        mView = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_search);
    }

    @Override
    public void draw(
            @NonNull
                    Canvas canvas)
    {
        canvas.drawBitmap(mView, 0, 0, mPaint);
        canvas.drawText("搜索", mView.getWidth() + 8,
                mView.getHeight() / 2 - (mPaint.descent()/2 + mPaint.ascent()/2), mPaint);
    }

    @Override
    public void setAlpha(int alpha)
    {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(
            @Nullable
                    ColorFilter colorFilter)
    {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity()
    {
        return PixelFormat.TRANSLUCENT;
    }
}
