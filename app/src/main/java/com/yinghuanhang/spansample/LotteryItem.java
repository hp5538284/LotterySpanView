package com.yinghuanhang.spansample;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.yinghuanhang.lottery.LotterySpanView;

/**
 * Created by Cao-Human on 2018/1/22
 */

public class LotteryItem implements LotterySpanView.Lottery {
    public LotteryItem(String name, Bitmap bitmap, int color) {
        mName = name;
        mBitmap = bitmap;
        mBackground = color;
    }

    private String mName;
    private Bitmap mBitmap;
    private int mBackground;

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public Bitmap getIconImage() {
        return mBitmap;
    }

    @Override
    public int getBackground() {
        return mBackground;
    }

    @Override
    public boolean isEquals(LotterySpanView.Lottery lottery) {
        return TextUtils.equals(mName, lottery.getName());
    }
}
