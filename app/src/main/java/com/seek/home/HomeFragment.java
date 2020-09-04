package com.seek.home;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seek.R;
import com.seek.ann.AnnHelper;
import com.seek.hotchat.AnnReport;
import com.seek.hotchat.bundle.main.view.HotChatHomeFragment;
import com.seek.msgbundle.ann.AnnMsg;
import com.seek.msgbundle.ann.MsgAnnHelper;
import com.seek.msgbundle.main.view.MsgHomeFragment;
import com.seek.recommend.bundle.RecommendFragment;
import com.seek.recommend.bundle.ann.AnnRecommend;
import com.xiu8.base.LazyFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends LazyFragment {


    private static final String POSITION = "position";

    private int mPosition;

    public static HomeFragment newFragment(int position) {
        HomeFragment fragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(POSITION, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void initViews(View view) {
        super.initViews(view);
    }

    @Override
    public void onLazyCreate(Bundle savedInstanceState) {
        super.onLazyCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mPosition = bundle.getInt(POSITION);

        getFragment(mPosition);

    }


    @Override
    public void onLazyResume() {
        super.onLazyResume();
        if (mPosition == 0) {
            AnnHelper.getInstance().onAnnMsg(AnnRecommend.PT, AnnRecommend.PAGE);
        } else if (mPosition == 1) {
            AnnHelper.getInstance().onAnnMsg(AnnReport.HotChat.Pt, AnnReport.HotChat.Page);
        } else if (mPosition == 2) {
            MsgAnnHelper.tj(AnnMsg.EN_PAGE);
        }
    }

    private void getFragment(int action) {
        Fragment fragment = null;
        if (action == 1) {
            fragment = HotChatHomeFragment.newInstance();
        } else if (action == 0) {
            fragment = RecommendFragment.newInstance();
        } else if (action == 2) {
            fragment = new MsgHomeFragment();
        }
        getChildFragmentManager().beginTransaction().replace(R.id.home_content, fragment).commit();
//

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onLazyDestroyView() {
        super.onLazyDestroyView();
    }
}
