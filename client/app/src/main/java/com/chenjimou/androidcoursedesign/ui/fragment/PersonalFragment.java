package com.chenjimou.androidcoursedesign.ui.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chenjimou.androidcoursedesign.R;
import com.chenjimou.androidcoursedesign.ui.LazyLoadFragment;
import com.chenjimou.androidcoursedesign.databinding.FragmentPersonalBinding;
import com.chenjimou.androidcoursedesign.ui.activity.MySpaceActivity;
import com.chenjimou.androidcoursedesign.utils.SharedPreferencesUtils;

import androidx.viewbinding.ViewBinding;

public class PersonalFragment extends LazyLoadFragment implements View.OnClickListener
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

        mBinding.btnMySpace.setOnClickListener(this);
        mBinding.btnMyComment.setOnClickListener(this);
    }

    @Override
    protected void initDataFirst()
    {
        mBinding.tvUserName.setText(SharedPreferencesUtils.getInstance().getUsername());
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_my_space:
                startActivity(new Intent(getContext(), MySpaceActivity.class));
                break;
            case R.id.btn_my_comment:
                break;
        }
    }
}
