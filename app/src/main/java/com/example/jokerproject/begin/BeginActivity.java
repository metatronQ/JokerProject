package com.example.jokerproject.begin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.jokerproject.MyApplication;
import com.example.jokerproject.R;
import com.example.jokerproject.base.BaseActivity;
import com.example.jokerproject.custom_control.MyBeginButton;

public class BeginActivity extends BaseActivity<BeginPresent> implements BeginView {

    @Override
    protected BeginPresent createPresenter() {
        return new BeginPresent();
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_begin;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

    }

}
