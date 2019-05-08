package com.lunamint.lunagram.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;

import com.lunamint.lunagram.R;
import com.lunamint.lunagram.ui.adapter.GovernancePagerAdapter;
import com.lunamint.lunagram.ui.component.LunagramBaseActivity;

import org.telegram.messenger.LocaleController;

public class GovernanceActivity extends LunagramBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_viewpager);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(LocaleController.getString("governance", R.string.governance));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setSelectedTabIndicatorColor(ActivityCompat.getColor(this, R.color.tab_indicator));
        tabLayout.setTabTextColors(ActivityCompat.getColor(this, R.color.tab_text), ActivityCompat.getColor(this, R.color.tab_text_selected));

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        GovernancePagerAdapter adapter = new GovernancePagerAdapter(getSupportFragmentManager(), getIntent().getStringExtra("accountName"), getIntent().getStringExtra("address"));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setBackgroundColor(ActivityCompat.getColor(this, R.color.bg_default));

        tabLayout.setupWithViewPager(viewPager);
        String[] tabText = {LocaleController.getString("active", R.string.active), LocaleController.getString("complete", R.string.complete), LocaleController.getString("pending", R.string.pending)};

        for (int i = 0; tabText.length > i; i++) {
            tabLayout.getTabAt(i).setText(tabText[i]);
        }
    }
}