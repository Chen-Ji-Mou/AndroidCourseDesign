package com.chenjimou.androidcoursedesign.ui.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
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

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.chenjimou.androidcoursedesign.R;
import com.chenjimou.androidcoursedesign.databinding.ActivityLoginBinding;
import com.chenjimou.androidcoursedesign.inter.RetrofitRequest;
import com.chenjimou.androidcoursedesign.model.LoginModel;
import com.chenjimou.androidcoursedesign.utils.DecodeUtils;
import com.chenjimou.androidcoursedesign.utils.SharedPreferencesUtils;
import com.chenjimou.androidcoursedesign.widget.LoadAnimationDialog;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.MacSpi;

public class LoginActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener
{
    ActivityLoginBinding mBinding;
    Disposable mDisposable;
    Retrofit mRetrofit;
    LoadAnimationDialog mDialog;

    boolean isError = false;
    boolean isLogin = false;

    static final int ACTION_REGISTER = 0;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        mBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        init();
    }

    void init()
    {
        mDialog = LoadAnimationDialog.init(this, "验证中，请稍后...");

        String usernameFromCache = SharedPreferencesUtils.getInstance().getUsername();
        String passwordFromCache = SharedPreferencesUtils.getInstance().getPassword();

        if (usernameFromCache != null)
            mBinding.etUsername.setText(usernameFromCache);

        if (passwordFromCache != null)
            mBinding.etPassword.setText(passwordFromCache);

        if (usernameFromCache != null && passwordFromCache != null)
            mBinding.btnLogin.setEnabled(true);

        mBinding.etUsername.addTextChangedListener(this);
        mBinding.etPassword.addTextChangedListener(this);
        mBinding.btnLogin.setOnClickListener(this);
        mBinding.btnRegister.setOnClickListener(this);

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
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_login:
                dispatchLogin();
                break;
            case R.id.btn_register:
                isLogin = false;
                startActivityForResult(new Intent(LoginActivity.this, RegisterActivity.class), ACTION_REGISTER);
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {
        mBinding.btnLogin.setEnabled(!mBinding.etUsername.getText().toString().isEmpty()
                && !mBinding.etPassword.getText().toString().isEmpty());
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        mBinding.btnLogin.setEnabled(!mBinding.etUsername.getText().toString().isEmpty()
                && !mBinding.etPassword.getText().toString().isEmpty());
    }

    @Override
    public void afterTextChanged(Editable s)
    {
        mBinding.btnLogin.setEnabled(!mBinding.etUsername.getText().toString().isEmpty()
                && !mBinding.etPassword.getText().toString().isEmpty());
    }

    @Override
    public void onStop()
    {
        super.onStop();

        if (isLogin)
            finish();

        if (mDisposable != null && !mDisposable.isDisposed())
            mDisposable.dispose();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            @Nullable
                    Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            mBinding.etUsername.setText(data.getStringExtra("username"));
            mBinding.etPassword.setText(data.getStringExtra("password"));
        }
    }

    void dispatchLogin()
    {
        mRetrofit.create(RetrofitRequest.class)
        .login(mBinding.etUsername.getText().toString(), mBinding.etPassword.getText().toString())
        .map(new Function<LoginModel, LoginModel>()
        {
            @Override
            public LoginModel apply(
                    @NonNull
                            LoginModel loginModel) throws Exception
            {
                String token = loginModel.getData();
                String[] split = token.split("\\.");
                String payload = split[1];
                byte[] decode = DecodeUtils.decodeByBase64(payload);
                String json = new String(decode);
                JSONObject jsonObject = new JSONObject(json);
                String userId = jsonObject.getString("id");
                SharedPreferencesUtils.getInstance().saveUserId(userId);
                return loginModel;
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<LoginModel>()
        {
            @Override
            public void onSubscribe(
                    @NonNull
                            Disposable d)
            {
                mDisposable = d;
                mDialog.show();
            }

            @Override
            public void onNext(
                    @NonNull
                            LoginModel loginModel)
            {
                isError = false;
                Log.d(TAG, "onNext: token "+loginModel.getData());
                SharedPreferencesUtils.getInstance().saveToken("Bearer " + loginModel.getData());
                SharedPreferencesUtils.getInstance().saveUsername(mBinding.etUsername.getText().toString());
                SharedPreferencesUtils.getInstance().savePassword(mBinding.etPassword.getText().toString());
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
                    isLogin = true;
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "登陆失败，请重试", Toast.LENGTH_SHORT).show();
                }
                mDialog.dismiss();
            }
        });
    }
}