package com.chenjimou.androidcoursedesign.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chenjimou.androidcoursedesign.R;
import com.chenjimou.androidcoursedesign.databinding.ActivitySharingImageSelectBinding;
import com.chenjimou.androidcoursedesign.databinding.LayoutShareEditItemBinding;
import com.chenjimou.androidcoursedesign.model.PictureFromDeviceModel;
import com.chenjimou.androidcoursedesign.ui.ShareEditItemDecoration;
import com.chenjimou.androidcoursedesign.utils.DecodeUtils;
import com.chenjimou.androidcoursedesign.utils.DisplayUtils;
import com.chenjimou.androidcoursedesign.utils.SystemBarUtil;

import java.util.ArrayList;
import java.util.List;

public class SharingImageSelectActivity extends AppCompatActivity implements View.OnClickListener
{
    ActivitySharingImageSelectBinding mBinding;
    ContentResolver mContentResolver;
    Disposable mDisposable;
    SharingImageSelectAdapter mAdapter;

    final List<PictureFromDeviceModel> dataOnUI = new ArrayList<>();
    final List<PictureFromDeviceModel> dataBySelect = new ArrayList<>();

    boolean isError = false;

    final int REQUEST_EXTERNAL_STORAGE = 1;

    static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    static final String SELECTION = MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?";

    static final String[] SELECTION_ARGS = {
            "image/jpeg",
            "image/png"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySharingImageSelectBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        init();
    }

    void init()
    {
        SystemBarUtil.setStatusBarColor(this, R.color.white);
        SystemBarUtil.setAndroidNativeLightStatusBar(this, true);

        mContentResolver = getContentResolver();

        mBinding.toolbar.setTitle("");
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBinding.recyclerview.setLayoutManager(new StaggeredGridLayoutManager(3,
                StaggeredGridLayoutManager.VERTICAL));
        mBinding.recyclerview.addItemDecoration(new ShareEditItemDecoration());

        mAdapter = new SharingImageSelectAdapter();
        mBinding.recyclerview.setAdapter(mAdapter);

        mBinding.btnSelectFinish.setOnClickListener(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        reset();
        checkPermissions();
    }

    void checkPermissions()
    {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                && !ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                requestPermissions(PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
        else
        {
            loadPictureFromDevice();
        }
    }

    void loadPictureFromDevice()
    {
        Observable.create(new ObservableOnSubscribe<Cursor>()
        {
            @Override
            public void subscribe(
                    @io.reactivex.annotations.NonNull
                            ObservableEmitter<Cursor> emitter) throws Exception
            {
                try
                {
                    Cursor result = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                            SELECTION, SELECTION_ARGS, MediaStore.Images.Media.DATE_ADDED + " desc");
                    emitter.onNext(result);
                }
                catch (Exception e)
                {
                    emitter.onError(e);
                }
                finally
                {
                    emitter.onComplete();
                }
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<Cursor>()
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
                            Cursor result)
            {
                isError = false;
                if (result.moveToFirst())
                {
                    int pictureIdIndex = result.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                    int picturePathIndex = result.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    do
                    {
                        PictureFromDeviceModel model = new PictureFromDeviceModel();

                        int id = result.getInt(pictureIdIndex);

                        byte[] bytes = result.getBlob(picturePathIndex);
                        String path = new String(bytes, 0, bytes.length - 1);

                        model.setPath(path);
                        model.setId(id);
                        dataOnUI.add(model);
                    }
                    while (result.moveToNext());
                }
                result.close();
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
                }
            }
        });
    }

    void reset()
    {
        dataOnUI.clear();
    }

    /**
     * 检查是否已选
     */
    boolean checkSelect(String path)
    {
        if (path == null || path.isEmpty())
        {
            return false;
        }
        for (PictureFromDeviceModel model : dataBySelect)
        {
            if (path.equals(model.getPath()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 反选
     */
    void reverseSelect(String path)
    {
        if (path == null || path.isEmpty())
        {
            return;
        }
        PictureFromDeviceModel del = null;
        for (PictureFromDeviceModel model : dataBySelect)
        {
            if (path.equals(model.getPath()))
            {
                del = model;
            }
        }
        dataBySelect.remove(del);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull
            String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == REQUEST_EXTERNAL_STORAGE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                loadPictureFromDevice();
            }
            else
            {
                Toast.makeText(this, "需要开启权限才能读取本机照片", Toast.LENGTH_SHORT).show();
                finish();
            }
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
    public void onStop()
    {
        super.onStop();
        if (mDisposable != null && !mDisposable.isDisposed())
            mDisposable.dispose();
    }

    @Override
    public void onClick(View v)
    {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("pictures", (ArrayList<? extends Parcelable>) dataBySelect);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    class SharingImageSelectAdapter extends RecyclerView.Adapter<SharingImageSelectAdapter.ViewHolder>
    {
        LayoutShareEditItemBinding itemBinding;

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(
                @NonNull
                        ViewGroup parent, int viewType)
        {
            itemBinding = LayoutShareEditItemBinding.inflate(getLayoutInflater(), parent, false);
            return new ViewHolder(itemBinding.getRoot());
        }

        @Override
        public void onBindViewHolder(
                @NonNull
                        ViewHolder holder, int position)
        {
            int screenWidth = DisplayUtils.getScreenWidth(SharingImageSelectActivity.this);

            StaggeredGridLayoutManager.LayoutParams layoutParams =
                    (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.height = screenWidth / 3;
            layoutParams.width = screenWidth / 3;
            holder.itemView.setLayoutParams(layoutParams);

            holder.iv_select.setVisibility(View.VISIBLE);

            Glide.with(SharingImageSelectActivity.this)
                    .load(dataOnUI.get(position).getPath())
                    .into(holder.iv_picture);
        }

        @Override
        public int getItemCount()
        {
            return dataOnUI.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            ImageView iv_picture;
            ImageView iv_select;

            public ViewHolder(
                    @NonNull
                            View itemView)
            {
                super(itemView);
                iv_picture = itemBinding.ivPicture;
                iv_select = itemBinding.ivSelect;
                itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        int position = getAdapterPosition();
                        PictureFromDeviceModel model = dataOnUI.get(position);
                        if (!checkSelect(model.getPath()))
                        {
                            dataBySelect.add(model);
                            iv_select.setBackgroundResource(R.drawable.icon_selected);
                        }
                        else
                        {
                            reverseSelect(model.getPath());
                            iv_select.setBackgroundResource(R.drawable.icon_unselected);
                        }
                        mBinding.btnSelectFinish.setEnabled(!dataBySelect.isEmpty());
                    }
                });
            }
        }
    }
}