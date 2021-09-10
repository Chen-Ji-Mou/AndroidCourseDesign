package com.chenjimou.androidcoursedesign.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chenjimou.androidcoursedesign.R;
import com.chenjimou.androidcoursedesign.databinding.ActivityNoticeBinding;
import com.chenjimou.androidcoursedesign.databinding.LayoutNoticeItemBinding;
import com.chenjimou.androidcoursedesign.inter.RetrofitRequest;
import com.chenjimou.androidcoursedesign.model.GetNoticesModel;
import com.chenjimou.androidcoursedesign.model.ReadNoticeModel;
import com.chenjimou.androidcoursedesign.ui.MySpaceItemDecoration;
import com.chenjimou.androidcoursedesign.utils.DateUtils;
import com.chenjimou.androidcoursedesign.utils.SharedPreferencesUtils;
import com.chenjimou.androidcoursedesign.utils.SystemBarUtil;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NoticeActivity extends AppCompatActivity
{
    ActivityNoticeBinding mBinding;
    NoticeAdapter mAdapter;
    Disposable mDisposable;
    Retrofit mRetrofit;
    LinearLayoutManager mLayoutManager;

    List<GetNoticesModel.DataDTO> notices;
    boolean isError = false;

    private static final String TAG = "NoticeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mBinding = ActivityNoticeBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        init();
    }

    void init()
    {
        SystemBarUtil.setStatusBarColor(this, R.color.white);
        SystemBarUtil.setAndroidNativeLightStatusBar(this, true);

        mBinding.toolbar.setTitle("");
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLayoutManager = new LinearLayoutManager(this);
        mBinding.recyclerView.setLayoutManager(mLayoutManager);
        mBinding.recyclerView.addItemDecoration(new MySpaceItemDecoration());

        mAdapter = new NoticeAdapter();
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

        if (getIntent() != null)
        {
            notices = getIntent().getParcelableArrayListExtra("notices");
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (notices != null && !notices.isEmpty())
        {
            mBinding.recyclerView.setVisibility(View.VISIBLE);
            mBinding.layoutNoData.getRoot().setVisibility(View.GONE);
            showNotices();
        }
        else
        {
            mBinding.recyclerView.setVisibility(View.GONE);
            mBinding.layoutNoData.getRoot().setVisibility(View.VISIBLE);
        }
    }

    void showNotices()
    {
        mAdapter.notifyDataSetChanged();

        Collections.sort(notices);

        Observable.fromArray(notices.toArray(new GetNoticesModel.DataDTO[0]))
        .concatMap(new Function<GetNoticesModel.DataDTO, ObservableSource<ReadNoticeModel>>()
        {
            @Override
            public ObservableSource<ReadNoticeModel> apply(
                    @io.reactivex.annotations.NonNull
                            GetNoticesModel.DataDTO dataDTO) throws Exception
            {
                return mRetrofit.create(RetrofitRequest.class)
                        .readNotice(SharedPreferencesUtils.getInstance().getToken(), dataDTO.getId());
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<ReadNoticeModel>()
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
                            ReadNoticeModel readNoticeModel)
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

    class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder>
    {
        LayoutNoticeItemBinding itemBinding;

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(
                @NonNull
                        ViewGroup parent, int viewType)
        {
            itemBinding = LayoutNoticeItemBinding.inflate(getLayoutInflater(), parent, false);
            return new ViewHolder(itemBinding.getRoot());
        }

        @Override
        public void onBindViewHolder(
                @NonNull
                        ViewHolder holder, int position)
        {
            GetNoticesModel.DataDTO dto = notices.get(position);

            if (dto.getRead() == 0)
            {
                holder.iv_unread.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.iv_unread.setVisibility(View.GONE);
            }

            holder.tv_user_name.setText(String.format(getString(R.string.tv_user_name), dto.getSender()));

            holder.tv_notice_content.setText(dto.getContent());

            String date = DateUtils.formatDate(dto.getDate());
            holder.tv_notice_date.setText(date);
        }

        @Override
        public int getItemCount()
        {
            return notices.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            CircleImageView iv_head_portrait;
            TextView tv_user_name;
            TextView tv_notice_content;
            TextView tv_notice_date;
            ImageView iv_unread;

            public ViewHolder(
                    @NonNull
                            View itemView)
            {
                super(itemView);
                iv_head_portrait = itemBinding.ivHeadPortrait;
                tv_user_name = itemBinding.tvUserName;
                tv_notice_content = itemBinding.tvNoticeContent;
                tv_notice_date = itemBinding.tvNoticeDate;
                iv_unread = itemBinding.ivUnread;
            }
        }
    }
}