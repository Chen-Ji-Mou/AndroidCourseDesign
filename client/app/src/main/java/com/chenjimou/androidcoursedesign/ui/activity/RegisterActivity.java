package com.chenjimou.androidcoursedesign.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.chenjimou.androidcoursedesign.R;
import com.chenjimou.androidcoursedesign.databinding.ActivityRegisterBinding;
import com.chenjimou.androidcoursedesign.inter.RetrofitRequest;
import com.chenjimou.androidcoursedesign.model.RegisterModel;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener
{
    ActivityRegisterBinding mBinding;
    Disposable mDisposable;
    Retrofit mRetrofit;

    boolean isError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        mBinding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        init();
    }

    void init()
    {
        mBinding.toolbar.setTitle("");
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBinding.etUsername.addTextChangedListener(this);
        mBinding.etPassword.addTextChangedListener(this);
        mBinding.btnRegisterFinish.setOnClickListener(this);

        mRetrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseUrl))
                .client(new OkHttpClient.Builder()
                        .callTimeout(60, TimeUnit.SECONDS)
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .build())
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
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
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {
        mBinding.btnRegisterFinish.setEnabled(!mBinding.etUsername.getText().toString().isEmpty()
                && !mBinding.etPassword.getText().toString().isEmpty());
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        mBinding.btnRegisterFinish.setEnabled(!mBinding.etUsername.getText().toString().isEmpty()
                && !mBinding.etPassword.getText().toString().isEmpty());
    }

    @Override
    public void afterTextChanged(Editable s)
    {
        mBinding.btnRegisterFinish.setEnabled(!mBinding.etUsername.getText().toString().isEmpty()
                && !mBinding.etPassword.getText().toString().isEmpty());
    }

    @Override
    public void onClick(View v)
    {
        dispatchRegister();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (mDisposable != null && !mDisposable.isDisposed())
            mDisposable.dispose();
    }

    void dispatchRegister()
    {
        mRetrofit.create(RetrofitRequest.class)
                .register(mBinding.etUsername.getText().toString(), mBinding.etPassword.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RegisterModel>()
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
                                    RegisterModel registerModel)
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
                        Toast.makeText(RegisterActivity.this, "注册失败，请重试", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete()
                    {
                        if (!isError)
                        {
                            Intent intent = new Intent();
                            intent.putExtra("username", mBinding.etUsername.getText().toString());
                            intent.putExtra("password", mBinding.etPassword.getText().toString());
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                });
    }
}