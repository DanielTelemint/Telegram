package com.lunamint.lunagram.ui;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lunamint.lunagram.R;
import com.lunamint.lunagram.ui.adapter.SelectNodeAdapter;
import com.lunamint.lunagram.ui.component.LunagramBaseActivity;
import com.lunamint.wallet.ApiUtils;
import com.lunamint.wallet.Blockchain;
import com.lunamint.wallet.LcdService;
import com.lunamint.wallet.LunaService;
import com.lunamint.wallet.model.BlockchainInfo;
import com.lunamint.wallet.utils.NetworkUtil;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.ui.Components.LayoutHelper;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectNodeActivity extends LunagramBaseActivity {

    private BlockchainInfo blockchainInfo;

    private SelectNodeAdapter selectNodeAdapter;

    private ListView listView;
    private LinearLayout loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(LocaleController.getString("selectNode", R.string.selectNode));

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(ActivityCompat.getColor(this, R.color.bg_default));

        FrameLayout frameLayout = new FrameLayout(this);
        mainLayout.addView(frameLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView = new ListView(this);
        listView.setOnItemClickListener(onItemClickListener);
        listView.setDivider(new ColorDrawable(0x88E4E9FE));
        listView.setDividerHeight(AndroidUtilities.dp(1));
        frameLayout.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setVisibility(View.INVISIBLE);

        selectNodeAdapter = new SelectNodeAdapter(this, 0, null);
        listView.setAdapter(selectNodeAdapter);

        loadingLayout = new LinearLayout(this);
        loadingLayout.setGravity(Gravity.CENTER);
        frameLayout.addView(loadingLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        ProgressBar progressBar = new ProgressBar(this);
        loadingLayout.addView(progressBar, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        setContentView(mainLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBlockchainInfo();
    }

    private void getBlockchainInfo() {
        if (!NetworkUtil.isNetworkAvailable(this)) {
            showNetworkErrorAlert();
            return;
        }

        LunaService lunaService = ApiUtils.getLunaService();
        lunaService.getBlockchainInfo().enqueue(new Callback<ArrayList<BlockchainInfo>>() {
            @Override
            public void onResponse(Call<ArrayList<BlockchainInfo>> call, Response<ArrayList<BlockchainInfo>> response) {
                if (response.code() == 200 && response.body() != null) {
                    for (BlockchainInfo blockchainInfo : response.body()) {
                        if (blockchainInfo.getChainId().equals(Blockchain.getInstance().getChainId())) {
                            searchIdx = 0;
                            SelectNodeActivity.this.blockchainInfo = blockchainInfo;
                            checkNodeStatus();
                            return;
                        }
                    }

                    showError(LocaleController.getString("noHasNode", R.string.noHasNode));
                } else {
                    showError(LocaleController.getString("internalServerError", R.string.internalServerError) + "\nStatus code :" + response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<BlockchainInfo>> call, Throwable t) {

                if (!NetworkUtil.isNetworkAvailable(SelectNodeActivity.this))
                    showNetworkErrorAlert();
            }
        });
    }

    private int searchIdx;

    private void checkNodeStatus() {
        if (blockchainInfo == null || blockchainInfo.getList() == null) {
            update();
            return;
        }
        if (searchIdx == blockchainInfo.getList().size()) {
            update();
            return;
        }
        LcdService lcdService = ApiUtils.getLcdService(blockchainInfo.getList().get(searchIdx).getLcd());
        lcdService.getNodeStatus().enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.code() == 200 && response.body() != null) {
                    blockchainInfo.getList().get(searchIdx).setActive(!response.body());
                } else {
                    blockchainInfo.getList().get(searchIdx).setActive(false);
                }
                searchIdx++;
                checkNodeStatus();
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                blockchainInfo.getList().get(searchIdx).setActive(false);
                searchIdx++;
                checkNodeStatus();
            }
        });
    }

    private void update() {
        if (selectNodeAdapter != null && blockchainInfo != null) {
            selectNodeAdapter.update(blockchainInfo);
            selectNodeAdapter.notifyDataSetChanged();

            loadingLayout.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        } else {
            showError(LocaleController.getString("noHasNode", R.string.noHasNode));
        }
    }

    private void showError(String msg) {
        if (loadingLayout == null) return;
        listView.setVisibility(View.GONE);
        loadingLayout.removeAllViews();

        TextView errorTextview = new TextView(this);
        errorTextview.setTextSize(0, AndroidUtilities.dp(16));
        errorTextview.setTextColor(ActivityCompat.getColor(this, R.color.manatee));
        errorTextview.setGravity(Gravity.CENTER);
        errorTextview.setText(msg);
        loadingLayout.addView(errorTextview, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

    }

    ListView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (blockchainInfo == null || blockchainInfo.getList() == null) return;
            Blockchain.getInstance().changeNode(blockchainInfo.getList().get(position).getName(),
                    blockchainInfo.getList().get(position).getNode(),
                    blockchainInfo.getList().get(position).getLcd(),
                    blockchainInfo.getReserveDenom().getOrigin(),
                    blockchainInfo.getReserveDenom().getDisplayName());
            finish();

            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.nodeChanged);
        }
    };
}
