package com.chenjimou.androidcoursedesign.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.chenjimou.androidcoursedesign.R;
import com.chenjimou.androidcoursedesign.databinding.ActivityMySpaceBinding;

public class MySpaceActivity extends AppCompatActivity
{
    ActivityMySpaceBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMySpaceBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
    }
}