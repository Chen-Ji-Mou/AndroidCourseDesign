package com.chenjimou.androidcoursedesign.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chenjimou.androidcoursedesign.R;
import com.chenjimou.androidcoursedesign.databinding.ActivitySpaceDetailBinding;
import com.chenjimou.androidcoursedesign.databinding.LayoutSpaceDetailItemBinding;
import com.chenjimou.androidcoursedesign.inter.RetrofitRequest;
import com.chenjimou.androidcoursedesign.model.GetCommentsModel;
import com.chenjimou.androidcoursedesign.utils.DisplayUtils;
import com.chenjimou.androidcoursedesign.utils.SharedPreferencesUtils;
import com.chenjimou.androidcoursedesign.widget.MyCoordinatorLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SpaceDetailActivity extends AppCompatActivity
        implements View.OnClickListener, AppBarLayout.OnOffsetChangedListener,
                TextView.OnEditorActionListener, MyCoordinatorLayout.OnTouchListener
{
    ActivitySpaceDetailBinding mBinding;
    SpaceDetailAdapter mAdapter;
    Retrofit mRetrofit;
    Disposable mDisposable;
    InputMethodManager mManager;

    String spaceId;
    List<String> pictures;
    String content;
    String userId;
    int collectionCount = -1;
    int screenHeight = -1;

    List<GetCommentsModel.DataDTO> comments = new ArrayList<>();
    boolean isError = false;
    boolean isInputOpen = false;
    int lastLoadPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        mBinding = ActivitySpaceDetailBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        init();
    }

    void init()
    {
        mBinding.toolbar.setTitle("");
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        screenHeight = DisplayUtils.getScreenHeight(this);

        ViewGroup.LayoutParams layoutParams = mBinding.appBarLayout.getLayoutParams();
        layoutParams.height = (int)(screenHeight * 0.75);
        mBinding.appBarLayout.setLayoutParams(layoutParams);

        mBinding.rvComment.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new SpaceDetailAdapter();
        mBinding.rvComment.setAdapter(mAdapter);

        mBinding.rootView.setOnTouchListener(this);
        mBinding.appBarLayout.addOnOffsetChangedListener(this);
        mBinding.fab.setOnClickListener(this);
        mBinding.btnComment.setOnClickListener(this);
        mBinding.etInputComment.setOnEditorActionListener(this);

        mRetrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .client(new OkHttpClient.Builder()
                        .callTimeout(60, TimeUnit.SECONDS)
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .build())
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        if (getIntent() != null && getIntent().getExtras() != null)
        {
            spaceId = getIntent().getExtras().getString("spaceId");
            pictures = getIntent().getExtras().getStringArrayList("pictures");
            content = getIntent().getExtras().getString("content");
            userId = getIntent().getExtras().getString("userId");
            collectionCount = getIntent().getExtras().getInt("collectionCount");
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

        if (collectionCount != -1)
        {
            mBinding.tvFavoriteCount.setText(String.format(getString(R.string.tv_favorite_count), collectionCount));
        }

        if (content != null)
        {
            mBinding.tvContent.setText(content);
        }

        reset();
        loadComments();
    }

    void loadComments()
    {
        mRetrofit.create(RetrofitRequest.class)
        .getComments(SharedPreferencesUtils.getInstance().getToken(), spaceId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<GetCommentsModel>()
        {
            @Override
            public void onSubscribe(
                    @io.reactivex.annotations.NonNull
                            Disposable d)
            {
                mDisposable = d;
            }

            @Override
            public void onNext(
                    @io.reactivex.annotations.NonNull
                            GetCommentsModel getCommentsModel)
            {
                isError = false;
                comments.addAll(getCommentsModel.getData());
            }

            @Override
            public void onError(
                    @io.reactivex.annotations.NonNull
                            Throwable e)
            {
                isError = true;
                e.printStackTrace();
            }

            @Override
            public void onComplete()
            {
                if (!isError)
                {
                    if (!comments.isEmpty())
                    {
                        mBinding.rvComment.setVisibility(View.VISIBLE);
                        mBinding.getRoot().findViewById(R.id.layout_no_data).setVisibility(View.GONE);
                    }
                    else
                    {
                        mBinding.rvComment.setVisibility(View.GONE);
                        mBinding.getRoot().findViewById(R.id.layout_no_data).setVisibility(View.VISIBLE);
                    }
                    mAdapter.notifyDataSetChanged();
                    mBinding.tvCommentCount.setText(getString(R.string.tv_comment_count, comments.size()));
                    lastLoadPosition = comments.size();
                }
                else
                {
                    Toast.makeText(SpaceDetailActivity.this, "加载失败，请重试", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    void hideSoftKeyboard()
    {
        mBinding.etInputComment.clearFocus();
        if (mManager.isActive() && getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null)
        {
            mManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    void reset()
    {
        isError = false;
        isInputOpen = false;
        lastLoadPosition = -1;
        comments.clear();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.fab:
                Toast.makeText(this, "点赞", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_comment:
                mBinding.etInputComment.setVisibility(View.VISIBLE);
                isInputOpen = true;
                mBinding.etInputComment.requestFocus();
                mManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                break;
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
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset)
    {
        if (Math.abs(verticalOffset) >= screenHeight * 0.75 * 0.75)
        {
            mBinding.tvTitle.setVisibility(View.VISIBLE);
        }
        else
        {
            mBinding.tvTitle.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
    {
        final String inputContent = mBinding.etInputComment.getText().toString().trim();
        if (actionId == EditorInfo.IME_ACTION_SEND)
        {
            if (inputContent.isEmpty())
            {
                Toast.makeText(this, "输入内容不能为空", Toast.LENGTH_SHORT).show();
                return false;
            }

            mBinding.etInputComment.setText(null);
            hideSoftKeyboard();
            mBinding.etInputComment.setVisibility(View.GONE);
            isInputOpen = false;

//            comments.add(inputContent);
//            mAdapter.notifyItemInserted(comments.size());
//            recyclerView.smoothScrollToPosition(comments.size() + 1);
            return true;
        }
        return false;
    }

    @Override
    public void onTouch(boolean isInput)
    {
        if (isInputOpen && !isInput)
        {
            mBinding.etInputComment.setVisibility(View.GONE);
            isInputOpen = false;
            hideSoftKeyboard();
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (mDisposable != null && !mDisposable.isDisposed())
            mDisposable.dispose();
    }

    class SpaceDetailAdapter extends RecyclerView.Adapter<SpaceDetailAdapter.AmongViewHolder>
    {
        LayoutSpaceDetailItemBinding amongItemBinding;

        @NonNull
        @Override
        public SpaceDetailAdapter.AmongViewHolder onCreateViewHolder(
                @NonNull
                        ViewGroup parent, int viewType)
        {
            amongItemBinding = LayoutSpaceDetailItemBinding.inflate(getLayoutInflater());
            return new AmongViewHolder(amongItemBinding.getRoot());
        }

        @Override
        public void onBindViewHolder(
                @NonNull
                SpaceDetailAdapter.AmongViewHolder viewHolder, int position)
        {
            GetCommentsModel.DataDTO dto = comments.get(position);
            viewHolder.tv_comment_content.setText(dto.getContent());
            viewHolder.tv_user_name.setText(String.format(getString(R.string.tv_user_name), dto.getUserId()));
        }

        @Override
        public int getItemCount()
        {
            return comments.size();
        }

        public class AmongViewHolder extends RecyclerView.ViewHolder
        {
            ImageView iv_user_icon;
            TextView tv_user_name;
            TextView tv_comment_content;

            public AmongViewHolder(
                    @NonNull
                            View itemView)
            {
                super(itemView);
                iv_user_icon = amongItemBinding.ivUserIcon;
                tv_user_name = amongItemBinding.tvUserName;
                tv_comment_content = amongItemBinding.tvCommentContent;
            }
        }
    }
}