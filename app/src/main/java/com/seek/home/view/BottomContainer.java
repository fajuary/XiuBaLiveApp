package com.seek.home.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.xiu8.base.util.DensityUtils;

/**
 * Created by chunyang on 2018/6/4.
 */

public class BottomContainer extends RelativeLayout {

    private final int DEFAULT_HEIGHT = DensityUtils.dip2px(getContext(), 50);

    public BottomContainer(@NonNull Context context) {
        super(context);
    }

    public BottomContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(DEFAULT_HEIGHT, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, newHeightMeasureSpec);
    }
}
