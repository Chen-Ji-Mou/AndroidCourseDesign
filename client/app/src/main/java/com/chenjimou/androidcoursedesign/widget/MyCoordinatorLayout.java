package com.chenjimou.androidcoursedesign.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.chenjimou.androidcoursedesign.utils.DisplayUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class MyCoordinatorLayout extends CoordinatorLayout
{
    OnTouchListener listener;
    final int screenHeight;
    // 评论输入框和布局间的分割线，超过该分割线视为点击评论输入框
    static final float SPLIT_LINE = 0.882918305f;

    public MyCoordinatorLayout(
            @NonNull
                    Context context)
    {
        this(context, null);
    }

    public MyCoordinatorLayout(
            @NonNull
                    Context context,
            @Nullable
                    AttributeSet attrs)
    {
        super(context, attrs);
        screenHeight = DisplayUtils.getScreenHeight(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if (ev.getAction() == MotionEvent.ACTION_DOWN && listener != null)
        {
            listener.onTouch((ev.getY() / screenHeight) >= SPLIT_LINE);
        }
        return super.onInterceptTouchEvent(ev);
    }

    public interface OnTouchListener
    {
        void onTouch(boolean isInput);
    }

    public void setOnTouchListener(OnTouchListener listener)
    {
        this.listener = listener;
    }
}
