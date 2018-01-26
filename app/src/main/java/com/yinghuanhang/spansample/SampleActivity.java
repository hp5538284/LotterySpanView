package com.yinghuanhang.spansample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.yinghuanhang.lottery.LotterySpanView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cao-Human on 2018/1/25
 */
public class SampleActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLotterySpan = (LotterySpanView) findViewById(R.id.lottery_span);
        findViewById(R.id.lottery_start).setOnClickListener(SampleActivity.this);
        findViewById(R.id.lottery_speed_fast).setOnClickListener(SampleActivity.this);
        findViewById(R.id.lottery_speed_middle).setOnClickListener(SampleActivity.this);
        findViewById(R.id.lottery_speed_low).setOnClickListener(SampleActivity.this);
        findViewById(R.id.lottery_damp_fast).setOnClickListener(SampleActivity.this);
        findViewById(R.id.lottery_damp_middle).setOnClickListener(SampleActivity.this);
        findViewById(R.id.lottery_damp_low).setOnClickListener(SampleActivity.this);
        onBuildingLotterySpanItems(mLotterySpan);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lottery_speed_middle: {
                mBaseSpeed = 12f;
                break;
            }
            case R.id.lottery_speed_fast: {
                mBaseSpeed = 18f;
                break;
            }
            case R.id.lottery_speed_low: {
                mBaseSpeed = 6f;
                break;
            }
            case R.id.lottery_damp_middle: {
                mLotterySpan.setDamping(4f);
                break;
            }
            case R.id.lottery_damp_fast: {
                mLotterySpan.setDamping(5f);
                break;
            }
            case R.id.lottery_damp_low: {
                mLotterySpan.setDamping(3f);
                break;
            }
            case R.id.lottery_start: {
                float target = (float) (Math.random() * 6) + mBaseSpeed;
                int index = (int) (Math.random() * mLotterySpan.getLotteries().size());
                mLotterySpan.onStarting(target);
                mLotterySpan.onStoppingIndex(index);
                Log.v("Lottery", "Stop on" + mLotterySpan.getLotteries().get(index).getName());
                break;
            }
        }
    }

    private void onBuildingLotterySpanItems(LotterySpanView span) {
        Bitmap hundred = BitmapFactory.decodeResource(getResources(), R.drawable.turntable_packet_hundred);
        Bitmap random = BitmapFactory.decodeResource(getResources(), R.drawable.turntable_packet_random);
        Bitmap ten = BitmapFactory.decodeResource(getResources(), R.drawable.turntable_packet_ten);
        Bitmap thanks = BitmapFactory.decodeResource(getResources(), R.drawable.turntable_thanks);
        Bitmap coins = BitmapFactory.decodeResource(getResources(), R.drawable.turntable_coins);
        Bitmap card = BitmapFactory.decodeResource(getResources(), R.drawable.turntable_card);
        List<LotterySpanView.Lottery> lotteries = new ArrayList<>();
        lotteries.add(new LotteryItem("100元话费红包", hundred, Color.parseColor("#FCF2D4")));
        lotteries.add(new LotteryItem("1枚金币", coins, Color.parseColor("#FCF7E8")));
        lotteries.add(new LotteryItem("10元话费红包", ten, Color.parseColor("#FCF2D4")));
        lotteries.add(new LotteryItem("10张抽奖卡", card, Color.parseColor("#FCF7E8")));
        lotteries.add(new LotteryItem("谢谢参与", thanks, Color.parseColor("#FCF2D4")));
        lotteries.add(new LotteryItem("1张抽奖卡", card, Color.parseColor("#FCF7E8")));
        lotteries.add(new LotteryItem("随机话费红包", random, Color.parseColor("#FCF2D4")));
        lotteries.add(new LotteryItem("10枚金币", coins, Color.parseColor("#FCF7E8")));
        span.setLotteries(lotteries);
    }

    private LotterySpanView mLotterySpan;
    private float mBaseSpeed = 12f;
}
