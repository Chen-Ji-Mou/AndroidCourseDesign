package com.chenjimou.androidcoursedesign.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.chenjimou.androidcoursedesign.R;
import com.chenjimou.androidcoursedesign.utils.DisplayUtils;
import com.chenjimou.androidcoursedesign.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class Banner<T> extends FrameLayout implements ViewPager.OnPageChangeListener
{
    private final Context mContext;
    // banner轮播的数据集合
    private List<T> bannerDataList;
    // 记录所有指示器中的"点"
    private List<View> indicators;
    // 记录指示器当前"点"的位置
    private int indicatorPosition = 0;

    private MyViewPager viewPager;
    private LinearLayout indicator;
    private Handler mHandler;
    private AutoRollTimer mAutoRollTimer;
    private OnBannerClickListener mOnClickListener;
    private BannerAdapter bannerAdapter;

    public Banner(Context context)
    {
        this(context, null);
    }

    public Banner(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public Banner(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        mContext = context;
        // 初始化
        init();
    }

    /**
     * 进行初始化
     */
    private void init()
    {
        // 初始化布局
        LayoutInflater.from(mContext).inflate(R.layout.layout_banner, this);
        viewPager = findViewById(R.id.banner_viewpager);
        indicator = findViewById(R.id.banner_indicator);

        // 定死ViewPager的高度是屏幕的1/4
        ViewGroup.LayoutParams layoutParams = viewPager.getLayoutParams();
        layoutParams.height = (int) (DisplayUtils.getScreenHeight((Activity) mContext) * 0.5);
        viewPager.setLayoutParams(layoutParams);

        // 初始化数据
        bannerDataList = new ArrayList<>();
        indicators = new ArrayList<>();
        mAutoRollTimer = new AutoRollTimer();
        mHandler = new Handler();
        bannerAdapter = new BannerAdapter();

        // 设置数据适配器
        viewPager.setAdapter(bannerAdapter);

        // 设置监听
        viewPager.addOnPageChangeListener(this);
    }

    /**
     * 更新 banner 轮播的数据
     *
     * @param data banner数据
     */
    public void notifyDataSetChanged(List<T> data)
    {
        if (null != data && !data.isEmpty())
        {
            this.bannerDataList.clear();
            this.bannerDataList.addAll(data);
        }
        viewPager.setScrollEnable(bannerDataList.size() > 1);
        // 添加指示点
        if (this.bannerDataList.size() > 1)
        {
            // 重置
            indicators.clear();
            indicator.removeAllViews();

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            for (int i = 0; i < this.bannerDataList.size(); i++)
            {
                ImageView imageView = new ImageView(mContext);
                if (i == 0)
                {
                    imageView.setBackgroundResource(R.drawable.icon_banner_unselected);
                }
                else
                {
                    imageView.setBackgroundResource(R.drawable.icon_banner_selected);
                }
                params.setMargins(0, 0, DisplayUtils.dip2px(mContext, 3), 0);
                imageView.setLayoutParams(params);
                // 添加"点"到指示器中
                indicator.addView(imageView);
                // 记录"点"
                indicators.add(imageView);
            }
        }
        // 更新数据
        bannerAdapter.notifyDataSetChanged();
    }

    /**
     * 设置开始轮播
     */
    public void startRoll()
    {
        mAutoRollTimer.start(mAutoRollTimer.interval);
    }

    /**
     * 设置停止轮播
     */
    public void stopRoll()
    {
        mAutoRollTimer.stop();
    }

    /**
     * 设置轮播间隔
     *
     * @param interval 间隔时长
     */
    public void setRollInterval(long interval)
    {
        mAutoRollTimer.interval = interval;
        startRoll();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {

    }

    @Override
    public void onPageSelected(int position)
    {
        if (bannerDataList.size() == 1)
        {
            return;
        }
        // 更新"点"的图案
        indicators.get(indicatorPosition).setBackgroundResource(R.drawable.icon_banner_selected);
        indicators.get(position % indicators.size()).setBackgroundResource(R.drawable.icon_banner_unselected);
        // 重新刷新 indicatorPosition 索引
        indicatorPosition = position % indicators.size();
    }

    @Override
    public void onPageScrollStateChanged(int state)
    {

    }

    public interface OnBannerClickListener
    {
        void onBannerClick(int position);
    }

    /**
     * 设置点击监听
     */
    public void setOnBannerClickListener(OnBannerClickListener onBannerClickListener)
    {
        mOnClickListener = onBannerClickListener;
    }

    private class BannerAdapter extends PagerAdapter
    {
        // 缓存ImageView实例
        private final List<ImageView> imgCache = new ArrayList<>();

        @Override
        public int getCount()
        {
            // 无限滑动
            return null != bannerDataList && !bannerDataList.isEmpty() ? Integer.MAX_VALUE : 0;
        }

        @Override
        public boolean isViewFromObject(
                @NonNull
                        View view,
                @NonNull
                        Object object)
        {
            return view == object;
        }

        @Override
        public void destroyItem(
                @NonNull
                        ViewGroup container, int position,
                @NonNull
                        Object object)
        {
            if (object instanceof ImageView)
            {
                ImageView imageView = (ImageView)object;
                container.removeView(imageView);
                // 添加缓存
                imgCache.add(imageView);
            }
        }

        @NonNull
        @Override
        public Object instantiateItem(
                @NonNull
                        ViewGroup container, int position)
        {
            ImageView imageView;

            // 获取ImageView对象
            if (imgCache.size() > 0)
            {
                imageView = imgCache.remove(0);
            }
            else
            {
                imageView = new ImageView(mContext);
            }
            // 设置图片缩放模式
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            imageView.setOnTouchListener(new OnTouchListener()
            {
                // 手指触摸点的X坐标
                private int downX = 0;
                // 手指按下的时间
                private long downTime = 0;

                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    switch (event.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
                            mAutoRollTimer.stop();
                            downX = (int)v.getX();
                            downTime = System.currentTimeMillis();
                            break;
                        case MotionEvent.ACTION_UP:
                            mAutoRollTimer.start(mAutoRollTimer.interval);
                            int moveX = (int)v.getX();
                            long moveTime = System.currentTimeMillis();
                            //判断为点击的条件
                            if (downX == moveX && (moveTime - downTime < 500))
                            {
                                //轮播图回调点击事件
                                if (null != mOnClickListener)
                                {
                                    mOnClickListener.onBannerClick(position % bannerDataList.size());
                                }
                            }
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            mAutoRollTimer.start(mAutoRollTimer.interval);
                            break;
                    }
                    return true;
                }
            });

            T data = bannerDataList.get(position % bannerDataList.size());
            if (data instanceof String)
            {
                String pictureId = (String) data;

                GlideUrl url = new GlideUrl(
                        mContext.getString(R.string.request_picture_url) + pictureId,
                        new LazyHeaders.Builder()
                                .addHeader("Authorization", SharedPreferencesUtils.getInstance().getToken())
                                .build());

                Glide.with(mContext).load(url).into(imageView);
            }

            // 添加imageView到布局中
            container.addView(imageView);

            return imageView;
        }
    }

    private class AutoRollTimer implements Runnable
    {
        // 是否在轮播的标志
        boolean isRunning = false;
        // 默认3秒轮播
        long interval = 3000;

        void start(long interval)
        {
            if (!isRunning)
            {
                isRunning = true;
                mHandler.removeCallbacks(this);
                mHandler.postDelayed(this, interval);
            }
        }

        void stop()
        {
            if (isRunning)
            {
                mHandler.removeCallbacks(this);
                isRunning = false;
            }
        }

        @Override
        public void run()
        {
            if (isRunning)
            {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                mHandler.postDelayed(this, interval);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        stopRoll();
    }
}
