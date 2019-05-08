package com.lunamint.lunagram.ui;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.lunamint.lunagram.R;
import com.lunamint.lunagram.ui.adapter.TokenListAdapter;
import com.lunamint.lunagram.ui.component.LunagramBaseActivity;
import com.lunamint.wallet.model.Coin;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

import java.util.ArrayList;

public class SelectTokenActivity extends LunagramBaseActivity {

    public static final int REQUEST_CODE_SELECT_TOKEN = 10;

    private ArrayList<Coin> coins;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(LocaleController.getString("selectToken", R.string.selectToken));

        coins = (ArrayList<Coin>) getIntent().getSerializableExtra("coins");

        LinearLayout mainLayout = new LinearLayout(this);

        ListView listView = new ListView(this);
        listView.setOnItemClickListener(onItemClickListener);
        listView.setDivider(new ColorDrawable(0x88E4E9FE));
        listView.setDividerHeight(AndroidUtilities.dp(1));
        mainLayout.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        TokenListAdapter tokenListAdapter = new TokenListAdapter(this, 0, coins);
        listView.setAdapter(tokenListAdapter);

        setContentView(mainLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    ListView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            finishWithResult(coins.get(position));
        }
    };

    private void finishWithResult(Coin coin) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("coin", coin);
        setResult(SelectTokenActivity.REQUEST_CODE_SELECT_TOKEN, resultIntent);
        finish();
    }
}
