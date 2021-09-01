package com.chenjimou.androidcoursedesign.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.chenjimou.androidcoursedesign.R;
import com.chenjimou.androidcoursedesign.databinding.ActivitySpaceDetailBinding;

public class SpaceDetailActivity extends AppCompatActivity
{
    ActivitySpaceDetailBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySpaceDetailBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
    }
}