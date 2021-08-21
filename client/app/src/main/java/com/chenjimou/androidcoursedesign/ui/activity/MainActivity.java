package com.chenjimou.androidcoursedesign.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;

import com.chenjimou.androidcoursedesign.R;
import com.chenjimou.androidcoursedesign.databinding.ActivityMainBinding;
import com.chenjimou.androidcoursedesign.ui.fragment.HomeFragment;
import com.chenjimou.androidcoursedesign.ui.fragment.PersonalFragment;
import com.chenjimou.androidcoursedesign.ui.fragment.ShareFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    ActivityMainBinding mBinding;

    List<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        init();
    }

    void init()
    {
        setSupportActionBar(mBinding.toolbar);

        fragments.add(new HomeFragment());
        fragments.add(new ShareFragment());
        fragments.add(new PersonalFragment());

        mBinding.viewpager.setOffscreenPageLimit(fragments.size());
        mBinding.viewpager.setAdapter(new MainPagerAdapter(this));
        mBinding.viewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback()
        {
            @Override
            public void onPageSelected(int position)
            {
                MenuItem item = mBinding.bottomNavigationView.getMenu().getItem(position);
                item.setChecked(true);
            }
        });

        mBinding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
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
                        mBinding.viewpager.setCurrentItem(1);
                        break;
                    case R.id.item_personal:
                        mBinding.viewpager.setCurrentItem(3);
                        break;
                }
                return true;
            }
        });
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