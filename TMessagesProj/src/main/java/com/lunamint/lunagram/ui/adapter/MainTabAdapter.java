package com.lunamint.lunagram.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.lunamint.lunagram.ui.view.WalletView;
import com.lunamint.wallet.model.Coin;

import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;

import retrofit2.Response;

public class MainTabAdapter extends PagerAdapter {

    private Context mContext;
    private RecyclerListView dialogsListview;
    private WalletView walletView;

    public MainTabAdapter(Context context, RecyclerListView listview, WalletView walletView){
        mContext = context;
        dialogsListview = listview;
        this.walletView = walletView;
    }

    @Override
    public int getCount() {
        return 2;
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
        if(position == 0){
            collection.addView(dialogsListview);
            return dialogsListview;
        }else{
            collection.addView(walletView);
            return walletView;
        }
    }

    public void onResume(){
        if(walletView != null) walletView.onResume();
    }

    public void onTransactionCreated(String tx){
        if(walletView != null) walletView.onTransactionCreated(tx);
    }

    public void onAccountChanged(){
        if(walletView != null) walletView.onAccountChanged();
    }

    public void onBalanceChanged(Response<ArrayList<Coin>> response){
        if(walletView != null) walletView.updateBalance(response);
    }

    public void onStakingChanged(){
        if(walletView != null) walletView.getStaking();
    }

    public void onNodeChanged(){
        if(walletView != null) walletView.onAccountChanged();
    }

    public void showError(String msg){
        if(walletView != null) walletView.showError(msg);
    }

    public void setMaintenance(boolean maintenance){
        if(walletView != null) walletView.setMaintenance(maintenance);
    }
}
