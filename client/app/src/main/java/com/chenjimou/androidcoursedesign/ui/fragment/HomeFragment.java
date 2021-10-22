package com.chenjimou.androidcoursedesign.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.chenjimou.androidcoursedesign.R;
import com.chenjimou.androidcoursedesign.databinding.LayoutHomeItemBinding;
import com.chenjimou.androidcoursedesign.inter.RetrofitRequest;
import com.chenjimou.androidcoursedesign.model.GetAllSpacesModel;
import com.chenjimou.androidcoursedesign.model.GetStarCountModel;
import com.chenjimou.androidcoursedesign.model.PostStarModel;
import com.chenjimou.androidcoursedesign.ui.HomeItemDecoration;
import com.chenjimou.androidcoursedesign.ui.LazyLoadFragment;
import com.chenjimou.androidcoursedesign.databinding.FragmentHomeBinding;
import com.chenjimou.androidcoursedesign.ui.activity.SpaceDetailActivity;
import com.chenjimou.androidcoursedesign.utils.DateUtils;
import com.chenjimou.androidcoursedesign.utils.DecodeUtils;
import com.chenjimou.androidcoursedesign.utils.DisplayUtils;
import com.chenjimou.androidcoursedesign.utils.SharedPreferencesUtils;
import com.chenjimou.androidcoursedesign.widget.LoadAnimationDialog;
import com.google.gson.Gson;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewbinding.ViewBinding;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends LazyLoadFragment implements OnRefreshListener, OnLoadMoreListener, View.OnClickListener
{
    FragmentHomeBinding mBinding;
    Disposable mDisposable;
    Retrofit mRetrofit;
    HomeAdapter mAdapter;
    LoadAnimationDialog mDialog;

    final List<GetAllSpacesModel.DataDTO> dataOnUI = new ArrayList<>();
    final List<Integer> sourceWidths = new ArrayList<>();
    final List<Integer> sourceHeights = new ArrayList<>();
    final List<Integer> collectionCounts = new ArrayList<>();
    boolean isError = false;
    int lastLoadPosition = -1;

    public static final int ACTION_POST_STAR = 0;

    private static final String TAG = "HomeFragment";

    @Override
    protected ViewBinding createViewBinding(LayoutInflater inflater, ViewGroup container)
    {
        return FragmentHomeBinding.inflate(inflater, container, false);
    }

    @Override
    protected void init(ViewBinding viewBinding)
    {
        mBinding = (FragmentHomeBinding) viewBinding;

        mDialog = LoadAnimationDialog.init(getContext(), "加载中，请稍后...");

        mBinding.recyclerview.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mBinding.recyclerview.addItemDecoration(new HomeItemDecoration());

        mAdapter = new HomeAdapter();
        mBinding.recyclerview.setAdapter(mAdapter);

        mRetrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .client(new OkHttpClient.Builder()
                        .callTimeout(60, TimeUnit.SECONDS)
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .build())
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        mBinding.smartRefreshLayout.setEnableRefresh(false);
        mBinding.smartRefreshLayout.setEnableLoadMore(false);
        mBinding.smartRefreshLayout.setRefreshFooter(new ClassicsFooter(getContext()));
        mBinding.smartRefreshLayout.setRefreshHeader(new ClassicsHeader(getContext()));
        mBinding.smartRefreshLayout.setOnRefreshListener(this);
        mBinding.smartRefreshLayout.setOnLoadMoreListener(this);

        mBinding.btnSearch.setOnClickListener(this);
    }

    @Override
    protected void initDataFirst()
    {
        reset();
        loadFromInternet();
    }

    void reset()
    {
        dataOnUI.clear();
        sourceWidths.clear();
        sourceHeights.clear();
        collectionCounts.clear();
        isError = false;
        lastLoadPosition = 0;
    }

    void loadFromInternet()
    {
        mRetrofit.create(RetrofitRequest.class)
        .getAllSpaces(SharedPreferencesUtils.getInstance().getToken())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<GetAllSpacesModel>()
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
                            GetAllSpacesModel getAllSpacesModel)
            {

                isError = false;
                Collections.sort(getAllSpacesModel.getData());
                dataOnUI.addAll(getAllSpacesModel.getData());
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
                    Toast.makeText(getContext(), "加载失败，请重试", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                }
            }
        });
    }

    void loadCounts()
    {
        Observable.fromArray(dataOnUI.toArray(new GetAllSpacesModel.DataDTO[0]))
        .concatMap(new Function<GetAllSpacesModel.DataDTO, ObservableSource<GetStarCountModel>>()
        {
            @Override
            public ObservableSource<GetStarCountModel> apply(
                    @io.reactivex.annotations.NonNull
                            GetAllSpacesModel.DataDTO dataDTO) throws Exception
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
                    loadPictures();
                }
                else
                {
                    Toast.makeText(getContext(), "加载失败，请重试", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                }
            }
        });
    }

    void loadPictures()
    {
        Observable.fromArray(dataOnUI.toArray(new GetAllSpacesModel.DataDTO[0]))
        .concatMap(new Function<GetAllSpacesModel.DataDTO, ObservableSource<ResponseBody>>()
        {
            @Override
            public ObservableSource<ResponseBody> apply(
                    @io.reactivex.annotations.NonNull
                            GetAllSpacesModel.DataDTO dataDTO) throws Exception
            {
                return mRetrofit.create(RetrofitRequest.class)
                        .getPicture(SharedPreferencesUtils.getInstance().getToken(), dataDTO.getPictures().get(0));
            }
        })
        .map(new Function<ResponseBody, byte[]>()
        {
            @Override
            public byte[] apply(
                    @io.reactivex.annotations.NonNull
                            ResponseBody responseBody) throws Exception
            {
                return responseBody.bytes();
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<byte[]>()
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
                            byte[] source)
            {
                isError = false;
                int[] dimensions = DecodeUtils.getSourceDimensions(source);
                sourceWidths.add(dimensions[0]);
                sourceHeights.add(dimensions[1]);
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
                        mBinding.recyclerview.setVisibility(View.VISIBLE);
                        mBinding.layoutNoData.getRoot().setVisibility(View.GONE);
                    }
                    else
                    {
                        mBinding.recyclerview.setVisibility(View.GONE);
                        mBinding.layoutNoData.getRoot().setVisibility(View.VISIBLE);
                    }
                    mAdapter.notifyDataSetChanged();
                    lastLoadPosition = dataOnUI.size();
                }
                else
                {
                    Toast.makeText(getContext(), "加载失败，请重试", Toast.LENGTH_SHORT).show();
                }
                mDialog.dismiss();
            }
        });
    }

    public void onLoadLatest()
    {
        reset();
        loadFromInternet();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (mDisposable != null && !mDisposable.isDisposed())
            mDisposable.dispose();
    }

    @Override
    public void onLoadMore(
            @NonNull
                    RefreshLayout refreshLayout)
    {

    }

    @Override
    public void onRefresh(
            @NonNull
                    RefreshLayout refreshLayout)
    {

    }

    @Override
    public void onClick(View v)
    {
        Toast.makeText(getContext(), "进入搜索页面", Toast.LENGTH_SHORT).show();
    }

    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder>
    {
        LayoutHomeItemBinding itemBinding;

        @NonNull
        @Override
        public HomeAdapter.ViewHolder onCreateViewHolder(
                @NonNull
                        ViewGroup parent, int viewType)
        {
            itemBinding = LayoutHomeItemBinding.inflate(getLayoutInflater(), parent, false);
            return new ViewHolder(itemBinding.getRoot());
        }

        @Override
        public void onBindViewHolder(
                @NonNull
                        HomeAdapter.ViewHolder holder, int position)
        {
            int screenWidth = DisplayUtils.getScreenWidth(getContext());
            int imageWidth = screenWidth / 2;
            int imageHeight = (int) (imageWidth / (1.0f * sourceWidths.get(position) / sourceHeights.get(position)));

            ConstraintLayout.LayoutParams layoutParams =
                    (ConstraintLayout.LayoutParams) holder.iv_picture.getLayoutParams();
            layoutParams.height = imageHeight;
            holder.iv_picture.setLayoutParams(layoutParams);

            GetAllSpacesModel.DataDTO dto = dataOnUI.get(position);

            GlideUrl url = new GlideUrl(
                    getString(R.string.request_picture_url) + dto.getPictures().get(0),
                    new LazyHeaders.Builder()
                            .addHeader("Authorization", SharedPreferencesUtils.getInstance().getToken())
                            .build());

            Glide.with(getContext())
                    .load(url)
//                    .placeholder(R.drawable.ic_loading)
                    .override(imageWidth, imageHeight)
                    .into(holder.iv_picture);

            holder.tv_content.setText(dto.getContent());

            String date = DateUtils.formatDate(dto.getDate());
            holder.tv_date.setText(date);

            dto.setCollectionCount(collectionCounts.get(position));

            holder.tv_collection_count.setText(String.format(getString(R.string.tv_collection_count), collectionCounts.get(position)));

            if (dto.getIsStar() == 1)
            {
                holder.iv_collection.setBackgroundResource(R.drawable.icon_favorite);
            }
            else
            {
                holder.iv_collection.setBackgroundResource(R.drawable.icon_unfavorite);
            }
        }

        @Override
        public int getItemCount()
        {
            return dataOnUI.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            ImageView iv_picture;
            TextView tv_content;
            TextView tv_date;
            ImageView iv_collection;
            TextView tv_collection_count;

            public ViewHolder(
                    @NonNull
                            View itemView)
            {
                super(itemView);
                iv_picture = itemBinding.ivPicture;
                tv_content = itemBinding.tvContent;
                tv_date = itemBinding.tvDate;
                iv_collection = itemBinding.ivCollection;
                tv_collection_count = itemBinding.tvCollectionCount;

                itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        int position = getLayoutPosition();
                        Intent intent = new Intent(getContext(), SpaceDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("spaceId", dataOnUI.get(position).getId());
                        bundle.putInt("collectionCount", dataOnUI.get(position).getCollectionCount());
                        intent.putExtras(bundle);
                        startActivityForResult(intent, ACTION_POST_STAR);
                    }
                });

                iv_collection.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        int position = getLayoutPosition();

                        mRetrofit.create(RetrofitRequest.class)
                        .postStar(SharedPreferencesUtils.getInstance().getToken(), dataOnUI.get(position).getId())
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
                                    GetAllSpacesModel.DataDTO dto = dataOnUI.get(position);
                                    int count = collectionCounts.get(position);
                                    if (dto.getIsStar() == 1)
                                    {
                                        iv_collection.setBackgroundResource(R.drawable.icon_unfavorite);
                                        dto.setIsStar(0);
                                        count--;
                                    }
                                    else
                                    {
                                        iv_collection.setBackgroundResource(R.drawable.icon_favorite);
                                        dto.setIsStar(1);
                                        count++;
                                    }
                                    collectionCounts.set(position, count);
                                    dto.setCollectionCount(count);
                                    tv_collection_count.setText(String.format(getString(R.string.tv_collection_count), count));
                                }
                                else
                                {
                                    Toast.makeText(getContext(), "点赞失败，请重试", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        }
    }
}
