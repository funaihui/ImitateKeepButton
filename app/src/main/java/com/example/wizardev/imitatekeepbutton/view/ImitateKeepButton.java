package com.example.wizardev.imitatekeepbutton.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.example.wizardev.imitatekeepbutton.R;

/**
 * author : wizardev
 * e-mail : wizarddev@163.com
 * time   : 2017/08/25
 * desc   :
 * version: 1.0
 */
public class ImitateKeepButton extends View {
    private Paint mPaint;
    private Paint textPaint;
    private int mRadius = 200;
    private float scale = 1;
    private Paint circleLinePaint;
    private Paint arcPaint;
    private boolean startDrawLine = false;
    private float angle = 0;
    private static final String TAG = "ImitateKeepButton";
    private ValueAnimator angleAnimator;
    private Path path;
    private ColorStateList ringBgColor;
    private ColorStateList ringColor;
    private ColorStateList circleColor;
    private ColorStateList textColor;
    private String contentText;
    private int textSize;
    private ValueAnimator animator;
    private int ringSize;
    private int ringRadius;
    private int space;

    public ImitateKeepButton(Context context) {
        this(context, null);
    }

    public ImitateKeepButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImitateKeepButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ImitateKeepButton, defStyleAttr, 0);
        ringBgColor = array.getColorStateList(R.styleable.ImitateKeepButton_ring_bg_color);
        ringColor = array.getColorStateList(R.styleable.ImitateKeepButton_ring_color);
        circleColor = array.getColorStateList(R.styleable.ImitateKeepButton_circle_color);
        textColor = array.getColorStateList(R.styleable.ImitateKeepButton_content_text_color);
        contentText = array.getString(R.styleable.ImitateKeepButton_content_text);
        mRadius = array.getDimensionPixelSize(R.styleable.ImitateKeepButton_radius, mRadius);
        textSize = array.getDimensionPixelSize(R.styleable.ImitateKeepButton_content_text_size, textSize);
        ringSize = array.getDimensionPixelSize(R.styleable.ImitateKeepButton_ring_size, 10);
        space = array.getDimensionPixelSize(R.styleable.ImitateKeepButton_space, 0);

        array.recycle();
        initAttributes();
        initPaintOrPath();

    }
    //当xml中没有定义时，设置默认的属性
    private void initAttributes() {
        ringBgColor = (ringBgColor != null) ? ringBgColor : ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary));
        ringColor = (ringColor != null) ? ringColor : ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark));
        circleColor = (circleColor != null) ? circleColor : ColorStateList.valueOf(getResources().getColor(R.color.colorAccent));
        contentText = (contentText != null) ? contentText : "";

        Log.i(TAG, "space: "+space);
        ringRadius = mRadius+ space;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int resultWidth = widthSize;
        int resultHeight = heightSize;
        if (mRadius * 2 < textPaint.measureText(contentText)) {
            mRadius = (int) textPaint.measureText(contentText);
        }
        if (widthMode == MeasureSpec.AT_MOST) {
            int contentWidth =  (mRadius + space + ringSize)*2+ getPaddingLeft() + getPaddingRight();
            resultWidth = (contentWidth < widthSize) ? resultWidth : contentWidth;
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            int contentHeight = (mRadius + space + ringSize)*2 + getPaddingTop() + getPaddingBottom();
            resultHeight = (contentHeight < heightSize) ? resultHeight : contentHeight;
        }

        setMeasuredDimension(resultWidth,resultHeight);
    }

    //初始化画笔及路径
    private void initPaintOrPath() {
        mPaint = new Paint();
        mPaint.setColor(circleColor.getColorForState(getDrawableState(),0));
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        circleLinePaint = new Paint();

        circleLinePaint.setColor(ringBgColor.getColorForState(getDrawableState(),0));
        circleLinePaint.setAntiAlias(true);
        circleLinePaint.setStrokeWidth(ringSize);
        circleLinePaint.setStyle(Paint.Style.STROKE);

        arcPaint = new Paint();
        arcPaint.setColor(ringColor.getColorForState(getDrawableState(),0));
        arcPaint.setAntiAlias(true);
        arcPaint.setStrokeWidth(ringSize);
        arcPaint.setStyle(Paint.Style.STROKE);
        path = new Path();

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(textSize);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        //计算画路径的矩形
        RectF rect = new RectF(getWidth()/2-ringRadius,getHeight()/2-ringRadius,
                getWidth()/2+ringRadius,getHeight()/2+ringRadius);


        canvas.scale(scale,scale,getWidth()/2,getHeight()/2);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mRadius, mPaint);
        if (startDrawLine){
            canvas.restore();
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, ringRadius, circleLinePaint);
            path.reset();
            path.addArc(rect, -90, angle);
            canvas.drawPath(path,arcPaint);
        }
        canvas.drawText(contentText,getWidth()/2,getHeight()/2,textPaint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (animator != null) {
                    animator.removeAllUpdateListeners();
                }
                ValueAnimator animator = ValueAnimator.ofFloat(1, 0.9f);
                animator.setDuration(10);
                animator.setInterpolator(new LinearInterpolator());
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        scale = (float) valueAnimator.getAnimatedValue();
                        postInvalidate();
                    }
                });

                animator.start();
                angleAnimator = ValueAnimator.ofFloat(0, 360f);
                angleAnimator.setDuration(3000);
                angleAnimator.setInterpolator(new LinearInterpolator());
                angleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        angle = (float) valueAnimator.getAnimatedValue();
                        if (angle == 360) {
                            angleAnimator.removeAllUpdateListeners();
                            Toast.makeText(getContext(),"签到完成！",Toast.LENGTH_SHORT).show();
                        }
                        postInvalidate();
                    }
                });
                angleAnimator.start();
                startDrawLine = true;
            }

            break;
            case MotionEvent.ACTION_UP: {
                angleAnimator.removeAllUpdateListeners();
                angle=0;
                animator = ValueAnimator.ofFloat(scale,1);
                animator.setDuration(300);
                animator.setInterpolator(new LinearInterpolator());
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        scale = (float) valueAnimator.getAnimatedValue();
                        postInvalidate();
                    }
                });
                animator.start();
            }
            startDrawLine = false;
            break;
        }
        return true;
    }
}
