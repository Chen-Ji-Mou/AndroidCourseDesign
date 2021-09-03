package com.chenjimou.androidcoursedesign.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.chenjimou.androidcoursedesign.base.BaseApplication;
import com.chenjimou.androidcoursedesign.utils.DisplayUtils;
import com.chenjimou.androidcoursedesign.utils.FlingUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

public class MyNestedScrollView extends NestedScrollView
{
    private ViewGroup mHeadView;
    private ViewGroup mBottomView;
    private onNestedScrollListener mListener;

    // 工具类，用于转换速度和距离
    private FlingUtils mFlingUtils;
    // 标志 MyNestedScrollView 是否发生了惯性滑动
    private boolean isFling = false;
    // MyNestedScrollView 进行惯性滑动的总距离
    private int flingY = 0;
    // 当前惯性滑动的Y轴速度
    private int velocityY = 0;
    // actionBar的高度
    private float actionBarSize;

    private static final int[] attrs = new int[] {
            android.R.attr.actionBarSize
    };

    public MyNestedScrollView(@NonNull Context context)
    {
        this(context, null);
    }

    public MyNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    private void init(Context context)
    {
        mFlingUtils = new FlingUtils(context);

        TypedArray typedArray = context.obtainStyledAttributes(attrs);
        actionBarSize = typedArray.getDimension(0, 0);
        typedArray.recycle();

        setOnScrollChangeListener(new View.OnScrollChangeListener()
        {
            /**
             * 当 MyNestedScrollView 的滚动位置更改时回调
             * @param scrollY 当前触发回调时 MyNestedScrollView 滑动的总距离
             * @param oldScrollY 上一次触发回调时 MyNestedScrollView 滑动的总距离
             */
            @Override
            public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
            {
                if (isFling)
                {
                    // 当发生 fling 的时候，清空 flingY 之前的数据（因为之前的数据不是惯性滑动产生的）
                    flingY = 0;
                    isFling = false;
                }
                // 当 MyNestedScrollView 已经滑到 headView 底部时，让 contentView 中的 RecyclerView 滑动
                if (scrollY == mHeadView.getMeasuredHeight())
                {
                    dispatchRecyclerViewFling();
                }
                // 每次触发回调时 MyNestedScrollView 滑动的距离 = scrollY - oldScrollY
                // 开始记录 MyNestedScrollView 进行惯性滑动的总距离
                flingY += scrollY - oldScrollY;
            }
        });
    }

    private void dispatchRecyclerViewFling()
    {
        if (velocityY != 0)
        {
            // 将速度换算成距离
            double flingDistance = mFlingUtils.getSplineFlingDistance(velocityY);
            // 如果根据速度换算出的距离大于 MyNestedScrollView 根据惯性已经滑动的距离
            // 这时候就需要让 contentView 中的 RecyclerView 继续根据惯性滑动
            if (flingDistance > flingY)
            {
                // 因为根据速度换算出的距离有一部分已经让 MyNestedScrollView 消耗了，所以需要减去 flingY
                // 因为 fling 方法只支持速度，所以需要将减去 flingY 后的距离再换算成速度
                recyclerViewFling(mFlingUtils.getVelocityByDistance(flingDistance - (double) flingY));
            }
        }
        // 重置
        flingY = 0;
        velocityY = 0;
    }

    /**
     * 回调执行 contentView 中 RecyclerView 的惯性滑动
     */
    private void recyclerViewFling(int velocityY)
    {
        // 寻找当前显示的 RecyclerView
        RecyclerView childRecyclerView = findChildRecyclerView(mBottomView);
        if (childRecyclerView != null)
        {
            childRecyclerView.fling(0, velocityY);
        }
    }

    /**
     * 寻找 contentView 中当前显示的 RecyclerView
     */
    private RecyclerView findChildRecyclerView(ViewGroup viewGroup)
    {
        for (int i = 0; i < viewGroup.getChildCount(); i++)
        {
            if (viewGroup.getChildAt(i) instanceof RecyclerView)
            {
                return (RecyclerView) viewGroup.getChildAt(i);
            }
        }
        return null;
    }

    /**
     * 当手指离开屏幕后还有速度，触发惯性滑动的回调
     */
    @Override
    public void fling(int velocityY)
    {
        super.fling(velocityY);
        if (velocityY <= 0)
        {
            this.velocityY = 0;
        }
        else
        {
            isFling = true;
            this.velocityY = velocityY;
        }
    }

    /**
     * 当布局加载完成之后的回调
     */
    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        mHeadView = (ViewGroup) ((ViewGroup) getChildAt(0)).getChildAt(0);
        mBottomView = (ViewGroup) ((ViewGroup) getChildAt(0)).getChildAt(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ViewGroup.LayoutParams layoutParams = mBottomView.getLayoutParams();
        // 修改contentView的高度为父容器的高度，实现吸顶效果
        layoutParams.height = (int) (getMeasuredHeight() - actionBarSize - DisplayUtils.dip2px(BaseApplication.sApplication, 24));
        mBottomView.setLayoutParams(layoutParams);
    }

    /**
     * 当发生嵌套滑动时，修改了 contentView 中的 RecyclerView 先滑动的现象
     * @param dy 触发回调时滑动的距离（正值代表向下滑动，负值代表向上滑动）
     * @param consumed 此 NestedScrollingParent 消耗的水平和垂直滚动距离
     *                 （ NestedScrollingChild 通过该值得知 NestedScrollingParent 滑动了多少）
     */
    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type)
    {
        // 这里我们认为 MyNestedScrollView 就已经是最外层的 NestedScrollingParent，因此拦截嵌套滑动事件，不再传给外层 parent
        // getScrollY() 返回的是 MyNestedScrollView 滑动的总距离
        // 若当前 headView 仍可见，则需要先将 headView 滑动至不可见后才让 contentView 中的 RecyclerView 滑动
        boolean hideTop = dy > 0 && getScrollY() < mHeadView.getMeasuredHeight();

        if (hideTop)
        {
            scrollBy(0, dy);
            consumed[1] = dy;
        }

        if (getScrollY() > mHeadView.getMeasuredHeight())
        {
            if (mListener != null)
                mListener.onBottomViewScroll();
        }
        else
        {
            if (mListener != null)
                mListener.onHeadViewScroll();
        }
    }

    public interface onNestedScrollListener
    {
        void onHeadViewScroll();
        void onBottomViewScroll();
    }

    public void setOnNestedScrollListener(onNestedScrollListener listener)
    {
        mListener = listener;
    }
}
