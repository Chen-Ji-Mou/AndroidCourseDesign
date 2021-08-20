package com.chenjimou.androidcoursedesign.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chenjimou.androidcoursedesign.LazyLoadFragment;
import com.chenjimou.androidcoursedesign.R;
import com.chenjimou.androidcoursedesign.databinding.FragmentHomeBinding;

import androidx.viewbinding.ViewBinding;

public class HomeFragment extends LazyLoadFragment
{
    @Override
    protected ViewBinding createViewBinding(LayoutInflater inflater, ViewGroup container)
    {
        return FragmentHomeBinding.inflate(inflater, container, false);
    }

    @Override
    protected void init(ViewBinding binding)
    {

    }
}
