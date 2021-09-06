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
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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
import com.chenjimou.androidcoursedesign.databinding.LayoutNoDataBinding;
import com.chenjimou.androidcoursedesign.databinding.LayoutSpaceDetailItemBinding;
import com.chenjimou.androidcoursedesign.inter.RetrofitRequest;
import com.chenjimou.androidcoursedesign.model.AddNewCommentModel;
import com.chenjimou.androidcoursedesign.model.GetAllSpacesModel;
import com.chenjimou.androidcoursedesign.model.GetCommentsModel;
import com.chenjimou.androidcoursedesign.model.GetSpaceDetailModel;
import com.chenjimou.androidcoursedesign.model.PostStarModel;
import com.chenjimou.androidcoursedesign.ui.SpaceDetailItemDecoration;
import com.chenjimou.androidcoursedesign.utils.DisplayUtils;
import com.chenjimou.androidcoursedesign.utils.SharedPreferencesUtils;
import com.chenjimou.androidcoursedesign.widget.LoadAnimationDialog;
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
    LoadAnimationDialog mDialog;

    String spaceId;
    int collectionCount = -1;

    int screenHeight = -1;
    float actionBarSize = -1;
    boolean isFirstLoad = true;
    int isStar = -1;
    boolean isRefresh = false;

    List<GetCommentsModel.DataDTO> comments = new ArrayList<>();
    boolean isError = false;
    boolean isInputOpen = false;
    int lastLoadPosition = -1;

    static final int[] attrs = new int[] {
            android.R.attr.actionBarSize
    };

    private static final String TAG = "SpaceDetailActivity";

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

        mDialog = LoadAnimationDialog.init(this, "加载中，请稍后...");

        TypedArray typedArray = obtainStyledAttributes(attrs);
        actionBarSize = typedArray.getDimension(0, 0);
        typedArray.recycle();

        ViewGroup.LayoutParams layoutParams = mBinding.appBarLayout.getLayoutParams();
        layoutParams.height = (int)(screenHeight * 0.75);
        mBinding.appBarLayout.setLayoutParams(layoutParams);

        mBinding.rvComment.setLayoutManager(new LinearLayoutManager(this));
        mBinding.rvComment.addItemDecoration(new SpaceDetailItemDecoration());

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
            collectionCount = getIntent().getExtras().getInt("collectionCount");
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (collectionCount != -1)
        {
            mBinding.tvFavoriteCount.setText(String.format(getString(R.string.tv_favorite_count), collectionCount));
        }

        reset();
        loadDetail();
    }

    void loadDetail()
    {
        mRetrofit.create(RetrofitRequest.class)
        .getSpaceDetail(SharedPreferencesUtils.getInstance().getToken(), spaceId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<GetSpaceDetailModel>()
        {
            @Override
            public void onSubscribe(
                    @io.reactivex.annotations.NonNull
                            Disposable d)
            {
                mDisposable = d;
                mDialog.show();
            }

            @Override
            public void onNext(
                    @io.reactivex.annotations.NonNull
                            GetSpaceDetailModel getSpaceDetailModel)
            {
                isError = false;
                mBinding.banner.notifyDataSetChanged(getSpaceDetailModel.getData().getPictures());
                mBinding.tvContent.setText(getSpaceDetailModel.getData().getContent());
                isStar = getSpaceDetailModel.getData().getIsStar();
                if (isStar == 1)
                {
                    mBinding.fab.setImageResource(R.drawable.icon_favorite);
                    mBinding.fab.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#E51C23")));
                }
                else
                {
                    mBinding.fab.setImageResource(R.drawable.icon_unfavorite);
                    mBinding.fab.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#BDBDBD")));
                }
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
                    loadComments();
                }
                else
                {
                    Toast.makeText(SpaceDetailActivity.this, "加载失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                if (isFirstLoad)
                {
                    mDialog.dismiss();
                }
                if (!isError)
                {
                    mAdapter.notifyDataSetChanged();
                    mBinding.tvCommentCount.setText(getString(R.string.tv_comment_count, comments.size()));
                    if (!isFirstLoad)
                    {
                        mBinding.rvComment.smoothScrollToPosition(comments.size());
                    }
                    lastLoadPosition = comments.size();
                    isFirstLoad = false;
                }
                else
                {
                    Toast.makeText(SpaceDetailActivity.this, "加载失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void postNewComment(String content)
    {
        mRetrofit.create(RetrofitRequest.class)
        .addNewComment(SharedPreferencesUtils.getInstance().getToken(), content, spaceId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<AddNewCommentModel>()
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
                            AddNewCommentModel addNewCommentModel)
            {
                isError = false;

                GetCommentsModel.DataDTO dto = new GetCommentsModel.DataDTO();
                dto.setContent(addNewCommentModel.getData().getContent());
                dto.setDate(addNewCommentModel.getData().getDate());
                dto.setId(addNewCommentModel.getData().getId());
                dto.setPostId(addNewCommentModel.getData().getPostId());
                dto.setUserId(addNewCommentModel.getData().getUserId());
                comments.add(dto);
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
                    reset();
                    loadComments();
                }
                else
                {
                    Toast.makeText(SpaceDetailActivity.this, "发布评论失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void showSoftKeyboard()
    {
        mBinding.etInputComment.requestFocus();
        mManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    void hideSoftKeyboard()
    {
        mBinding.etInputComment.clearFocus();
        mManager.hideSoftInputFromWindow(mBinding.etInputComment.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
                postStar();
                break;
            case R.id.btn_comment:
                mBinding.etInputComment.setVisibility(View.VISIBLE);
                isInputOpen = true;
                showSoftKeyboard();
                break;
        }
    }

    void postStar()
    {
        mRetrofit.create(RetrofitRequest.class)
        .postStar(SharedPreferencesUtils.getInstance().getToken(), spaceId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<PostStarModel>()
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
                            PostStarModel postStarModel)
            {
                isError = false;
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
                    if (isStar == 1)
                    {
                        mBinding.fab.setImageResource(R.drawable.icon_unfavorite);
                        mBinding.fab.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#BDBDBD")));
                        isStar = 0;
                        collectionCount--;
                    }
                    else
                    {
                        mBinding.fab.setImageResource(R.drawable.icon_favorite);
                        mBinding.fab.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#E51C23")));
                        isStar = 1;
                        collectionCount++;
                    }
                    mBinding.tvFavoriteCount.setText(String.format(getString(R.string.tv_favorite_count), collectionCount));

                    if (!isRefresh)
                    {
                        setResult(RESULT_OK);
                    }
                }
                else
                {
                    Toast.makeText(SpaceDetailActivity.this, "点赞失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
            mBinding.etInputComment.setVisibility(View.GONE);
            isInputOpen = false;
            hideSoftKeyboard();

            postNewComment(inputContent);

            return true;
        }
        return false;
    }

    @Override
    public void onTouch(boolean isInput)
    {
        if (isInputOpen && !isInput)
        {
            mBinding.etInputComment.setText(null);
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

    class SpaceDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        LayoutNoDataBinding noDataItemBinding;
        LayoutSpaceDetailItemBinding amongItemBinding;

        static final int TYPE_NO_DATA = 0;
        static final int TYPE_AMONG = 1;

        @Override
        public int getItemViewType(int position)
        {
            if (comments.isEmpty())
            {
                return TYPE_NO_DATA;
            }
            else
            {
                return TYPE_AMONG;
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(
                @NonNull
                        ViewGroup parent, int viewType)
        {
            RecyclerView.ViewHolder holder;
            if (viewType == TYPE_NO_DATA)
            {
                noDataItemBinding = LayoutNoDataBinding.inflate(getLayoutInflater(), parent, false);
                holder = new NoDataViewHolder(noDataItemBinding.getRoot());
            }
            else
            {
                amongItemBinding = LayoutSpaceDetailItemBinding.inflate(getLayoutInflater(), parent, false);
                holder = new AmongViewHolder(amongItemBinding.getRoot());
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(
                @NonNull
                        RecyclerView.ViewHolder holder, int position)
        {
            if (getItemViewType(position) == TYPE_AMONG)
            {
                AmongViewHolder viewHolder = (AmongViewHolder) holder;
                GetCommentsModel.DataDTO dto = comments.get(position);
                viewHolder.tv_comment_content.setText(dto.getContent());
                viewHolder.tv_user_name.setText(String.format(getString(R.string.tv_user_name), dto.getUserId()));
            }
            else if (getItemViewType(position) == TYPE_NO_DATA)
            {
                int screenHeight = DisplayUtils.getScreenHeight(SpaceDetailActivity.this);
                int itemHeight = (int)(screenHeight - actionBarSize
                        - mBinding.layoutCommentHead.getHeight() - DisplayUtils.dip2px(SpaceDetailActivity.this, 50));
                ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                layoutParams.height = itemHeight;
                holder.itemView.setLayoutParams(layoutParams);
            }
        }

        @Override
        public int getItemCount()
        {
            if (comments.isEmpty())
            {
                return 1;
            }
            else
            {
                return comments.size();
            }
        }

        public class NoDataViewHolder extends RecyclerView.ViewHolder
        {
            public NoDataViewHolder(
                    @NonNull
                            View itemView)
            {
                super(itemView);
            }
        }

        public class AmongViewHolder extends RecyclerView.ViewHolder
        {
            ImageView iv_head_portrait;
            TextView tv_user_name;
            TextView tv_comment_content;

            public AmongViewHolder(
                    @NonNull
                            View itemView)
            {
                super(itemView);
                iv_head_portrait = amongItemBinding.ivHeadPortrait;
                tv_user_name = amongItemBinding.tvUserName;
                tv_comment_content = amongItemBinding.tvCommentContent;
            }
        }
    }
}