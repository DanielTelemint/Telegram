package com.telemint.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.telemint.ui.view.StakingView;
import com.telemint.ui.view.WalletView;
import com.telemint.ui.view.WalletView2;

import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.DialogsActivity;

public class MainTabAdapter extends PagerAdapter {

    private Context mContext;
    private DialogsActivity mActivity;
    private RecyclerListView dialogsListview;

    public MainTabAdapter(Context context, DialogsActivity activity, RecyclerListView listview){
        mContext = context;
        mActivity = activity;
        dialogsListview = listview;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        if(position == 0) {
            LinearLayout stakingView = new StakingView(mContext, mActivity);
            collection.addView(stakingView);
            return stakingView;
        }else if(position == 1){
            RelativeLayout walletView = new WalletView2(mContext, mActivity);
            collection.addView(walletView);
            return walletView;
        }else{
            collection.addView(dialogsListview);
            return dialogsListview;
        }

    }
}
