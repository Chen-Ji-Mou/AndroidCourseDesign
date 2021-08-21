package com.chenjimou.androidcoursedesign.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

/**
 * 懒加载Fragment
 */
public abstract class LazyLoadFragment extends Fragment
{
    // 是否是第一次加载
    boolean isFirstLoad = true;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull
                    LayoutInflater inflater,
            @Nullable
                    ViewGroup container,
            @Nullable
                    Bundle savedInstanceState)
    {
        ViewBinding viewBinding = createViewBinding(inflater, container);
        init(viewBinding);
        return viewBinding.getRoot();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (isFirstLoad)
        {
            initDataFirst();
            isFirstLoad = false;
        }
    }

    /**
     * 设置布局文件id
     */
    protected abstract ViewBinding createViewBinding(LayoutInflater inflater, ViewGroup container);

    /**
     * 初始化监听（空实现）
     */
    protected void init(ViewBinding viewBinding){ }

    /**
     * 第一次数据初始化（空实现）
     */
    protected void initDataFirst(){ }
}
