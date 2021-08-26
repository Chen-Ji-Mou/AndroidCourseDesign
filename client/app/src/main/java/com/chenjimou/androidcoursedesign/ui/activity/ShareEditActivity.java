package com.chenjimou.androidcoursedesign.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chenjimou.androidcoursedesign.R;
import com.chenjimou.androidcoursedesign.databinding.ActivityShareEditBinding;
import com.chenjimou.androidcoursedesign.databinding.LayoutAddPictureBinding;
import com.chenjimou.androidcoursedesign.databinding.LayoutShareEditItemBinding;
import com.chenjimou.androidcoursedesign.model.PictureFromDeviceModel;
import com.chenjimou.androidcoursedesign.ui.ShareEditItemDecoration;
import com.chenjimou.androidcoursedesign.utils.DisplayUtils;
import com.chenjimou.androidcoursedesign.utils.SystemBarUtil;

import java.util.ArrayList;
import java.util.List;

public class ShareEditActivity extends AppCompatActivity
{
    ActivityShareEditBinding mBinding;
    ShareEditAdapter mAdapter;
    InputMethodManager mManager;

    final List<PictureFromDeviceModel> dataOnUI = new ArrayList<>();

    static final int ACTION_SELECT_IMAGE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mBinding = ActivityShareEditBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        init();
    }

    void init()
    {
        SystemBarUtil.setStatusBarColor(this, R.color.white);
        SystemBarUtil.setAndroidNativeLightStatusBar(this, true);

        mManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        mBinding.toolbar.setTitle("");
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBinding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3,
                StaggeredGridLayoutManager.VERTICAL));
        mBinding.recyclerView.addItemDecoration(new ShareEditItemDecoration());

        mAdapter = new ShareEditAdapter();
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        hideSoftKeyboard();
    }

    void hideSoftKeyboard()
    {
        mBinding.etContent.clearFocus();
        if (mManager.isActive() && getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null)
        {
            mManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull
            MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            @Nullable
                    Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }

    class ShareEditAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        static final int TYPE_ADD = 0;
        static final int TYPE_PICTURE = 1;

        LayoutAddPictureBinding addPictureBinding;
        LayoutShareEditItemBinding shareEditItemBinding;

        @Override
        public int getItemViewType(int position)
        {
            if (position == dataOnUI.size())
                return TYPE_ADD;
            else
                return TYPE_PICTURE;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(
                @NonNull
                        ViewGroup parent, int viewType)
        {
            RecyclerView.ViewHolder viewHolder;
            if (viewType == TYPE_ADD)
            {
                addPictureBinding = LayoutAddPictureBinding.inflate(getLayoutInflater(), parent, false);
                viewHolder = new AddPictureViewHolder(addPictureBinding.getRoot());
            }
            else
            {
                shareEditItemBinding = LayoutShareEditItemBinding.inflate(getLayoutInflater(), parent, false);
                viewHolder = new PictureViewHolder(shareEditItemBinding.getRoot());
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(
                @NonNull
                        RecyclerView.ViewHolder holder, int position)
        {
            int screenWidth = DisplayUtils.getScreenWidth(ShareEditActivity.this);
            int divider = DisplayUtils.dip2px(ShareEditActivity.this,8);

            StaggeredGridLayoutManager.LayoutParams layoutParams =
                    (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.height = (screenWidth - divider * 4) / 3;
            layoutParams.width = (screenWidth - divider * 4) / 3;
            holder.itemView.setLayoutParams(layoutParams);

            if (getItemViewType(position) == TYPE_PICTURE)
            {
                PictureViewHolder viewHolder = (PictureViewHolder)holder;
                Glide.with(ShareEditActivity.this)
                        .load(dataOnUI.get(position).getPath())
                        .into(viewHolder.iv_picture);
            }
            else
            {
                AddPictureViewHolder viewHolder = (AddPictureViewHolder)holder;
                Glide.with(ShareEditActivity.this)
                        .load(R.drawable.icon_add_picture)
                        .into(viewHolder.iv_add_picture);
            }
        }

        @Override
        public int getItemCount()
        {
            return dataOnUI.size() + 1;
        }

        class AddPictureViewHolder extends RecyclerView.ViewHolder
        {
            ImageView iv_add_picture;

            public AddPictureViewHolder(
                    @NonNull
                            View itemView)
            {
                super(itemView);
                iv_add_picture = addPictureBinding.ivAddPicture;
                addPictureBinding.itemAddPicture.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        startActivityForResult(new Intent(ShareEditActivity.this, SharingImageSelectActivity.class),
                                ACTION_SELECT_IMAGE);
                    }
                });
            }
        }

        class PictureViewHolder extends RecyclerView.ViewHolder
        {
            ImageView iv_picture;

            public PictureViewHolder(
                    @NonNull
                            View itemView)
            {
                super(itemView);
                iv_picture = shareEditItemBinding.ivPicture;
            }
        }
    }
}