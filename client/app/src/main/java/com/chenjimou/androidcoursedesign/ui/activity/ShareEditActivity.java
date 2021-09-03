package com.chenjimou.androidcoursedesign.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chenjimou.androidcoursedesign.R;
import com.chenjimou.androidcoursedesign.databinding.ActivityShareEditBinding;
import com.chenjimou.androidcoursedesign.databinding.LayoutAddPictureItemBinding;
import com.chenjimou.androidcoursedesign.databinding.LayoutShareEditItemBinding;
import com.chenjimou.androidcoursedesign.inter.RetrofitRequest;
import com.chenjimou.androidcoursedesign.model.PictureFromDeviceModel;
import com.chenjimou.androidcoursedesign.model.PostSpaceModel;
import com.chenjimou.androidcoursedesign.model.UpLoadPictureModel;
import com.chenjimou.androidcoursedesign.ui.ShareEditItemDecoration;
import com.chenjimou.androidcoursedesign.utils.DisplayUtils;
import com.chenjimou.androidcoursedesign.utils.SharedPreferencesUtils;
import com.chenjimou.androidcoursedesign.utils.SystemBarUtil;
import com.chenjimou.androidcoursedesign.widget.LoadAnimationDialog;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ShareEditActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher
{
    ActivityShareEditBinding mBinding;
    ShareEditAdapter mAdapter;
    InputMethodManager mManager;
    Retrofit mRetrofit;
    Disposable mDisposable;
    LoadAnimationDialog mDialog;

    final List<PictureFromDeviceModel> dataOnUI = new ArrayList<>();

    boolean isError = false;

    static final int ACTION_SELECT_IMAGE = 0;

    private static final String TAG = "ShareEditActivity";

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

        mDialog = LoadAnimationDialog.init(this, "发布中，请稍后...");

        mBinding.toolbar.setTitle("");
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBinding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3,
                StaggeredGridLayoutManager.VERTICAL));
        mBinding.recyclerView.addItemDecoration(new ShareEditItemDecoration());

        mAdapter = new ShareEditAdapter();
        mBinding.recyclerView.setAdapter(mAdapter);

        mBinding.etContent.addTextChangedListener(this);

        mBinding.btnShareFinish.setOnClickListener(this);

        mRetrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .client(new OkHttpClient.Builder()
                        .callTimeout(60, TimeUnit.SECONDS)
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .build())
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
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

    void reset()
    {
        dataOnUI.clear();
    }

    void dispatchPostSpace(List<String> pictureIds)
    {
        mRetrofit.create(RetrofitRequest.class)
        .postSpace(SharedPreferencesUtils.getInstance().getToken(), mBinding.etContent.getText().toString(), pictureIds)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<PostSpaceModel>()
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
                            PostSpaceModel postSpaceModel)
            {
                isError = false;
            }

            @Override
            public void onError(
                    @io.reactivex.annotations.NonNull
                            Throwable e)
            {
                isError = true;
            }

            @Override
            public void onComplete()
            {
                if (!isError)
                {
                    Toast.makeText(ShareEditActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }
                else
                {
                    Toast.makeText(ShareEditActivity.this, "发布失败，请重试", Toast.LENGTH_SHORT).show();
                }
                mDialog.dismiss();
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
    protected void onActivityResult(int requestCode, int resultCode,
            @Nullable
                    Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            if (data != null && data.getExtras() != null)
            {
                Bundle bundle = data.getExtras();
                ArrayList<PictureFromDeviceModel> selectPictures = bundle.getParcelableArrayList("pictures");
                if (selectPictures != null && !selectPictures.isEmpty())
                {
                    dataOnUI.addAll(selectPictures);
                    mAdapter.notifyDataSetChanged();
                }
            }
            mBinding.btnShareFinish.setEnabled(!mBinding.etContent.getText().toString().isEmpty() && !dataOnUI.isEmpty());
        }
    }

    @Override
    public void onClick(View v)
    {
        List<String> pictureIds = new ArrayList<>();

        Observable.fromArray(dataOnUI.toArray(new PictureFromDeviceModel[0]))
        .concatMap(new Function<PictureFromDeviceModel, ObservableSource<UpLoadPictureModel>>()
        {
            @Override
            public ObservableSource<UpLoadPictureModel> apply(
                    @io.reactivex.annotations.NonNull
                            PictureFromDeviceModel pictureFromDeviceModel) throws Exception
            {
                File file = new File(pictureFromDeviceModel.getPath());
                RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
                return mRetrofit.create(RetrofitRequest.class)
                        .upLoadPicture(SharedPreferencesUtils.getInstance().getToken(), part);
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<UpLoadPictureModel>()
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
                            UpLoadPictureModel upLoadPictureModel)
            {
                isError = false;
                pictureIds.add(upLoadPictureModel.getData().getId());
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
                    dispatchPostSpace(pictureIds);
                }
                else
                {
                    Toast.makeText(ShareEditActivity.this, "发布失败，请重试", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {
        mBinding.btnShareFinish.setEnabled(!mBinding.etContent.getText().toString().isEmpty() && !dataOnUI.isEmpty());
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        mBinding.btnShareFinish.setEnabled(!mBinding.etContent.getText().toString().isEmpty() && !dataOnUI.isEmpty());
    }

    @Override
    public void afterTextChanged(Editable s)
    {
        mBinding.btnShareFinish.setEnabled(!mBinding.etContent.getText().toString().isEmpty() && !dataOnUI.isEmpty());
    }

    class ShareEditAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        static final int TYPE_ADD = 0;
        static final int TYPE_PICTURE = 1;

        LayoutAddPictureItemBinding addPictureBinding;
        LayoutShareEditItemBinding itemBinding;

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
                addPictureBinding = LayoutAddPictureItemBinding.inflate(getLayoutInflater(), parent, false);
                viewHolder = new AddPictureViewHolder(addPictureBinding.getRoot());
            }
            else
            {
                itemBinding = LayoutShareEditItemBinding.inflate(getLayoutInflater(), parent, false);
                viewHolder = new PictureViewHolder(itemBinding.getRoot());
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(
                @NonNull
                        RecyclerView.ViewHolder holder, int position)
        {
            int screenWidth = DisplayUtils.getScreenWidth(ShareEditActivity.this);

            StaggeredGridLayoutManager.LayoutParams layoutParams =
                    (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.height = screenWidth / 3;
            layoutParams.width = screenWidth / 3;
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
                itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        reset();
                        startActivityForResult(new Intent(ShareEditActivity.this, SharingImageSelectActivity.class),
                                ACTION_SELECT_IMAGE);
                    }
                });
            }
        }

        class PictureViewHolder extends RecyclerView.ViewHolder
        {
            ImageView iv_picture;
            ImageView iv_select;

            public PictureViewHolder(
                    @NonNull
                            View itemView)
            {
                super(itemView);
                iv_picture = itemBinding.ivPicture;
                iv_select = itemBinding.ivSelect;
            }
        }
    }
}