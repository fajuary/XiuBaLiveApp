package com.seek.guide;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seek.R;
import com.seek.library.activity.SeekBaseMVPActivity;
import com.xiu8.base.mvp.presenter.CreatePresenter;
import com.xiu8.base.mvp.view.IBaseView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nice on 2018/6/15.
 */
@CreatePresenter(GuidePresenter.class)
public class GuideActivity extends SeekBaseMVPActivity<GuidePresenter> implements IBaseView, ViewPager.OnPageChangeListener {
    private ViewPager vp;
    private int []imageIdArray;//图片资源的数组
    private List<View> viewList;//图片资源的集合
    private ViewGroup vg;//放置圆点

    //实例化原点View
    private ImageView iv_point;
    private ImageView[]ivPointArray;
    private TextView mStart;//开始体验
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
    }

    @Override
    public void initViews() {
        mStart = findView(R.id.guide_start_bnt);
        //加载viewPager
        initViewPager();
        //加载底部圆点
        initPoint();
    }

    @Override
    public void initEvent() {

    }

    private void initViewPager() {
        vp = findView(R.id.guide_banner);
        //实例化图片资源
        imageIdArray = new int[]{R.drawable.guide_1,R.drawable.guide_2,R.drawable.guide_3,R.drawable.guide_4};
        viewList = new ArrayList<>();
        //获取一个Layout参数，设置为全屏
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);

        //循环创建View并加入到集合中
        int len = imageIdArray.length;
        for (int i = 0;i<len;i++){
            //new ImageView并设置全屏和图片资源
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(params);
            imageView.setBackgroundResource(imageIdArray[i]);
            //将ImageView加入到集合中
            viewList.add(imageView);
        }
        //View集合初始化好后，设置Adapter
        vp.setAdapter(new GuidePageAdapter(viewList));
        //设置滑动监听
        vp.setOnPageChangeListener(this);

    }

    private void initPoint() {
        //这里实例化LinearLayout
        vg = findView(R.id.guide_ll_point);
        //根据ViewPager的item数量实例化数组
        ivPointArray = new ImageView[viewList.size()];
        //循环新建底部圆点ImageView，将生成的ImageView保存到数组中
        int size = viewList.size();
        for (int i = 0;i<size;i++){
            iv_point = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20,20);
            params.leftMargin = 30;
            params.rightMargin = 30;

            iv_point.setLayoutParams(params);
            iv_point.setPadding(60,0,30,0);//left,top,right,bottom
            ivPointArray[i] = iv_point;
            //第一个页面需要设置为选中状态，这里采用两张不同的图片
            if (i == 0){
                iv_point.setBackgroundResource(R.drawable.guide_point_enable);
            }else{
                iv_point.setBackgroundResource(R.drawable.guide_point_normal);
            }
            //将数组中的ImageView加入到ViewGroup
            vg.addView(ivPointArray[i]);
        }
    }

    @Override
    public void onPresenterFinished() {
        super.onPresenterFinished();
        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onClick();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //循环设置当前页的标记图
        int length = imageIdArray.length;
        for (int i = 0;i<length;i++){
            ivPointArray[position].setBackgroundResource(R.drawable.guide_point_enable);
            if (position != i){
                ivPointArray[i].setBackgroundResource(R.drawable.guide_point_normal);
            }
        }
        //判断是否是最后一页，若是则显示按钮
        if (position == imageIdArray.length - 1){
            mStart.setVisibility(View.VISIBLE);
        }else {
            mStart.setVisibility(View.GONE);
        }


    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public Activity getActivity() {
        return this;
    }
}
