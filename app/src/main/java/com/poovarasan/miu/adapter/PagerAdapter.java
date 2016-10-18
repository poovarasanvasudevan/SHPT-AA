package com.poovarasan.miu.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.poovarasan.miu.fragments.Calls;
import com.poovarasan.miu.fragments.Contacts;
import com.poovarasan.miu.fragments.Message;

/**
 * Created by poovarasanv on 14/10/16.
 */

public class PagerAdapter extends FragmentPagerAdapter {

    int tabCount;
    private String tabTitles[] = new String[] { "Calls", "Chat", "Contacts" };
    public PagerAdapter(FragmentManager fm,int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Calls tab1 = new Calls();
                return tab1;
            case 1:
                Message tab2 = new Message();
                return tab2;
            case 2:
                Contacts tab3 = new Contacts();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
