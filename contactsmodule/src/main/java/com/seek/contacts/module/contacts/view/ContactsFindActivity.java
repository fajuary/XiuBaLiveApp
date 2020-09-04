package com.seek.contacts.module.contacts.view;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.seek.ann.AnnHelper;
import com.seek.contacts.module.R;
import com.seek.contacts.module.ann.ANNContacts;
import com.seek.contacts.module.contacts.adapter.ContactsFindAdapter;
import com.seek.contacts.module.contacts.bean.ContactsFindBean;
import com.seek.contacts.module.contacts.bean.ContactsInfoBean;
import com.seek.contacts.module.contacts.presenter.ContactsFindPresenter;
import com.seek.library.user.UserHelp;
import com.seek.library.widget.dialog.GaussActivity;
import com.seek.library.widget.toast.Toast;
import com.xiu8.base.analysis.json.JsonUtils;
import com.xiu8.base.mvp.presenter.CreatePresenter;
import com.xiu8.base.util.DensityUtils;
import com.xiu8.base.util.KeyBoardUtils;

import org.json.JSONObject;

import java.util.List;

@CreatePresenter(ContactsFindPresenter.class)
public class ContactsFindActivity extends GaussActivity<ContactsFindPresenter> implements IContactsFindView {

    private RecyclerView mContactsFriendsRlv;
    private ContactsFindAdapter mAdapter;
    private EditText mSearchEdi;
    private TextView mCleanTv;
    private ImageView mCloseIv;
    private TextView mHeadView;
    private TextView mHeadEmptyView;
    private int mType;//0=精确，1=模糊
    private int pageIndex = 1;
    private int mClickPosition;
    private String mSearchContent = "";
    private String msgContent;
    private int mTotalCount;
    private SmartRefreshLayout mRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_activity_contacts);
    }


    @Override
    public void initViews() {
        mContactsFriendsRlv = (RecyclerView) findViewById(R.id.contacts_friends_rlv);
        mSearchEdi = (EditText) findViewById(R.id.contacts_friends_edi_search);
        mCleanTv = (TextView) findViewById(R.id.contacts_friends_tv_clean);
        mCloseIv = (ImageView) findViewById(R.id.contacts_friends_iv_close);
        mAdapter = new ContactsFindAdapter();
        mContactsFriendsRlv.setAdapter(mAdapter);
        mContactsFriendsRlv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter.addHeaderView(getHeadView());
        mAdapter.addHeaderView(getHeadEmptyView());
        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.contacts_friends_refresh);
        mRefreshLayout.setRefreshFooter(new ClassicsFooter(this));
        mRefreshLayout.setEnableRefresh(false);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onPresenterFinished() {
        mPresenter.checkUpload(this, pageIndex, mSearchContent);
    }

    @Override
    public void initEvent() {
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.contacts_friends_tv_invitation) {
                    mClickPosition = position;
                    mAdapter.setData(position,mAdapter.getItem(position).setApply(true));
                    if (TextUtils.isEmpty(msgContent)) {
                        mPresenter.getInvitationContent();
                    } else {
                        sendMsg(mAdapter.getItem(mClickPosition));
                    }
                }
            }
        });
        mSearchEdi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = charSequence.toString();
                update(s.trim());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mSearchEdi.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    String content = v.getText().toString().trim();
                    if (!TextUtils.isEmpty(content)) {
                        mSearchContent=content;
                        pageIndex=1;
                        mPresenter.getContactsList(pageIndex, mSearchContent,false);
                        KeyBoardUtils.hideKeyboard(ContactsFindActivity.this);

                    }
                    return true;
                }
                return false;
            }
        });

        mCleanTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchEdi.setText("");
            }
        });
        mCloseIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnnHelper.getInstance().onAnnMsg(ANNContacts.PT_CONTACTS,ANNContacts.ADDRESS_BOOK_PAGE_CLOSE);
                finish();
            }
        });
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                if (mTotalCount>mAdapter.getData().size()){
                    mPresenter.getContactsList(++pageIndex,mSearchContent,true);
                }else {
                    Toast.showToast("没有更多数据");
                    mRefreshLayout.finishLoadMore();
                }

            }

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {

            }
        });
    }

    private void update(String content) {
        if (!TextUtils.isEmpty(content)) {
            mCleanTv.setVisibility(View.VISIBLE);
            mCloseIv.setVisibility(View.GONE);
        } else {
            mCleanTv.setVisibility(View.GONE);
            mCloseIv.setVisibility(View.VISIBLE);
        }
    }

    private View getHeadView(){
        mHeadView=new TextView(this);
        mHeadView.setTextColor(Color.parseColor("#FFFFFF"));
        ViewGroup.LayoutParams params=new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        int padding = DensityUtils.dip2px(this, 15);
        int paddingHeight = DensityUtils.dip2px(this, 10);
        mHeadView.setPadding(padding,paddingHeight,0,paddingHeight);
        mHeadView.setLayoutParams(params);
        mHeadView.setText("通讯录好友");
        mHeadView.setGravity(Gravity.LEFT);
        return mHeadView;
    }
    private View getHeadEmptyView(){
        mHeadEmptyView=new TextView(this);
        mHeadEmptyView.setTextColor(Color.parseColor("#FFFFFF"));
        ViewGroup.LayoutParams params=new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        int padding = DensityUtils.dip2px(this, 15);
        int paddingHeight = DensityUtils.dip2px(this, 10);
        mHeadEmptyView.setPadding(padding,paddingHeight,0,paddingHeight);
        mHeadEmptyView.setLayoutParams(params);
        return mHeadEmptyView;
    }

    @Override
    public void onContactsList(ContactsInfoBean infoBean,boolean isLoadMore) {
        mTotalCount =infoBean.getCount();
        List<ContactsFindBean> list = infoBean.getList();
        if (isLoadMore){
            mAdapter.addData(list);
            mRefreshLayout.finishLoadMore();
        }else {
            if (list!=null && list.size()>0){
                mHeadEmptyView.setVisibility(View.GONE);
            }else {
                mHeadEmptyView.setText("没有搜索到你要找的人啊！");
                mHeadEmptyView.setGravity(Gravity.CENTER);
                mHeadEmptyView.setVisibility(View.VISIBLE);
            }
            mAdapter.replaceData(list);
        }


    }

    @Override
    public void onInvitationSuccess(String msgContent) {
        this.msgContent = msgContent;
        sendMsg(mAdapter.getItem(mClickPosition));
    }

    @Override
    public void onUploadStart() {
        mHeadEmptyView.setText("正在同步通讯录数据...");
        mHeadEmptyView.setGravity(Gravity.CENTER);
        mHeadEmptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUploadFinish() {


    }

    @Override
    public void hideLoading() {
        mRefreshLayout.finishLoadMore();
    }

    private void sendMsg(final ContactsFindBean dataBean) {
        String content = UserHelp.getInstance().getUserBean().getNick() + " 在seek里面等你好久拉,快来玩吧,下载地址：" + msgContent;
        sendMsg(dataBean.getMobile(), content);
    }


    private void sendMsg(String number, String content) {
        if (TextUtils.isEmpty(number)) {
            Toast.showToast("请输入手机号");
            return;
        }
        if (TextUtils.isEmpty(content)) {
            Toast.showToast("请输入内容");
            return;
        }
        JSONObject jsonObject=new JSONObject();
        JsonUtils.putValue(jsonObject,"number",number);
        AnnHelper.getInstance().onAnnMsg(ANNContacts.PT_CONTACTS,ANNContacts.ADDRESS_BOOK_PAGE_INVITATION,jsonObject);
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
        sendIntent.setData(Uri.parse("smsto:" + number));
        sendIntent.putExtra("sms_body", content);
        startActivity(sendIntent);
    }
}
