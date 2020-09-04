package com.xiu8.kb.model.account.detail;

import com.seek.library.EventKey;
import com.seek.library.http.SeekPObserver;
import com.xiu8.base.bus.IBusEvent;
import com.xiu8.base.mvp.model.CreateModel;
import com.xiu8.base.mvp.presenter.BasePresenter;

import java.util.List;

/**
 * Created by zhangpengfei on 2018/8/29.
 */
@CreateModel(KBSeekDetailModel.class)
public class KBSeekDetailPresenter extends BasePresenter<KBSeekDetailView, KBSeekDetailModel> {


    private KBSeekDetailPresenter.AccountInfo mAccountInfo;

    private class AccountInfo implements IBusEvent<Long> {

        @Override
        public void notifyEvent(Long data, String key) {
            if (isViewAttached()) {
                //获取k币余额
                mView.onAccountResult(data);
            }
        }

    }

    /**
     * 获取账户余额
     */
    private void getAccountInfo() {
        notify(0L, EventKey.KB.GET_ACCOUNT_INFO);
    }


    @Override
    public void onAttach(KBSeekDetailView view) {
        super.onAttach(view);
        mAccountInfo = new KBSeekDetailPresenter.AccountInfo();
        //注册获取余额
        register(EventKey.KB.RESULT_ACCOUNT_INFO, mAccountInfo);
    }


    private int pageIndex=1;
    //刷新走的位置
    public void refreshKBList(){
        pageIndex=1;
        getAccountInfo();
        getKBList(pageIndex);
    }
    //加载更多走的位置
    public void loadMoreKbList(){
        pageIndex++;
        getKBList(pageIndex);
    }

    /**
     * 页码
     *
     * @param pageIndex
     */
    public void getKBList(final int pageIndex) {
        /**
         * 如果解析String的话将得到服务器给出的原始json
         */
        mModel.getKBList(pageIndex)
                .subscribe(new SeekPObserver<SeekDetailBean>(this) {
            @Override
            public void onSuccess(SeekDetailBean kbListInfoResultBean) {

                /**
                 * 注释：如果解析Bean出错将会抛出异常：未知错误，无法解析json异常，NullPointerException等
                 */
                if (isViewAttached()) {

                    List<SeekKBInfoBean> kbListInfoList=kbListInfoResultBean.getList();
                    int pageSize=kbListInfoResultBean.getPageSize();
                    int totalPage=kbListInfoResultBean.getTotalPage();

                    if (pageIndex==1){
                        if (null!=kbListInfoList&&kbListInfoList.size()!=0){
                            mView.getRefreshKblist(kbListInfoList);
                        }else{
                            //当前为第一页并且数据列表为空表示没有数据
                            mView.showEmptyPage();
                        }
                    }else {
                        if (null != kbListInfoList && kbListInfoList.size() != 0) {

                            int kblistSize=kbListInfoList.size();
                            /**
                             * 当前列表数据小于每页数据或者当前页大于等于总页数表示没有更多数据了
                             */
                            if (kblistSize<pageSize||pageIndex>=totalPage){
                                mView.getLoadMoreKbList(kbListInfoList,true);
                            }else {
                                mView.getLoadMoreKbList(kbListInfoList,false);
                            }
                        } else {
                            //当前数据列表为空并且不是第一页的话表示没有更多数据了
                            mView.noMoreDataList();
                        }
                    }

                }
            }

            @Override
            protected void onError(int code, String message) {
                if (isViewAttached()) {
                    if (pageIndex==1){
                        mView.onKBListError(0,message);

                    }else {
                        mView.onKBListError(1,message);

                    }
                }
            }
        });


    }


}
