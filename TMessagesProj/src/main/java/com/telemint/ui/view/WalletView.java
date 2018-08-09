package com.telemint.ui.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.telemint.messenger.R;
import com.telemint.ui.adapter.MainTabWalletAdapter;
import com.telemint.ui.fragment.SendAssetFragment;
import com.telemint.ui.test.TestData;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.DialogsActivity;

public class WalletView extends RelativeLayout {

    private DialogsActivity mActivity;

    private TextView addressTextview;

    private MainTabWalletAdapter adapter;

    public WalletView(Context context, DialogsActivity activity) {
        super(context);

        mActivity = activity;

        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(AndroidUtilities.dp(10), AndroidUtilities.dp(0), AndroidUtilities.dp(10), AndroidUtilities.dp(58));
        addView(mainLayout, LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        TextView titleTextview = new TextView(context);
        titleTextview.setText("Welcome to your wallet");
        titleTextview.setTextColor(getResources().getColor(R.color.tab_staking_title));
        titleTextview.setTextSize(0, AndroidUtilities.dp(16));
        mainLayout.addView(titleTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT,LayoutHelper.WRAP_CONTENT,0,20,0,0));

        addressTextview = new TextView(context);
        addressTextview.setText("cosmosaccaddr1rvm0em6w3qkzcwnzf9hkqvksujl895dfww4ecn");
        addressTextview.setTextColor(getResources().getColor(R.color.text_value_default));
        addressTextview.setTextSize(0, AndroidUtilities.dp(10));
        mainLayout.addView(addressTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT,LayoutHelper.WRAP_CONTENT,0,0,0,20));

        ListView listView = new ListView(context);
        listView.setDividerHeight(1);
        listView.setPadding(AndroidUtilities.dp(10),0, AndroidUtilities.dp(10),0);
        mainLayout.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 0, 1.0f));

        adapter = new MainTabWalletAdapter(context, 0, TestData.getInstance().getWalletTabMenu());
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        LinearLayout buttonLayout = new LinearLayout(context);
        buttonLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        buttonLayout.setPadding(AndroidUtilities.dp(10), AndroidUtilities.dp(16), AndroidUtilities.dp(10), AndroidUtilities.dp(16));
        addView(buttonLayout, LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, RelativeLayout.ALIGN_PARENT_BOTTOM));

        LinearLayout sendButton = new LinearLayout(context);
        sendButton.setGravity(Gravity.CENTER);
        sendButton.setPadding(AndroidUtilities.dp(10), 0, AndroidUtilities.dp(10), 0);
        sendButton.setOnClickListener(onClickListener);
        buttonLayout.addView(sendButton);

        Drawable drawable = context.getResources().getDrawable(R.drawable.btn_green);
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(0xff000000, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56), AndroidUtilities.dp(56));
            drawable = combinedDrawable;
        }
        sendButton.setBackground(drawable);

        ImageView sendIconImageview = new ImageView(context);
        sendIconImageview.setImageResource(R.drawable.icon_send);
        sendIconImageview.setScaleType(ImageView.ScaleType.FIT_XY);
        sendButton.addView(sendIconImageview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT,LayoutHelper.WRAP_CONTENT,0,0,5,0));

        TextView sendTextview = new TextView(context);
        sendTextview.setText("SEND ATOM");
        sendTextview.setTextColor(getResources().getColor(R.color.btn_text_default));
        sendTextview.setTextSize(0, AndroidUtilities.dp(12));
        sendButton.addView(sendTextview);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
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
