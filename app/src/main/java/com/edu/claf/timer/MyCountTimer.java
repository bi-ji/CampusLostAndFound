package com.edu.claf.timer;

import android.os.CountDownTimer;
import android.widget.Button;

public class MyCountTimer extends CountDownTimer {
    private Button btngetVertify;
    public MyCountTimer(long millisInFuture, long countDownInterval,Button btngetVertify) {
        super(millisInFuture, countDownInterval);
        this.btngetVertify = btngetVertify;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        btngetVertify.setText((millisUntilFinished/1000)+"秒后重发");
    }

    @Override
    public void onFinish() {
        btngetVertify.setText("重新发送验证码");
    }
}
