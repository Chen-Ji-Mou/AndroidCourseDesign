package com.chenjimou.androidcoursedesign.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.chenjimou.androidcoursedesign.R;
import com.chenjimou.androidcoursedesign.databinding.ActivityMainBinding;
import com.chenjimou.androidcoursedesign.ui.fragment.HomeFragment;
import com.chenjimou.androidcoursedesign.ui.fragment.PersonalFragment;
import com.chenjimou.androidcoursedesign.utils.SystemBarUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener
{
    ActivityMainBinding mBinding;
    HomeFragment mHomeFragment;

    List<Fragment> fragments = new ArrayList<>();

    static final int ACTION_SHARE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        init();
    }

    void init()
    {
        SystemBarUtil.setStatusBarColor(this, R.color.colorPrimary);
        SystemBarUtil.setAndroidNativeLightStatusBar(this, false);

        mHomeFragment = new HomeFragment();

        fragments.add(mHomeFragment);
        fragments.add(new PersonalFragment());

        mBinding.viewpager.setUserInputEnabled(false);
        mBinding.viewpager.setAdapter(new MainPagerAdapter(this));

        mBinding.bottomNavigationView.enableAnimation(false);
        mBinding.bottomNavigationView.enableShiftingMode(false);
        mBinding.bottomNavigationView.enableItemShiftingMode(false);
        mBinding.bottomNavigationView.setTextVisibility(false);
        mBinding.bottomNavigationView.setOnNavigationItemSelectedListener(this);

        mBinding.btnShare.setOnClickListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(
            @NonNull
                    MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.item_home:
                mBinding.viewpager.setCurrentItem(0);
                break;
            case R.id.item_share:
                return false;
            case R.id.item_personal:
                mBinding.viewpager.setCurrentItem(1);
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_share:
                startActivityForResult(new Intent(MainActivity.this, ShareEditActivity.class), ACTION_SHARE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            @Nullable
                    Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            mHomeFragment.onLoadLatest();
        }
    }

    class MainPagerAdapter extends FragmentStateAdapter
    {
        public MainPagerAdapter(
                @NonNull
                        FragmentActivity fragmentActivity)
        {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position)
        {
            return fragments.get(position);
        }

        @Override
        public int getItemCount()
        {
            return fragments.size();
        }
    }
}