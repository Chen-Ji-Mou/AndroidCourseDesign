package com.chenjimou.androidcoursedesign.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import android.app.Service;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.chenjimou.androidcoursedesign.R;
import com.chenjimou.androidcoursedesign.databinding.ActivityMySpaceBinding;
import com.chenjimou.androidcoursedesign.databinding.LayoutMySpaceItemBinding;
import com.chenjimou.androidcoursedesign.inter.RetrofitRequest;
import com.chenjimou.androidcoursedesign.model.DeleteSpaceModel;
import com.chenjimou.androidcoursedesign.model.GetStarCountModel;
import com.chenjimou.androidcoursedesign.model.GetUserSpaceModel;
import com.chenjimou.androidcoursedesign.ui.MySpaceItemDecoration;
import com.chenjimou.androidcoursedesign.utils.DateUtils;
import com.chenjimou.androidcoursedesign.utils.DisplayUtils;
import com.chenjimou.androidcoursedesign.utils.SharedPreferencesUtils;
import com.chenjimou.androidcoursedesign.utils.SystemBarUtil;
import com.chenjimou.androidcoursedesign.widget.LoadAnimationDialog;
import com.chenjimou.androidcoursedesign.widget.MyPopupWindow;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MySpaceActivity extends AppCompatActivity implements View.OnClickListener
{
    ActivityMySpaceBinding mBinding;
    Disposable mDisposable;
    Retrofit mRetrofit;
    MySpaceAdapter mAdapter;
    LoadAnimationDialog mDialog;
    MyPopupWindow mPopupWindow;
    Vibrator mVibrator;

    final List<GetUserSpaceModel.DataDTO> dataOnUI = new ArrayList<>();
    final List<Integer> collectionCounts = new ArrayList<>();
    boolean isError = false;
    int lastLoadPosition = -1;
    int currentPosition = -1;

    int screenWidth = -1;

    private static final String TAG = "MySpaceActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMySpaceBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        init();
    }

    void init()
    {
        SystemBarUtil.setStatusBarColor(this, R.color.white);
        SystemBarUtil.setAndroidNativeLightStatusBar(this, true);

        mVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);

        screenWidth = DisplayUtils.getScreenWidth(this);

        mBinding.toolbar.setTitle("");
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDialog = LoadAnimationDialog.init(this, "加载中，请稍后...");

        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerView.addItemDecoration(new MySpaceItemDecoration());
        mBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new MySpaceAdapter();
        mBinding.recyclerView.setAdapter(mAdapter);

        mRetrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .client(new OkHttpClient.Builder()
                        .callTimeout(60, TimeUnit.SECONDS)
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .build())
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        mPopupWindow = new MyPopupWindow.Builder(this)
                .cancelTouchout(true)
                .view(R.layout.layout_delete_space)
                .isFocusable(true)
                .animStyle(R.style.AnimDown)
                .addViewOnclick(R.id.btn_delete_space, this)
                .widthpx(screenWidth - DisplayUtils.dip2px(this, 16))
                .heightdp(50)
                .build();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        reset();
        loadFromInternet();
    }

    void reset()
    {
        isError = false;
        lastLoadPosition = -1;
        currentPosition = -1;
        dataOnUI.clear();
        collectionCounts.clear();
    }

    void loadFromInternet()
    {
        mRetrofit.create(RetrofitRequest.class)
        .getUserSpace(SharedPreferencesUtils.getInstance().getToken(), SharedPreferencesUtils.getInstance().getUserId())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<GetUserSpaceModel>()
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
                            GetUserSpaceModel getUserSpaceModel)
            {
                isError = false;
                dataOnUI.addAll(getUserSpaceModel.getData());
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
                    loadCounts();
                }
                else
                {
                    Toast.makeText(MySpaceActivity.this, "加载失败，请重试", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                }
            }
        });
    }

    void loadCounts()
    {
        Observable.fromArray(dataOnUI.toArray(new GetUserSpaceModel.DataDTO[0]))
        .concatMap(new Function<GetUserSpaceModel.DataDTO, ObservableSource<GetStarCountModel>>()
        {
            @Override
            public ObservableSource<GetStarCountModel> apply(
                    @io.reactivex.annotations.NonNull
                            GetUserSpaceModel.DataDTO dataDTO) throws Exception
            {
                return mRetrofit.create(RetrofitRequest.class)
                        .getStarCount(SharedPreferencesUtils.getInstance().getToken(), dataDTO.getId());
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<GetStarCountModel>()
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
                            GetStarCountModel getStarCountModel)
            {
                isError = false;
                collectionCounts.add(getStarCountModel.getData().getCount());
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
                    if (!dataOnUI.isEmpty())
                    {
                        mBinding.recyclerView.setVisibility(View.VISIBLE);
                        mBinding.layoutNoData.getRoot().setVisibility(View.GONE);
                    }
                    else
                    {
                        mBinding.recyclerView.setVisibility(View.GONE);
                        mBinding.layoutNoData.getRoot().setVisibility(View.VISIBLE);
                    }
                    mAdapter.notifyDataSetChanged();
                    lastLoadPosition = dataOnUI.size();
                }
                else
                {
                    Toast.makeText(MySpaceActivity.this, "加载失败，请重试", Toast.LENGTH_SHORT).show();
                }
                mDialog.dismiss();
            }
        });
    }

    void deleteSpace(String spaceId)
    {
        StringBuilder data = new StringBuilder();
        data.append("id").append("=").append(spaceId);
        String jso = data.substring(0, data.length() - 1);

        mRetrofit.create(RetrofitRequest.class)
        .deleteSpace(SharedPreferencesUtils.getInstance().getToken(),
                RequestBody.create(MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"), jso))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<DeleteSpaceModel>()
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
                            DeleteSpaceModel deleteSpaceModel)
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
                    dataOnUI.remove(currentPosition);
                    mAdapter.notifyItemRemoved(currentPosition);
                    Toast.makeText(MySpaceActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(MySpaceActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                }
                mPopupWindow.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        deleteSpace(dataOnUI.get(currentPosition).getId());
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

    class MySpaceAdapter extends RecyclerView.Adapter<MySpaceAdapter.ViewHolder>
    {
        LayoutMySpaceItemBinding itemBinding;

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(
                @NonNull
                        ViewGroup parent, int viewType)
        {
            itemBinding = LayoutMySpaceItemBinding.inflate(getLayoutInflater(), parent, false);
            return new ViewHolder(itemBinding.getRoot());
        }

        @Override
        public void onBindViewHolder(
                @NonNull
                        ViewHolder holder, int position)
        {
            GetUserSpaceModel.DataDTO dto = dataOnUI.get(position);

            GlideUrl url = new GlideUrl(
                    getString(R.string.request_picture_url) + dto.getPictures().get(0),
                    new LazyHeaders.Builder()
                            .addHeader("Authorization", SharedPreferencesUtils.getInstance().getToken())
                            .build());

            Glide.with(MySpaceActivity.this)
                    .load(url)
                    .into(holder.iv_space_cover);

            holder.tv_space_content.setText(dto.getContent());

            String date = DateUtils.formatDate(dto.getDate());
            holder.tv_space_date.setText(date);

            dto.setCollectionCount(collectionCounts.get(position));

            holder.tv_space_count.setText(String.format(getString(R.string.tv_collection_count),
                    collectionCounts.get(position)));
        }

        @Override
        public int getItemCount()
        {
            return dataOnUI.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            ImageView iv_space_cover;
            TextView tv_space_content;
            TextView tv_space_count;
            TextView tv_space_date;

            public ViewHolder(
                    @NonNull
                            View itemView)
            {
                super(itemView);
                iv_space_cover = itemBinding.ivSpaceCover;
                tv_space_content = itemBinding.tvSpaceContent;
                tv_space_count = itemBinding.tvSpaceCount;
                tv_space_date = itemBinding.tvSpaceDate;
                itemView.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        mVibrator.vibrate(70);
                        currentPosition = getLayoutPosition();
                        mPopupWindow.showAsDropDown(itemView);
                        return true;
                    }
                });
            }
        }
    }
}