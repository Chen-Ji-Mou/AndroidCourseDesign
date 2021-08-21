package com.chenjimou.androidcoursedesign.ui.fragment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.chenjimou.androidcoursedesign.ui.LazyLoadFragment;
import com.chenjimou.androidcoursedesign.databinding.FragmentPersonalBinding;

import androidx.viewbinding.ViewBinding;

public class PersonalFragment extends LazyLoadFragment
{
    @Override
    protected ViewBinding createViewBinding(LayoutInflater inflater, ViewGroup container)
    {
        return FragmentPersonalBinding.inflate(inflater, container, false);
    }

    @Override
    protected void init(ViewBinding viewBinding)
    {

    }
}
