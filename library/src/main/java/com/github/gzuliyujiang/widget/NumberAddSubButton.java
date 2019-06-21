package com.github.gzuliyujiang.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.gzuliyujiang.NumberAddSub.R;

/**
 * 数量增加和减少控制按钮，如购物车商品数量
 * Modified from https://github.com/qinci/NumberButton
 *
 * @author liyujiang
 * @date 2019/5/7 17:50
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class NumberAddSubButton extends LinearLayout implements View.OnClickListener, TextWatcher {
    private int total = Integer.MAX_VALUE;
    private int minLimit = 0;
    private int maxLimit = Integer.MAX_VALUE;
    private TextView tvSub;
    private TextView tvAdd;
    private EditText etResult;
    private OnWarnListener mOnWarnListener;

    public NumberAddSubButton(Context context) {
        this(context, null);
    }

    public NumberAddSubButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.layout_number_add_sub, this);

        tvSub = findViewById(R.id.tv_number_sub);
        tvSub.setOnClickListener(this);
        tvAdd = findViewById(R.id.tv_number_add);
        tvAdd.setOnClickListener(this);

        etResult = findViewById(R.id.et_number_result);
        etResult.addTextChangedListener(this);
        etResult.setOnClickListener(this);
    }

    public final TextView getSubButton() {
        return tvSub;
    }

    public final EditText getEditText() {
        return etResult;
    }

    public final TextView getAddButton() {
        return tvAdd;
    }

    public int getCurrentNumber() {
        try {
            return Integer.parseInt(etResult.getText().toString());
        } catch (NumberFormatException ignore) {
        }
        etResult.setText(String.valueOf(minLimit));
        return minLimit;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        int number = getCurrentNumber();
        if (id == R.id.tv_number_sub) {
            if (number > 1) {
                etResult.setText(String.valueOf(number - 1));
            }
        } else if (id == R.id.tv_number_add) {
            if (number < Math.min(maxLimit, total)) {
                etResult.setText(String.valueOf(number + 1));
            } else if (maxLimit > total) {
                warningForTotal();
            } else {
                warningForMaxLimit();
            }
        }
    }

    private void onNumberInput() {
        int number = getCurrentNumber();
        if (number <= minLimit) {
            tvSub.setTextColor(0xFFDCDCDC);
        } else {
            tvSub.setTextColor(0xFF333333);
        }
        if (number >= maxLimit) {
            tvAdd.setTextColor(0xFFCCCCCC);
        } else {
            tvAdd.setTextColor(0xFFFFFFFF);
        }
        if (number < minLimit) {
            warningForMinLimit();
            return;
        }

        int limit = Math.min(maxLimit, total);
        if (number > maxLimit) {
            etResult.setText(String.valueOf(limit));
            if (maxLimit > total) {
                warningForTotal();
            } else {
                warningForMaxLimit();
            }
        }
    }

    private void warningForTotal() {
        if (mOnWarnListener != null) {
            mOnWarnListener.onTotalWarning(total);
        }
    }

    private void warningForMinLimit() {
        if (mOnWarnListener != null) {
            mOnWarnListener.onMinLimitWarning(minLimit);
        }
    }

    private void warningForMaxLimit() {
        if (mOnWarnListener != null) {
            mOnWarnListener.onMaxLimitWarning(maxLimit);
        }
    }

    public NumberAddSubButton setCurrentNumber(int number) {
        if (number < minLimit) {
            etResult.setText(String.valueOf(minLimit));
            return this;
        }
        etResult.setText(String.valueOf(Math.min(Math.min(maxLimit, total), number)));
        return this;
    }

    public int getTotal() {
        return total;
    }

    public NumberAddSubButton setTotal(int total) {
        this.total = total;
        return this;
    }

    public int getMinLimit() {
        return minLimit;
    }

    public int getMaxLimit() {
        return maxLimit;
    }

    public NumberAddSubButton setLimit(int min, int max) {
        this.minLimit = Math.min(min, max);
        this.maxLimit = Math.max(min, max);
        return this;
    }

    public NumberAddSubButton setOnWarnListener(OnWarnListener onWarnListener) {
        mOnWarnListener = onWarnListener;
        return this;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        onNumberInput();
    }

    @Override
    public void afterTextChanged(Editable s) {
        try {
            etResult.setSelection(s.length());
        } catch (IndexOutOfBoundsException ignore) {
        }
    }

    public interface OnWarnListener {
        /**
         * 超过总数量
         *
         * @param total 总数量
         */
        void onTotalWarning(int total);

        /**
         * 超过最小限制数量
         *
         * @param min 最小限制数量
         */
        void onMinLimitWarning(int min);

        /**
         * 超过最大限制数量
         *
         * @param max 最大限制数量
         */
        void onMaxLimitWarning(int max);
    }

}
