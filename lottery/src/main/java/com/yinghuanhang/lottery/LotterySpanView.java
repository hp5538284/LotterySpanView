package com.yinghuanhang.lottery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cao-Human on 2018/1/19
 */

public class LotterySpanView extends View implements Runnable {
    public LotterySpanView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        mDensity = metrics.density;
        mBackground.setColor(Color.parseColor("#FFEDB6"));  // 背景画笔
        mBackground.setAntiAlias(true);
        mName.setColor(Color.parseColor("#CF7401"));    // 文字画笔
        mName.setTextSize(12f * mDensity);
        mName.setAntiAlias(true);
    }

    public LotterySpanView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LotterySpanView(Context context) {
        this(context, null, 0);
    }

    @Override
    protected void onSizeChanged(int width, int height, int w, int h) {
        super.onSizeChanged(width, height, w, h);
        if (width != w && width > 0) {
            mCenterY = (float) height / 2f;
            mCenterX = (float) width / 2f;
            mRadius = width;
            mSquare.set(0, 0, width, height);
            mCircle.set(32f * mDensity, 32f * mDensity, width - 32 * mDensity, height - 32 * mDensity);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(LotterySpanView.this);
        mTarget = 0;
        mSpeed = 0;
    }

    private RectF mSquare = new RectF(), mCircle = new RectF(); // 视图区域，圆盘区域
    private Paint mBackground = new Paint();    // 圆盘背景画笔
    private float mCenterX, mCenterY, mRadius;  // 圆盘的中心位置、直径
    private float mCurrent = 0f, mSweep = 360f;
    private float mRectify = -90f;
    private float mDensity = 2f;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mCenterX != 0 && mCenterY != 0) {
            canvas.drawCircle(mCenterX, mCenterY, mRadius / 2, mBackground);
        }
        for (int index = 0; index < mLotteries.size(); index++) {
            float angle = mCurrent + mRectify + mSweep * index - mSweep / 2;
            Lottery lottery = mLotteries.get(index);
            onDrawingSpanText(canvas, lottery, angle);
        }
        for (int index = 0; index < mLotteries.size(); index++) {
            float angle = mCurrent + mSweep * index;
            Lottery lottery = mLotteries.get(index);
            onDrawingIcon(canvas, lottery, angle);
        }
    }

    private float mTarget = 0f;
    private float mAcceleration = 10f, mSpeed = 0f;
    private float mDamping = 4f;
    private boolean mIsRolling = false;
    private static final int mDelay = 16;

    /**
     * 设置阻尼系数
     *
     * @param damping
     */
    public void setDamping(float damping) {
        mDamping = damping;
    }

    public void onStoppingIndex(final int targetIndex) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                onStop(targetIndex);
            }
        }, 2400);
    }

    /**
     * 开始旋转
     *
     * @param targetSpeed 本次要达到的最大旋转速度
     */
    public void onStarting(float targetSpeed) {
        if (mTarget == 0 && mSpeed == 0) {
            mAcceleration = Math.abs(targetSpeed - mSpeed) / 100f;  // 设定1600秒达到最大速度，算出加速度
            mTarget = targetSpeed;
            mIsRolling = true;
            postDelayed(LotterySpanView.this, mDelay);
        }
    }

    public void onStop(int targetIndex) {
        removeCallbacks(LotterySpanView.this);
        mIsRolling = false;
        // 根据速度计算还要转动多少圈，外加当前奖项起始角度，奖项内随机角度
        int times = (int) (mSpeed / mDamping) * 360 + 360;
        float random = mSweep * (float) (Math.random() - 0.5d) * 0.45f;
        float distance = times - mSweep * targetIndex + random;
        float n = 2f * (distance - mCurrent) / mSpeed;
        mAcceleration = mSpeed / (n - 1);
        mTarget = 0;
        mIsRolling = true;
        run();
    }

    @Override
    public void run() {
        if (!mIsRolling) {
            return;
        }
        mCurrent = (mCurrent + mSpeed) % 360;
        invalidate();   // 转盘转动一次
        if (mSpeed < mTarget && mSpeed + mAcceleration > mTarget) {
            mSpeed = mTarget;   // 速度比目标速度小，并且不足一次加速
            postDelayed(LotterySpanView.this, mDelay);
            return;
        }
        if (mSpeed > mTarget && mSpeed - mAcceleration < mTarget) {
            mSpeed = mTarget;   // 速度比目标速度大，并且不足一次加速
            postDelayed(LotterySpanView.this, mDelay);
            return;
        }
        if (mSpeed < mTarget) { // 速度比目标速度小
            mSpeed = mSpeed + mAcceleration;
            postDelayed(LotterySpanView.this, mDelay);
            return;
        }
        if (mSpeed > mTarget) { // 速度比目标速度大
            mSpeed = mSpeed - mAcceleration;
            postDelayed(LotterySpanView.this, mDelay);
            return;
        }
        if (mSpeed > 0) {
            postDelayed(LotterySpanView.this, mDelay);
        }
    }

    private Paint mSpan = new Paint();      // 奖项背景画笔
    private Paint mName = new Paint();      // 奖品名称画笔
    private Matrix mMatrix = new Matrix();  // 奖项图标绘制的位置矩阵

    /**
     * 绘制奖项图标
     *
     * @param canvas  画布对象
     * @param lottery 奖项
     * @param angle   奖项角度
     */
    private void onDrawingSpanText(Canvas canvas, Lottery lottery, float angle) {
        mSpan.setColor(lottery.getBackground());
        canvas.drawArc(mCircle, angle, mSweep, true, mSpan);    // 绘制圆盘奖项背景色

        double radius = mCircle.width() * Math.PI / mLotteries.size();  // 计算弧长
        float measureText = mName.measureText(lottery.getName()) / 2;   // 文字的长短
        float horizontal = (float) (radius / 2) - measureText;      // 水平偏移量
        float vertical = mName.ascent();            // 竖直偏移量可以自定义
        Path path = new Path();
        path.addArc(mCircle, angle, mSweep);        // 文字的路径
        canvas.drawTextOnPath(lottery.getName(), path, horizontal, vertical, mName);
    }

    /**
     * 绘制奖项图标
     *
     * @param canvas  画布对象
     * @param lottery 奖项
     * @param angle   奖项角度
     */
    private void onDrawingIcon(Canvas canvas, Lottery lottery, float angle) {
        double radian = (double) (angle + mRectify) / 180d * Math.PI;
        double x = mCenterX + mRadius / 4 * Math.cos(radian);
        double y = mCenterY + mRadius / 4 * Math.sin(radian);
        Bitmap bitmap = lottery.getIconImage();
        float dx = (float) x - (float) bitmap.getWidth() / 2f;
        float dy = (float) y - (float) bitmap.getHeight() / 2f;
        mMatrix.reset();
        mMatrix.postRotate(angle, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        mMatrix.postTranslate(dx, dy);
        canvas.drawBitmap(bitmap, mMatrix, null);
    }

    /**
     * 设置可抽奖项目
     *
     * @param list 奖项
     */
    public void setLotteries(List<Lottery> list) {
        mLotteries = list;
        mSweep = 360f / list.size();
        if (mCenterX != 0 && mCenterY != 0) {
            invalidate();
        }
    }

    private List<Lottery> mLotteries = new ArrayList<>();   // 省去非空判断

    public List<Lottery> getLotteries() {
        return mLotteries;
    }

    public interface Lottery {
        String getName();

        Bitmap getIconImage();

        boolean isEquals(Lottery lottery);

        int getBackground();
    }
}