package com.xiu8.kb.model.mall;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiu8.base.ximageloader.XImageLoader;
import com.xiu8.kb.model.R;

/**
 * Created by guojiel on 2018/8/29.
 */

public class KBMallListAdapter extends BaseQuickAdapter<KBGoodsBean, BaseViewHolder> {

    public KBMallListAdapter() {
        super(R.layout.kb_mall_goods_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, KBGoodsBean item) {
        XImageLoader.getInstance().display((ImageView) helper.getView(R.id.icon), item.getIconUrl());
        helper.setText(R.id.name, item.getName());
        helper.setText(R.id.remark, item.getRemark());
        helper.setText(R.id.stock, String.format("%s剩余", "" + item.getStock()));
        helper.setText(R.id.saleCount, String.format("%s人兑换", "" + item.getSaleCount()));
        helper.setText(R.id.price, String.format("价值:%s", "" + item.getPrice()));
        helper.getView(R.id.exchange).setEnabled(item.isExchangeEnabled());
        helper.addOnClickListener(R.id.exchange);
    }

}
