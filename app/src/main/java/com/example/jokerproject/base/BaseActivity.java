package com.example.jokerproject.base;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.example.jokerproject.R;
import com.example.jokerproject.util.ScreenUtil;

public abstract class BaseActivity<P extends BasePresent> extends AppCompatActivity implements IVIew {

    protected P presenter;
    protected View mStatusBar;
    protected ViewGroup mBaseContentView;
    protected ViewGroup mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBaseContentView();
        setContentView(mBaseContentView);
        immersionStatusBar();
        initStatusBar();
        initContentView();
        inflateBaseView();


        presenter = createPresenter();
        if (presenter != null) presenter.attach(this);

        initView(savedInstanceState);
    }

    protected void initBaseContentView() {
        mBaseContentView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.activity_base,
                (ViewGroup) getWindow().getDecorView(), false);
    }

    protected void initContentView() {
        mContentView = (ViewGroup) LayoutInflater.from(this).inflate(getContentLayoutId(),
                (ViewGroup) getWindow().getDecorView(), false);
    }

    protected void inflateBaseView() {
        mBaseContentView.addView(mContentView);
    }

    /**
     * 隐藏状态栏 沉浸式
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void immersionStatusBar() {
        Window window = getWindow();
        View decorView = window.getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        //修改为light主体，字体变成深色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }
    }

    protected void initStatusBar() {
        mStatusBar = new StatusBarView(this);
        ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtil.getStatusBarHeight());
        mStatusBar.setLayoutParams(p);
        mStatusBar.setBackgroundColor(getResources().getColor(R.color.colorBaseBackground));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) presenter.detach();
    }

    @Override
    public void getData() {
    }

    protected abstract P createPresenter();

    protected abstract int getContentLayoutId();

    protected abstract void initView(Bundle savedInstanceState);
}
