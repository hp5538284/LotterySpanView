package com.yinghuanhang.lottery;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * 正方形FrameLayout, 拉伸方式分为以下三种: <ul> <li>STRETCH_MAX_SIZE - 按宽高最大值拉伸</li> <li>STRETCH_VERTICAL -
 * 垂直拉伸, 即高度按宽度值拉伸</li> <li>STRETCH_HORIZONTAL - 水平拉伸, 即宽度按高度值拉伸</li> </ul>
 *
 * @author WhatsAndroid
 */
public class SquareFrameLayout extends FrameLayout {
    public static final int STRETCH_MAX_SIZE = 0;
    public static final int STRETCH_VERTICAL = 1;
    public static final int STRETCH_HORIZONTAL = 2;
    public static final int STRETCH_MIN_SIZE = 3;

    private int mStretchMode = STRETCH_MAX_SIZE;

    public SquareFrameLayout(Context context) {
        super(context, null);
    }

    public SquareFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SquareFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SquareFrameLayout);
        mStretchMode = a.getInt(R.styleable.SquareFrameLayout_stretchMode, STRETCH_MAX_SIZE);
        a.recycle();
    }

    public void setStretchMode(int stretchMode) {
        if (stretchMode != mStretchMode) {
            mStretchMode = stretchMode;
            requestLayout();
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        int mode;
        int size;
        switch (mStretchMode) {
        case STRETCH_VERTICAL: {
            size = getMeasuredWidth();
            mode = MeasureSpec.getMode(widthMeasureSpec);
            break;
        }
        case STRETCH_HORIZONTAL: {
            size = getMeasuredHeight();
            mode = MeasureSpec.getMode(heightMeasureSpec);
            break;
        }
        case STRETCH_MIN_SIZE: {
            int size_w = getMeasuredWidth();
            int size_h = getMeasuredHeight();
            boolean stretchVertical = (size_w < size_h);
            size = stretchVertical ? size_w : size_h;
            mode = stretchVertical ?
                    MeasureSpec.getMode(widthMeasureSpec) : MeasureSpec.getMode(heightMeasureSpec);
            break;
        }
        default: {
            int size_w = getMeasuredWidth();
            int size_h = getMeasuredHeight();
            boolean stretchVertical = (size_w > size_h);
            size = stretchVertical ? size_w : size_h;
            mode = stretchVertical ?
                    MeasureSpec.getMode(widthMeasureSpec) : MeasureSpec.getMode(heightMeasureSpec);
            break;
        }
        }

        int measureSpec = MeasureSpec.makeMeasureSpec(size, mode);
        super.onMeasure(measureSpec, measureSpec);
    }
}
