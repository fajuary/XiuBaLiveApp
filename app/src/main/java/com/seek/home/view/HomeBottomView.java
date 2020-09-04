package com.seek.home.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.seek.R;

import ezy.ui.view.BadgeButton;

/**
 * Created by chunyang on 2018/6/5.
 */

public class HomeBottomView extends BottomContainer {


    public HomeBottomView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HomeBottomView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeBottomView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_home_bottom, this, true);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            BadgeButton civ = (BadgeButton) getChildAt(i);
            civ.setTag(i);
            getChildAt(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    if (mOnBottomChangeListener != null) {
                        mOnBottomChangeListener.onChange(position);
                    }
                }
            });
        }
    }

    public void toggle(int position) {//1
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            BadgeButton civ = (BadgeButton) getChildAt(i);
            if (i == position) {//选中
                civ.setSelected(true);
                civ.setActivated(true);
            } else {//非选中
                civ.setSelected(false);
                if (position == 1) {
                    civ.setActivated(true);
                } else {
                    civ.setActivated(false);
                }
            }
        }
    }

    public void setBadge(int position, String count) {
        int total = getChildCount();
        if (position > total) return;
        BadgeButton badgeButton = (BadgeButton) getChildAt(position);
        if (count == null) {
            badgeButton.setBadgeVisible(false);
        } else {
            badgeButton.setBadgeText(count);
            badgeButton.setBadgeVisible(true);
        }
        badgeButton.invalidate();
    }


    public interface OnBottomChangeListener {
        void onChange(int position);
    }

    private OnBottomChangeListener mOnBottomChangeListener;

    public void setOnBottomChangeListener(OnBottomChangeListener onBottomChangeListener) {
        mOnBottomChangeListener = onBottomChangeListener;
    }
}
