package com.github.gzuliyujiang.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.gzuliyujiang.NumberAddSub.R;

import java.util.Locale;

/**
 * 数量增加和减少控制按钮，如购物车商品数量
 * <p>
 * 基于 https://github.com/qinci/NumberButton 做了一些优化及增强
 *
 * @author liyujiang
 * @date 2019/5/7 17:50
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class NumberAddSubtractButton extends LinearLayout implements View.OnClickListener, TextWatcher {
    private static final int NOT_SET = -1;

    private int total = Integer.MAX_VALUE;
    private int minLimit = 1;
    private int maxLimit = Integer.MAX_VALUE;
    private LinearLayout llContainer;
    private TextView tvSubtract;
    private TextView tvAdd;
    private EditText etNumber;
    private OnWarnListener mOnWarnListener;

    private int buttonNormalTextColor;
    private int buttonDisableTextColor;

    public NumberAddSubtractButton(Context context) {
        this(context, null);
    }

    public NumberAddSubtractButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.layout_number_addsubtract, this);
        llContainer = findViewById(R.id.ll_number_container);
        tvSubtract = findViewById(R.id.tv_number_subtract);
        tvSubtract.setOnClickListener(this);
        tvAdd = findViewById(R.id.tv_number_add);
        tvAdd.setOnClickListener(this);
        etNumber = findViewById(R.id.et_number_result);
        etNumber.addTextChangedListener(this);
        etNumber.setOnClickListener(this);

        if (attrs != null) {
            initAttrs(context, attrs);
        }
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NumberAddSubtractButton);
        boolean editable = typedArray.getBoolean(R.styleable.NumberAddSubtractButton_editable, true);
        int defaultNumber = typedArray.getInteger(R.styleable.NumberAddSubtractButton_defaultNumber, minLimit);
        int buttonWidth = typedArray.getDimensionPixelSize(R.styleable.NumberAddSubtractButton_buttonWidth, NOT_SET);
        int buttonTextSize = typedArray.getDimensionPixelSize(R.styleable.NumberAddSubtractButton_buttonTextSize, NOT_SET);
        buttonNormalTextColor = typedArray.getColor(R.styleable.NumberAddSubtractButton_buttonNormalTextColor, 0xFF333333);
        buttonDisableTextColor = typedArray.getColor(R.styleable.NumberAddSubtractButton_buttonDisableTextColor, 0xFFDCDCDC);
        int numberWidth = typedArray.getDimensionPixelSize(R.styleable.NumberAddSubtractButton_numberWidth, NOT_SET);
        int numberTextSize = typedArray.getDimensionPixelSize(R.styleable.NumberAddSubtractButton_numberTextSize, NOT_SET);
        int numberTextColor = typedArray.getColor(R.styleable.NumberAddSubtractButton_numberTextColor, 0xFF333333);
        int borderDrawableRes = typedArray.getResourceId(R.styleable.NumberAddSubtractButton_borderDrawable, NOT_SET);
        int dividerDrawableRes = typedArray.getResourceId(R.styleable.NumberAddSubtractButton_dividerDrawable, NOT_SET);
        int buttonLeftDrawableRes = typedArray.getResourceId(R.styleable.NumberAddSubtractButton_buttonLeftDrawable, NOT_SET);
        int buttonRightDrawableRes = typedArray.getResourceId(R.styleable.NumberAddSubtractButton_buttonRightDrawable, NOT_SET);
        typedArray.recycle();

        setEditable(editable);
        tvSubtract.setTextColor(buttonDisableTextColor);
        tvAdd.setTextColor(buttonDisableTextColor);
        etNumber.setTextColor(numberTextColor);
        if (buttonTextSize > 0) {
            tvSubtract.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonTextSize);
            tvAdd.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonTextSize);
        }
        if (numberTextSize > 0) {
            etNumber.setTextSize(TypedValue.COMPLEX_UNIT_PX, numberTextSize);
        }
        if (buttonWidth > 0) {
            LayoutParams params = new LayoutParams(buttonWidth, LayoutParams.MATCH_PARENT);
            tvSubtract.setLayoutParams(params);
            tvAdd.setLayoutParams(params);
        }
        if (numberWidth > 0) {
            LayoutParams params = new LayoutParams(numberWidth, LayoutParams.MATCH_PARENT);
            etNumber.setLayoutParams(params);
        }
        if (borderDrawableRes > 0) {
            llContainer.setBackgroundResource(borderDrawableRes);
        }
        if (dividerDrawableRes > 0) {
            Drawable drawable;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable = context.getDrawable(dividerDrawableRes);
            } else {
                drawable = getResources().getDrawable(dividerDrawableRes);
            }
            llContainer.setDividerDrawable(drawable);
        }
        if (buttonLeftDrawableRes > 0) {
            tvSubtract.setBackgroundResource(buttonLeftDrawableRes);
        }
        if (buttonRightDrawableRes > 0) {
            tvAdd.setBackgroundResource(buttonRightDrawableRes);
        }
        if (defaultNumber != NOT_SET) {
            changeInputText(defaultNumber);
        }
    }

    public Integer getCurrentNumber() {
        try {
            String s = etNumber.getText().toString();
            if (TextUtils.isEmpty(s)) {
                return null;
            }
            return Integer.parseInt(s);
        } catch (NumberFormatException ignore) {
        }
        changeInputText(minLimit);
        return minLimit;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Integer number = getCurrentNumber();
        if (number == null) {
            return;
        }
        if (id == R.id.tv_number_subtract) {
            if (number > 1 && number > minLimit) {
                changeInputText(number - 1);
            }
        } else if (id == R.id.tv_number_add) {
            if (number < Math.min(maxLimit, total)) {
                changeInputText(number + 1);
            } else if (maxLimit > total) {
                warningForTotal();
            } else {
                warningForMaxLimit();
            }
        }
    }

    private void onNumberInput() {
        Integer number = getCurrentNumber();
        if (number == null) {
            tvSubtract.setTextColor(buttonDisableTextColor);
            tvAdd.setTextColor(buttonDisableTextColor);
            return;
        }
        if (number <= minLimit) {
            tvSubtract.setTextColor(buttonDisableTextColor);
        } else {
            tvSubtract.setTextColor(buttonNormalTextColor);
        }
        if (number >= maxLimit) {
            tvAdd.setTextColor(buttonDisableTextColor);
        } else {
            tvAdd.setTextColor(buttonNormalTextColor);
        }
        if (number < minLimit) {
            changeInputText(minLimit);
            warningForMinLimit();
            return;
        }

        int limit = Math.min(maxLimit, total);
        if (number > maxLimit) {
            changeInputText(limit);
            if (maxLimit > total) {
                warningForTotal();
            } else {
                warningForMaxLimit();
            }
        }
    }

    private void changeInputText(int number) {
        String s = String.valueOf(number);
        etNumber.setText(s);
        try {
            etNumber.setSelection(s.length());
        } catch (IndexOutOfBoundsException ignore) {
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

    public void setEditable(boolean editable) {
        if (editable) {
            etNumber.setFocusable(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                etNumber.setKeyListener(new DigitsKeyListener(Locale.getDefault()));
            } else {
                etNumber.setKeyListener(new DigitsKeyListener());
            }
        } else {
            etNumber.setFocusable(false);
            etNumber.setKeyListener(null);
        }
    }

    public NumberAddSubtractButton setCurrentNumber(int number) {
        if (number < minLimit) {
            changeInputText(minLimit);
            return this;
        }
        changeInputText(Math.min(Math.min(maxLimit, total), number));
        return this;
    }

    public int getTotal() {
        return total;
    }

    public NumberAddSubtractButton setTotal(int total) {
        this.total = total;
        return this;
    }

    public int getMinLimit() {
        return minLimit;
    }

    public int getMaxLimit() {
        return maxLimit;
    }

    public NumberAddSubtractButton setLimit(int min, int max) {
        this.minLimit = Math.min(min, max);
        this.maxLimit = Math.max(min, max);
        return this;
    }

    public NumberAddSubtractButton setOnWarnListener(OnWarnListener onWarnListener) {
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
