package com.chenjimou.androidcoursedesign.ui.fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.chenjimou.androidcoursedesign.ui.LazyLoadFragment;
import com.chenjimou.androidcoursedesign.databinding.FragmentPersonalBinding;

import androidx.viewbinding.ViewBinding;

public class PersonalFragment extends LazyLoadFragment
{
    private static final String TAG = "PersonalFragment";

    @Override
    protected ViewBinding createViewBinding(LayoutInflater inflater, ViewGroup container)
    {
        return FragmentPersonalBinding.inflate(inflater, container, false);
    }

    @Override
    protected void init(ViewBinding viewBinding)
    {

    }

    @Override
    protected void initDataFirst()
    {
        Log.d(TAG, "initDataFirst: ");
    }
}
