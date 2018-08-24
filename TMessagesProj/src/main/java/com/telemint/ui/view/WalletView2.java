package com.telemint.ui.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.telemint.messenger.R;
import com.telemint.ui.adapter.CardFragmentPagerAdapter;
import com.telemint.ui.adapter.MainTabWalletAdapter;
import com.telemint.ui.component.ShadowTransformer;
import com.telemint.ui.fragment.SendAssetFragment;
import com.telemint.ui.test.TestData;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.DialogsActivity;

public class WalletView2 extends RelativeLayout {

    private DialogsActivity mActivity;


    public WalletView2(Context context, DialogsActivity activity) {
        super(context);

        mActivity = activity;
        setClipToPadding(false);


        Button totalInfoArea = new Button(context);
        totalInfoArea.setText("Total Info Area\n\n\nTotal assets & Staking Infomation");
        addView(totalInfoArea, LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, 300));

        ViewPager viewPager = new ViewPager(context);
        viewPager.setClipToPadding(false);
        viewPager.setPadding(AndroidUtilities.dp(50),AndroidUtilities.dp(0),AndroidUtilities.dp(50),AndroidUtilities.dp(0));

        addView(viewPager, LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, 400, 0,300,0,0));

        CardFragmentPagerAdapter pagerAdapter = new CardFragmentPagerAdapter(context, AndroidUtilities.dp(2));
        ShadowTransformer fragmentCardShadowTransformer = new ShadowTransformer(viewPager, pagerAdapter);
        fragmentCardShadowTransformer.enableScaling(true);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageTransformer(false, fragmentCardShadowTransformer);
        viewPager.setOffscreenPageLimit(3);
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            presentSendAssetFragment();
        }
    };

    private void presentSendAssetFragment(){
        Bundle args = new Bundle();
        args.putBoolean("test", true);
        mActivity.presentFragment(new SendAssetFragment(args));
    }


}
