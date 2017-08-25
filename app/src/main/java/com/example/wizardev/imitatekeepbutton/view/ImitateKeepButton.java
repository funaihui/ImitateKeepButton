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
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.example.wizardev.imitatekeepbutton.R;
import com.example.wizardev.imitatekeepbutton.util.DPUtils;

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
    private boolean enable = true;
    public ColorStateList getRingBgColor() {
        return ringBgColor;
    }

    public void setRingBgColor(ColorStateList ringBgColor) {
        this.ringBgColor = ringBgColor;
    }

    public ColorStateList getRingColor() {
        return ringColor;
    }

    public void setRingColor(ColorStateList ringColor) {
        this.ringColor = ringColor;
    }

    public ColorStateList getCircleColor() {
        return circleColor;
    }

    public void setCircleColor(int circleColor) {

        this.circleColor = ColorStateList.valueOf(circleColor);
    }



    public void setTextColor(ColorStateList textColor) {
        this.textColor = textColor;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getRingSize() {
        return ringSize;
    }

    public void setRingSize(int ringSize) {
        this.ringSize = ringSize;
    }

    public int getRingRadius() {
        return ringRadius;
    }

    public void setRingRadius(int ringRadius) {
        this.ringRadius = ringRadius;
    }

    public int getNarrowDown() {
        return narrowDown;
    }

    public void setNarrowDown(int narrowDown) {
        this.narrowDown = narrowDown;
    }

    private String contentText;
    private int textSize;
    private ValueAnimator animator;
    private int ringSize;
    private int ringRadius;
    private int space;
    private int narrowDown;
    private int value = 0;
    private float result;
    private ValueAnimator animatorValue;
    private OnViewClick onViewClick;

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
        narrowDown = array.getDimensionPixelSize(R.styleable.ImitateKeepButton_narrow_down, 10);

        array.recycle();
        initAttributes();
        initPaintOrPath();

    }
    //当xml中没有定义时，设置默认的属性
    private void initAttributes() {
        ringBgColor = (ringBgColor != null) ? ringBgColor : ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary));
        ringColor = (ringColor != null) ? ringColor : ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark));
        circleColor = (circleColor != null) ? circleColor : ColorStateList.valueOf(getResources().getColor(R.color.colorAccent));
        textColor = (textColor != null) ? textColor : ColorStateList.valueOf(Color.WHITE);
        contentText = (contentText != null) ? contentText : "";

        ringRadius = mRadius;
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
        textPaint.setColor(textColor.getColorForState(getDrawableState(),0));
        textPaint.setTextSize(textSize);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画圆
        ringRadius = mRadius - DPUtils.dip2px(getContext(),value/2);
        mPaint.setColor(circleColor.getColorForState(getDrawableState(),0));

        canvas.drawCircle(getWidth() / 2, getHeight() / 2, ringRadius, mPaint);
        //用户按键时开始画圆环
        if (startDrawLine){
            //计算外环的半径，记得要减去外环的宽度的一半
            result = ringRadius + space +ringSize/2;
            //画完整的进度条
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, result, circleLinePaint);
           //画进度条路径
            path.reset();
            //计算画路径的矩形
            float left = getWidth()/2-result;
            float top = getHeight()/2-result;
            float right = getWidth()/2+result;
            float bottom = getHeight()/2+result;
            RectF rect = new RectF(left,top, right ,bottom);
            path.addArc(rect, -90, angle);

            //画圆环的路径
            canvas.drawPath(path,arcPaint);
        }
        canvas.drawText(contentText,getWidth()/2,getHeight()/2,textPaint);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (!enable && event.getAction()!=MotionEvent.ACTION_UP) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {

                    if (animator != null) {
                        animator.removeAllUpdateListeners();
                    }
                    animatorValue = ValueAnimator.ofInt(0, narrowDown);
                    animatorValue.setDuration(100);
                    animatorValue.setInterpolator(new LinearInterpolator());
                    animatorValue.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            value = (int) valueAnimator.getAnimatedValue();
                            if (value == narrowDown) {
                                startDrawLine = true;
                                animatorValue.removeAllUpdateListeners();
                            }
                            postInvalidate();
                        }
                    });

                    animatorValue.start();
                    angleAnimator = ValueAnimator.ofFloat(0, 360f);
                    angleAnimator.setDuration(2000);
                    angleAnimator.setInterpolator(new LinearInterpolator());
                    angleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            angle = (float) valueAnimator.getAnimatedValue();
                            if (angle == 360) {
                                angleAnimator.removeAllUpdateListeners();
                                onViewClick.onFinish(ImitateKeepButton.this);

                            }
                            postInvalidate();
                        }
                    });
                    angleAnimator.start();
            }

            break;
            case MotionEvent.ACTION_UP: {
                restoreShape();
            }
            startDrawLine = false;
            break;
        }
        return true;
    }

    private void restoreShape() {
        angleAnimator.removeAllUpdateListeners();
        animatorValue.removeAllUpdateListeners();
        animator = ValueAnimator.ofInt(value,0);
        animator.setDuration(300);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                value = (int) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        animator.start();
    }

    public interface OnViewClick{
        void onFinish(View view);
    }

    public void setOnViewClick(OnViewClick viewClick){
        this.onViewClick = viewClick;
    }

    @Override
    public void setEnabled(boolean enabled) {
       // super.setEnabled(enabled);
        this.enable = enabled;
    }
}
