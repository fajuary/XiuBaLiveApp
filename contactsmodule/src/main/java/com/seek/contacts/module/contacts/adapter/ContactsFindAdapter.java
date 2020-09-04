package com.seek.contacts.module.contacts.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.seek.contacts.module.R;
import com.seek.contacts.module.contacts.bean.ContactsFindBean;
import com.xiu8.base.util.TextUtils;


/**
 * Created by yangxu on 2018/6/7.
 */

public class ContactsFindAdapter extends BaseQuickAdapter<ContactsFindBean,BaseViewHolder> {

    public ContactsFindAdapter() {
        super(R.layout.contacts_item);
    }
    @Override
    protected void convert(BaseViewHolder helper, ContactsFindBean item) {
        bindContacts(helper,item);
    }
    private void bindContacts(BaseViewHolder helper,ContactsFindBean data){
        if (data.isApply()){
            helper.setText(R.id.contacts_friends_tv_invitation,"已邀请")
            .setBackgroundRes(R.id.contacts_friends_tv_invitation,R.drawable.shape_find_friends_item_apply_bg);
        }else {
            helper.setText(R.id.contacts_friends_tv_invitation,"邀请")
                    .setBackgroundRes(R.id.contacts_friends_tv_invitation,R.drawable.shape_find_friends_item_add_bg);
        }
        helper.addOnClickListener(R.id.contacts_friends_tv_invitation);
        helper.setText(R.id.contacts_friends_user_nick, TextUtils.checkName(data.getRemark(),6))
                .setText(R.id.contacts_friends_user_phone,data.getMobile()+"");
       helper.addOnClickListener(R.id.contacts_friends_tv_invitation);
    }
}
