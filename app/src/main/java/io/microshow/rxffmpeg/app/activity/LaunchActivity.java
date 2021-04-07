package io.microshow.rxffmpeg.app.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import java.util.concurrent.TimeUnit;

import io.microshow.rxffmpeg.app.R;
import io.microshow.rxffmpeg.app.utils.Utils;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.internal.observers.BlockingBaseObserver;
import rxpermissions3.RxPermissions;

/**
 * 启动页
 * Created by Super on 2019/12/6.
 */
public class LaunchActivity extends BaseActivity {

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    //权限
    private RxPermissions rxPermissions = null;

    //需要申请的权限，必须先在AndroidManifest.xml有声明，才可以动态获取权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataBindingUtil.setContentView(this, R.layout.activity_launch);

        rxPermissions = new RxPermissions(this);

        rxPermissions.request(PERMISSIONS_STORAGE)
                .subscribe(new BlockingBaseObserver<Boolean>() {
                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        if (aBoolean) {// 用户同意了权限
                            gotoMainAct();
                        } else {//用户拒绝了权限
                            Toast.makeText(LaunchActivity.this, "您拒绝了权限，请往设置里开启权限", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });
    }

    private void gotoMainAct() {
        mCompositeDisposable.add(Flowable.timer(3000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(next -> {
                    startActivity(new Intent(LaunchActivity.this, MainActivity.class));
                    finish();
                }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.fixInputMethodManagerLeak(this);
        mCompositeDisposable.clear();
    }

}
