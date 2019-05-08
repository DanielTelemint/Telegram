package com.lunamint.lunagram.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lunamint.lunagram.ui.fragment.GovernanceListFragment;

public class GovernancePagerAdapter extends FragmentPagerAdapter {

    private String accountName;
    private String address;

    public GovernancePagerAdapter(FragmentManager fragmentManager, String accountName, String address) {
        super(fragmentManager);
        this.accountName = accountName;
        this.address = address;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Fragment getItem(int position) {
        return GovernanceListFragment.newInstance(position, accountName, address);
    }

}
