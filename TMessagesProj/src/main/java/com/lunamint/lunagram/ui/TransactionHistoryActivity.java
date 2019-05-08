package com.lunamint.lunagram.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;

import com.lunamint.lunagram.R;
import com.lunamint.lunagram.ui.adapter.TransactionHistoryPagerAdapter;
import com.lunamint.lunagram.ui.component.LunagramBaseActivity;

import org.telegram.messenger.LocaleController;

public class TransactionHistoryActivity extends LunagramBaseActivity {

    public static final int TYPE_TRANSFER = 0;
    public static final int TYPE_STAKING = 1;
    public static final int TYPE_GOVERNANCE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(LocaleController.getString("transactionHistory", R.string.transactionHistory));

        setContentView(R.layout.fragment_viewpager);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setSelectedTabIndicatorColor(ActivityCompat.getColor(this, R.color.tab_indicator));
        tabLayout.setTabTextColors(ActivityCompat.getColor(this, R.color.tab_text), ActivityCompat.getColor(this, R.color.tab_text_selected));

        ViewPager viewPager = findViewById(R.id.viewpager);

        TransactionHistoryPagerAdapter adapter = new TransactionHistoryPagerAdapter(getSupportFragmentManager(), getIntent().getStringExtra("address"));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setBackgroundColor(ActivityCompat.getColor(this, R.color.bg_default));

        tabLayout.setupWithViewPager(viewPager);
        String[] tabText = {LocaleController.getString("transfer", R.string.transfer), LocaleController.getString("staking", R.string.staking), LocaleController.getString("governance", R.string.governance)};

        for (int i = 0; tabText.length > i; i++) {
            tabLayout.getTabAt(i).setText(tabText[i]);
        }

        viewPager.setCurrentItem(getIntent().getIntExtra("startType", 0));

    }
}