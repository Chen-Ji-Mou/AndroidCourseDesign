package com.chenjimou.androidcoursedesign.ui.fragment;

import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chenjimou.androidcoursedesign.R;
import com.chenjimou.androidcoursedesign.inter.RetrofitRequest;
import com.chenjimou.androidcoursedesign.model.GetNoticesModel;
import com.chenjimou.androidcoursedesign.ui.LazyLoadFragment;
import com.chenjimou.androidcoursedesign.databinding.FragmentPersonalBinding;
import com.chenjimou.androidcoursedesign.ui.activity.MySpaceActivity;
import com.chenjimou.androidcoursedesign.ui.activity.NoticeActivity;
import com.chenjimou.androidcoursedesign.utils.SharedPreferencesUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.viewbinding.ViewBinding;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class PersonalFragment extends LazyLoadFragment implements View.OnClickListener
{
    FragmentPersonalBinding mBinding;
    Disposable mDisposable;
    Retrofit mRetrofit;

    final List<GetNoticesModel.DataDTO> notices = new ArrayList<>();
    boolean isError = false;

    boolean isUnRead = false;

    @Override
    protected ViewBinding createViewBinding(LayoutInflater inflater, ViewGroup container)
    {
        return FragmentPersonalBinding.inflate(inflater, container, false);
    }

    @Override
    protected void init(ViewBinding viewBinding)
    {
        mBinding = (FragmentPersonalBinding) viewBinding;

        mBinding.ivNotice.setOnClickListener(this);
        mBinding.btnMySpace.setOnClickListener(this);
        mBinding.btnMyComment.setOnClickListener(this);

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
    protected void initDataFirst()
    {
        mBinding.tvUserName.setText(SharedPreferencesUtils.getInstance().getUsername());
    }

    @Override
    public void onResume()
    {
        super.onResume();
        reset();
        loadNotices();
    }

    void reset()
    {
        notices.clear();
        isError = false;
    }

    void loadNotices()
    {
        mRetrofit.create(RetrofitRequest.class)
        .getNotices(SharedPreferencesUtils.getInstance().getToken())
        .map(new Function<GetNoticesModel, GetNoticesModel>()
        {
            @Override
            public GetNoticesModel apply(
                    @NonNull
                            GetNoticesModel getNoticesModel) throws Exception
            {
                for (GetNoticesModel.DataDTO dto : getNoticesModel.getData())
                {
                    if (dto.getRead() == 0)
                    {
                        isUnRead = true;
                        break;
                    }
                }
                return getNoticesModel;
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<GetNoticesModel>()
        {
            @Override
            public void onSubscribe(
                    @NonNull
                            Disposable d)
            {
                mDisposable = d;
            }

            @Override
            public void onNext(
                    @NonNull
                            GetNoticesModel getNoticesModel)
            {
                isError = false;
                notices.addAll(getNoticesModel.getData());
            }

            @Override
            public void onError(
                    @NonNull
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
                    if (isUnRead)
                    {
                        mBinding.ivNotice.setImageResource(R.drawable.bg_notice_spot);
                    }
                    else
                    {
                        mBinding.ivNotice.setImageResource(R.drawable.icon_notice);
                    }
                }
                else
                {
                    Toast.makeText(getContext(), "通知更新失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.iv_notice:
                isUnRead = false;
                Intent intent = new Intent(getContext(), NoticeActivity.class);
                intent.putParcelableArrayListExtra("notices", (ArrayList<? extends Parcelable>) notices);
                startActivity(intent);
                break;
            case R.id.btn_my_space:
                startActivity(new Intent(getContext(), MySpaceActivity.class));
                break;
            case R.id.btn_my_comment:
                break;
        }
    }
}
