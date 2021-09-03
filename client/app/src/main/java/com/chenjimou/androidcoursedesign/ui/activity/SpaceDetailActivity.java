package com.chenjimou.androidcoursedesign.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.chenjimou.androidcoursedesign.R;
import com.chenjimou.androidcoursedesign.databinding.ActivitySpaceDetailBinding;
import com.chenjimou.androidcoursedesign.databinding.LayoutSpaceDetailItemBinding;
import com.chenjimou.androidcoursedesign.utils.DisplayUtils;

import java.util.List;

public class SpaceDetailActivity extends AppCompatActivity implements View.OnClickListener
{
    ActivitySpaceDetailBinding mBinding;

    List<String> pictures;
    String content;
    long date;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        mBinding = ActivitySpaceDetailBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        init();
    }

    void init()
    {
        int statusBarSize = DisplayUtils.getStatusBarSize(this);
        if (statusBarSize != -1)
        {
            mBinding.toolbar.setPadding(0, statusBarSize, 0, 0);
        }

        mBinding.btnBack.setOnClickListener(this);

        mBinding.rvComment.setVisibility(View.GONE);

        mBinding.rvComment.setLayoutManager(new LinearLayoutManager(this));

        if (getIntent() != null && getIntent().getExtras() != null)
        {
            pictures = getIntent().getExtras().getStringArrayList("pictures");
            content = getIntent().getExtras().getString("content");
            date = getIntent().getExtras().getLong("date");
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (pictures != null)
        {
            mBinding.banner.notifyDataSetChanged(pictures);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_back:
                finish();
                break;
        }
    }

    class SpaceDetailAdapter extends RecyclerView.Adapter<SpaceDetailAdapter.ViewHolder>
    {
        LayoutSpaceDetailItemBinding itemBinding;

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(
                @NonNull
                        ViewGroup parent, int viewType)
        {
            itemBinding = LayoutSpaceDetailItemBinding.inflate(getLayoutInflater());
            return new ViewHolder(itemBinding.getRoot());
        }

        @Override
        public void onBindViewHolder(
                @NonNull
                        ViewHolder holder, int position)
        {

        }

        @Override
        public int getItemCount()
        {
            return pictures.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            ImageView iv_user_icon;
            TextView tv_user_name;
            TextView tv_comment_content;

            public ViewHolder(
                    @NonNull
                            View itemView)
            {
                super(itemView);
                iv_user_icon = itemBinding.ivUserIcon;
                tv_user_name = itemBinding.tvUserName;
                tv_comment_content = itemBinding.tvCommentContent;
            }
        }
    }
}