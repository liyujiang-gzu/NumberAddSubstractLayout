package com.github.gzuliyujiang.amountviewdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.github.gzuliyujiang.widget.NumberAddSubtractLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NumberAddSubtractLayout addSubButton = findViewById(R.id.number_addsubtract_button_custom_1);
        addSubButton.setTotal(9999);
        addSubButton.setLimit(2, 999);
        addSubButton.setOnWarnListener(new NumberAddSubtractLayout.OnWarnListener() {
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
