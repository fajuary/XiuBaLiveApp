package com.seek.home;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by chunyang on 2018/5/22.
 */

public class HomePagerAdapter extends FragmentPagerAdapter {


    private List<Fragment> mFragments;
//    private FragmentManager fm;

    public HomePagerAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
//        this.fm = fm;
        this.mFragments = list;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments == null ? 0 : mFragments.size();
    }


//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        Fragment fragment = (Fragment) super.instantiateItem(container,
//                position);
//        fm.beginTransaction().show(fragment).commit();
//        return fragment;
//    }
//
//    @Override
//    public void destroyItem(ViewGroup container, int position, Object object) {
////        super.destroyItem(container, position, object);
//        Fragment fragment = mFragments.get(position);
//        fm.beginTransaction().hide(fragment).commit();
//    }
}
