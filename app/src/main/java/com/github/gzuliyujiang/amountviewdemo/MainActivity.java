package com.github.gzuliyujiang.amountviewdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.github.gzuliyujiang.widget.NumberAddSubButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NumberAddSubButton amountView = findViewById(R.id.amount_view);
        amountView.setTotal(999);
        amountView.setLimit(2, 99);
        amountView.setOnWarnListener(new NumberAddSubButton.OnWarnListener() {
            @Override
            public void onTotalWarning(int total) {
                showToast("超过总库存" + total);
            }

            @Override
            public void onMinLimitWarning(int min) {
                showToast("不能少于" + min);
            }

            @Override
            public void onMaxLimitWarning(int max) {
                showToast("最多可购买" + max);
            }
        });
    }

    private void showToast(String text) {
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }

}
