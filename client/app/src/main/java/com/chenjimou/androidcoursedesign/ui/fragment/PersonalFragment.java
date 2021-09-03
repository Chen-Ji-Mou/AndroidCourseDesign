package com.chenjimou.androidcoursedesign.ui.fragment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.chenjimou.androidcoursedesign.ui.LazyLoadFragment;
import com.chenjimou.androidcoursedesign.databinding.FragmentPersonalBinding;
import com.chenjimou.androidcoursedesign.utils.SharedPreferencesUtils;

import androidx.viewbinding.ViewBinding;

public class PersonalFragment extends LazyLoadFragment
{
    FragmentPersonalBinding mBinding;

    private static final String TAG = "PersonalFragment";

    @Override
    protected ViewBinding createViewBinding(LayoutInflater inflater, ViewGroup container)
    {
        return FragmentPersonalBinding.inflate(inflater, container, false);
    }

    @Override
    protected void init(ViewBinding viewBinding)
    {
        mBinding = (FragmentPersonalBinding) viewBinding;
    }

    @Override
    protected void initDataFirst()
    {
        mBinding.tvUserName.setText(SharedPreferencesUtils.getInstance().getUsername());
    }
}
