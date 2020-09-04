package com.xiu8.kb.model.account.detail.ui;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiu8.kb.model.R;
import com.xiu8.kb.model.account.detail.SeekKBInfoBean;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by zhangpengfei on 2018/8/29.
 * k币获取详情列表adapter
 */

public class KBSeekListAdapter extends BaseQuickAdapter<SeekKBInfoBean, KBSeekListAdapter.KBSeekListViewHolder> {


    public KBSeekListAdapter(@Nullable List<SeekKBInfoBean> data) {
        super(R.layout.recycleview_kbinfo_itemlayout, data);
    }


    @Override
    protected void convert(KBSeekListAdapter.KBSeekListViewHolder viewHolder, SeekKBInfoBean kbListInfo) {

        if (null!=kbListInfo){
            //消费描述
            String remark=kbListInfo.getRemark();
            //消费时间戳
            long timeLon=kbListInfo.getTime();
            //消费金额
            int amount=kbListInfo.getAmount();


//            TimeUtils

            String showTimeStr=formatYearTimeFromLong(timeLon,"yyyy.MM.dd");
            viewHolder.finishTimeTxt.setText(showTimeStr);
            if (TextUtils.isEmpty(remark)){
                remark="任务获取";
            }
            viewHolder.getTypeNameTxt.setText(remark);
            //0=收入，1=支出
            int type=kbListInfo.getType();
            if (type==0){
                viewHolder.getloseMoneyTxt.setText("+ "+amount);
                viewHolder.getloseMoneyTxt.setTextColor(Color.parseColor("#52B800"));
            }else {
                viewHolder.getloseMoneyTxt.setText("- "+amount);
                viewHolder.getloseMoneyTxt.setTextColor(Color.parseColor("#FF6262"));
            }
        }

    }

    /**
     * 获取特定的时间格式
     * @param timeLon
     * @param timeFormat
     * @return
     */
    public  String formatYearTimeFromLong(long timeLon,String timeFormat) {
        //为0表示当前时间戳（毫秒）
        if (timeLon==0){
            timeLon=System.currentTimeMillis();
        }
        SimpleDateFormat format = new SimpleDateFormat(timeFormat);
        return format.format(timeLon);
    }
    class KBSeekListViewHolder extends BaseViewHolder {
        TextView getTypeNameTxt;
        TextView finishTimeTxt;
        TextView getloseMoneyTxt;

        public KBSeekListViewHolder(View view) {
            super(view);
            getTypeNameTxt=getView(R.id.reycleview_kbinfo_getTypeNameTxt);
            finishTimeTxt=getView(R.id.reycleview_kbinfo_finishTimeTxt);
            getloseMoneyTxt=getView(R.id.reycleview_kbinfo_getloseMoneyTxt);

        }
    }


}
