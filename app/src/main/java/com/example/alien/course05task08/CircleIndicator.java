package com.example.alien.course05task08;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class CircleIndicator extends View {
    private final static float LINE_WIDTH = 5;
    private final static float ARC_DELIMITER_SIZE = 5;
    private final static float TEXT_SIZE_SCALE = 0.7f;

    private int mWidthSpecSize;
    private int mHeightSpecSize;
    private float mRadius;
    private float mCx;
    private float mCy;
    private Paint mMainPaint;
    private Paint mInnerPaint;
    private Paint mArcSpacePaint;
    private Paint mArcValuePaint;
    private Paint mTextPaint;
    private RectF mMainBounds;
    private int mMaxValue;
    private int mValue;
    private float mArcSize;
    private float mArcDelimiterSize;
    private Rect mTextBounds;
    private String valueString;
    private float mLineWidth;

    public CircleIndicator(Context context) {
        super(context);
        init(context, null);
    }

    public CircleIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        TypedArray mainTypedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleIndicator, 0, R.style.DefaultCircleIndicator);
        setMaxValue(mainTypedArray.getInteger(R.styleable.CircleIndicator_maxValue, 0));
        setValue(mainTypedArray.getInteger(R.styleable.CircleIndicator_value, 0));

        mInnerPaint = new Paint();
        mInnerPaint.setColor(Color.WHITE);
        mInnerPaint.setAntiAlias(true);
        mInnerPaint.setStyle(Paint.Style.FILL);

        mArcSpacePaint = new Paint();
        mArcSpacePaint.setColor(Color.GRAY);
        mArcSpacePaint.setAntiAlias(true);
        mArcSpacePaint.setStyle(Paint.Style.FILL);

        mArcValuePaint = new Paint();
        mArcValuePaint.setAntiAlias(true);
        mArcValuePaint.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint();

        setColor(mainTypedArray.getColor(R.styleable.CircleIndicator_color, Color.BLUE));
        setBackgroundColor(mainTypedArray.getColor(R.styleable.CircleIndicator_backgroundColor, Color.WHITE));

        mMainBounds = new RectF();

        mTextBounds = new Rect();

        mainTypedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        mHeightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        mRadius = Math.min(mWidthSpecSize, mHeightSpecSize) / 2 * 0.9f;

        mCx = mWidthSpecSize / 2;
        mCy = mHeightSpecSize / 2;

        mMainBounds.set(mCx - mRadius, mCy - mRadius, mCx + mRadius, mCy + mRadius);

        calculateArcSize();
        calculateTextSize();
        calculateLineWidth();

        setMeasuredDimension(mWidthSpecSize, mHeightSpecSize);
    }

    private void calculateTextSize() {
        float textSize = 1;
        //mTextPaint.setColor(mArcValuePaint.getColor());
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(textSize);
        while (mTextPaint.measureText(String.valueOf(mMaxValue)) <= mRadius * TEXT_SIZE_SCALE) {
            textSize += 1;
            mTextPaint.setTextSize(textSize);
        }
    }

    private void calculateArcSize() {
        mArcDelimiterSize = ARC_DELIMITER_SIZE;
        mArcSize = ARC_DELIMITER_SIZE * 2;
        do {
            if (mArcDelimiterSize > mArcSize) {
                mArcDelimiterSize = mArcDelimiterSize / 2;
            }
            mArcSize = (360f - mArcDelimiterSize * mMaxValue) / (float) mMaxValue;
        } while (mArcDelimiterSize > mArcSize);
    }

    private void calculateLineWidth() {
        mLineWidth = mRadius * 0.1f;
        if (mLineWidth < LINE_WIDTH) {
            mLineWidth = LINE_WIDTH;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(mCx, mCy, mRadius, mInnerPaint);
        Paint curPaint;

        if (mValue > 0 && mValue < mMaxValue) {
            canvas.save();
            canvas.rotate(-90, mCx, mCy);
            float curAngel = mArcDelimiterSize;
            for (int i = 0; i < mMaxValue; i++) {
                if (i < mValue) {
                    curPaint = mArcValuePaint;
                } else {
                    curPaint = mArcSpacePaint;
                }
                canvas.drawArc(mMainBounds, curAngel, mArcSize, true, curPaint);
                curAngel += mArcSize + mArcDelimiterSize;
            }
            canvas.restore();
        } else {
            if (mValue == 0) {
                curPaint = mArcSpacePaint;
            } else {
                curPaint = mArcValuePaint;
            }
            canvas.drawCircle(mCx, mCy, mRadius, curPaint);
        }

        canvas.drawCircle(mCx, mCy, mRadius - mLineWidth, mInnerPaint);


        mTextPaint.getTextBounds(valueString, 0, valueString.length(), mTextBounds);
        canvas.drawText(String.valueOf(mValue), mCx, mCy + mTextBounds.height() / 2, mTextPaint);
    }

    public void setValue(int value) {
        if (value >= 0 && value <= mMaxValue) {
            mValue = value;
        } else if (value < 0) {
            mValue = 0;
        } else {
            mValue = mMaxValue;
        }
        valueString = String.valueOf(mValue);
        invalidate();
    }

    public void setMaxValue(int value) {
        mMaxValue = value >= 2 ? value : 2;
    }

    public void setColor(int color) {
        mArcValuePaint.setColor(color);
        mTextPaint.setColor(color);
        invalidate();
    }

    public void setBackgroundColor(int color) {
        mInnerPaint.setColor(color);
        invalidate();
    }
}
