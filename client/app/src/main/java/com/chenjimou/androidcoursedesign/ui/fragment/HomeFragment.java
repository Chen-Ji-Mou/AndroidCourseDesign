package com.chenjimou.androidcoursedesign.ui.fragment;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chenjimou.androidcoursedesign.R;
import com.chenjimou.androidcoursedesign.databinding.LayoutHomeItemBinding;
import com.chenjimou.androidcoursedesign.inter.RetrofitRequest;
import com.chenjimou.androidcoursedesign.model.GetAllSpacesModel;
import com.chenjimou.androidcoursedesign.ui.HomeItemDecoration;
import com.chenjimou.androidcoursedesign.ui.LazyLoadFragment;
import com.chenjimou.androidcoursedesign.databinding.FragmentHomeBinding;
import com.chenjimou.androidcoursedesign.utils.DateUtils;
import com.chenjimou.androidcoursedesign.utils.DecodeUtils;
import com.chenjimou.androidcoursedesign.utils.DisplayUtils;
import com.chenjimou.androidcoursedesign.utils.SharedPreferencesUtils;
import com.google.gson.Gson;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewbinding.ViewBinding;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends LazyLoadFragment implements OnRefreshListener, OnLoadMoreListener
{
    FragmentHomeBinding mBinding;
    Disposable mDisposable;
    Retrofit mRetrofit;
    HomeAdapter mAdapter;

    final List<GetAllSpacesModel.DataDTO> dataOnUI = new ArrayList<>();

    final List<byte[]> sources = new ArrayList<>();
    final List<Integer> sourceWidths = new ArrayList<>();
    final List<Integer> sourceHeights = new ArrayList<>();

    boolean isError = false;
    int lastLoadPosition = 0;

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

        mBinding.recyclerview.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mBinding.recyclerview.addItemDecoration(new HomeItemDecoration());

        mAdapter = new HomeAdapter();
        mBinding.recyclerview.setAdapter(mAdapter);

        mRetrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseUrl))
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
    }

    @Override
    protected void initDataFirst()
    {
        loadFromInternet();
        Log.d(TAG, "initDataFirst: ");
    }

    void loadFromInternet()
    {
        mRetrofit.create(RetrofitRequest.class).getAllSpaces(SharedPreferencesUtils.getInstance().getToken())
                .map(new Function<GetAllSpacesModel, GetAllSpacesModel>()
                {
                    @Override
                    public GetAllSpacesModel apply(
                            @io.reactivex.annotations.NonNull
                                    GetAllSpacesModel getAllSpacesModel) throws Exception
                    {
                        for (GetAllSpacesModel.DataDTO dto : getAllSpacesModel.getData())
                        {
                            byte[] source = DecodeUtils.decodeByBase64(dto.getPictures().get(0));
                            sources.add(source);

                            int[] dimensions = DecodeUtils.getSourceDimensions(source);
                            sourceWidths.add(dimensions[0]);
                            sourceHeights.add(dimensions[1]);
                        }
                        return getAllSpacesModel;
                    }
                })
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
                    }

                    @Override
                    public void onNext(
                            @io.reactivex.annotations.NonNull
                                    GetAllSpacesModel getAllSpacesModel)
                    {
                        isError = false;
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
                            if(!dataOnUI.isEmpty())
                            {
                                mBinding.recyclerview.setVisibility(View.VISIBLE);
                                mBinding.getRoot().findViewById(R.id.layout_no_data).setVisibility(View.GONE);
                            }
                            else
                            {
                                mBinding.recyclerview.setVisibility(View.GONE);
                                mBinding.getRoot().findViewById(R.id.layout_no_data).setVisibility(View.VISIBLE);
                            }
                            mAdapter.notifyDataSetChanged();
                            lastLoadPosition = dataOnUI.size();
                        }
                    }
                });
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
            int screenWidth = DisplayUtils.getScreenWidth((Activity)getContext());
            int imageWidth = (screenWidth - DisplayUtils.dip2px(getContext(), 24)) / 2;
            int imageHeight = (int) (imageWidth * (1.0f * sourceWidths.get(position) / sourceHeights.get(position)));

            StaggeredGridLayoutManager.LayoutParams layoutParams =
                    (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.height = imageHeight;
            holder.itemView.setLayoutParams(layoutParams);

            Glide.with(getContext())
                    .load(sources.get(position))
//                    .placeholder(R.drawable.ic_loading)
                    .override(imageWidth, imageHeight)
                    .into(holder.iv_picture);

            holder.tv_content.setText(dataOnUI.get(position).getContent());

            String date = DateUtils.formatDate(dataOnUI.get(position).getDate());
            holder.tv_date.setText(date);
        }

        @Override
        public int getItemCount()
        {
            return dataOnUI.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            CardView cv_item;
            ImageView iv_picture;
            TextView tv_content;
            TextView tv_date;

            public ViewHolder(
                    @NonNull
                            View itemView)
            {
                super(itemView);
                cv_item = itemBinding.cvItem;
                iv_picture = itemBinding.ivPicture;
                tv_content = itemBinding.tvContent;
                tv_date = itemBinding.tvDate;
            }
        }
    }
}
