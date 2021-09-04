package com.chenjimou.androidcoursedesign.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * 继承自 androidx.viewpager.widget.ViewPager，定制功能，例如：禁启用页面滑动
 */
public class MyViewPager extends ViewPager
{
    private static final String TAG = "MyViewPager";
    /** 是否允许滑动 */
    private boolean isEnableScroll = true;

    public MyViewPager(Context context)
    {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0)
    {
        if (isEnableScroll)
        {
            return super.onTouchEvent(arg0);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0)
    {
        if (isEnableScroll)
        {
            try
            {
                return super.onInterceptTouchEvent(arg0);
            }
            catch (IllegalArgumentException e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 设置滑动能力
     * @param enable true：允许滑动，false：禁止滑动
     */
    public void setScrollEnable(boolean enable)
    {
        isEnableScroll = enable;
    }
}
