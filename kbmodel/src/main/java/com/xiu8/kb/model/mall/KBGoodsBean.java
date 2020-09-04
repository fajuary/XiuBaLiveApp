package com.xiu8.kb.model.mall;

import com.xiu8.base.BaseBean;

/**
 * Created by guojiel on 2018/8/29.
 * kb 兑换中心 商品bean
 */

public class KBGoodsBean extends BaseBean {

    private long goodsId;//": "long,商品Id",
    private String name;//": "string,商品名称",
    private long price;//": "long,价格",
    private String remark;//": "string,商品描述",
    private int stock;//": "integer,库存",
    private int saleCount;//": "integer,销量"
    private String cover;//商品图片

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(int saleCount) {
        this.saleCount = saleCount;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getIconUrl() {
        return getCover();
    }

    /**
     * 是否有库存
     */
    public boolean isExchangeEnabled(){
        return getStock() > 0;
    }

    /**
     * 自己的kb是否足够兑换该商品
     * @param selfKb 自己的kb余额
     * @return
     */
    public boolean isKbEnough(long selfKb){
        return selfKb >= getPrice();
    }

    /**
     * 自己兑换成功 库存-1 销量+1
     */
    public void selfBuyOne() {
        if(stock > 0){
            --stock;
        }
        ++saleCount;
    }

}
