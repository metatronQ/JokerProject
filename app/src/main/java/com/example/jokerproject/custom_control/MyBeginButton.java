package com.example.jokerproject.custom_control;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.nfc.Tag;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.jokerproject.table_board.TableActivity;

public class MyBeginButton extends View {

    public final String TAG = "My";

    Context mContext;
    int mHeight;
    int mWidth;
    int mLeft;
    int mTop;
    int mRight;
    int mBottom;
    ViewGroup mViewGroup;
    Paint mPaint;

    public MyBeginButton(Context context) {
        super(context);
    }

    public MyBeginButton(Context context,AttributeSet attributeSet){
        super(context,attributeSet);
        this.mContext = context;
        initPaint();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);


        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initPaint();
        RectF rectF = new RectF(mWidth/4,mHeight/4,mWidth/4*3,mHeight/4*3);
        canvas.drawRoundRect(rectF,10,10,mPaint);
        mPaint.setTextSize(80);
        mPaint.setColor(Color.BLACK);
        canvas.drawText("点击开始",mWidth/9*4,mHeight/5*3,mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                Intent intent = new Intent(mContext, TableActivity.class);
                mContext.startActivity(intent);
                break;
                default:
                    break;
        }

        return true;
    }

    public void initPaint(){
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#FBC02D"));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(10f);
    }

}
