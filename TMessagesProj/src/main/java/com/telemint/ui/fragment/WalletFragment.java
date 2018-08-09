package com.telemint.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.telegram.messenger.FileLog;
import org.telegram.ui.ActionBar.BaseFragment;

public class WalletFragment extends BaseFragment {
    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();

    }
    @Override
    public View createView(final Context context) {
        LinearLayout walletLayout = new LinearLayout(context);
        walletLayout.setBackgroundColor(Color.BLACK);
        fragmentView = walletLayout;
        return fragmentView;
    }

    protected void clearViews() {
        if (fragmentView != null) {
            ViewGroup parent = (ViewGroup) fragmentView.getParent();
            if (parent != null) {
                try {
                    onRemoveFromParent();
                    parent.removeView(fragmentView);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            fragmentView = null;
        }
        if (actionBar != null) {
            ViewGroup parent = (ViewGroup) actionBar.getParent();
            if (parent != null) {
                try {
                    parent.removeView(actionBar);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            actionBar = null;
        }
        parentLayout = null;
    }
}
