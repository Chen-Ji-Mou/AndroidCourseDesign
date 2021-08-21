package com.chenjimou.androidcoursedesign.ui.fragment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.chenjimou.androidcoursedesign.ui.LazyLoadFragment;
import com.chenjimou.androidcoursedesign.databinding.FragmentShareBinding;

import androidx.viewbinding.ViewBinding;

public class ShareFragment extends LazyLoadFragment
{
    @Override
    protected ViewBinding createViewBinding(LayoutInflater inflater, ViewGroup container)
    {
        return FragmentShareBinding.inflate(inflater, container, false);
    }

    @Override
    protected void init(ViewBinding viewBinding)
    {

    }
}
