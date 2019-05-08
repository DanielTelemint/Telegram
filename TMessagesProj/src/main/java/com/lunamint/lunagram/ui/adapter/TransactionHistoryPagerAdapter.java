package com.lunamint.lunagram.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lunamint.lunagram.ui.fragment.TransactionHistoryFragment;

public class TransactionHistoryPagerAdapter extends FragmentPagerAdapter {

    private String address;

    public TransactionHistoryPagerAdapter(FragmentManager fragmentManager, String address) {
        super(fragmentManager);
        this.address = address;
    }

    @Override
    public int getCount(){
        return 3;
    }

    @Override
    public Fragment getItem(int position) {
        return TransactionHistoryFragment.newInstance(position, address);
    }

}
