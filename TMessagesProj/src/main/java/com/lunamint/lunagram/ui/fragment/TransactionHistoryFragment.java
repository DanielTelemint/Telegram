package com.lunamint.lunagram.ui.fragment;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lunamint.lunagram.R;
import com.lunamint.lunagram.ui.TransactionDetailActivity;
import com.lunamint.lunagram.ui.adapter.TransactionHistoryAdapter;
import com.lunamint.wallet.model.TransactionHistory;
import com.lunamint.wallet.ApiUtils;
import com.lunamint.wallet.LcdService;
import com.lunamint.wallet.model.tx.DefaultHistory;
import com.lunamint.wallet.model.tx.proposal.ProposalHistory;
import com.lunamint.wallet.model.tx.send.SendHistory;
import com.lunamint.wallet.utils.Parser;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionHistoryFragment extends Fragment {

    private int type;
    private String address;

    private ArrayList<TransactionHistory> transactionHistory = new ArrayList<>();

    private TransactionHistoryAdapter adapter;

    private LinearLayout contentsLayout;
    private LinearLayout loadingLayout;
    private TextView emptyTextview;

    private Comparator<TransactionHistory> comparator = new Comparator<TransactionHistory>() {
        @Override
        public int compare(TransactionHistory o1, TransactionHistory o2) {
            return Integer.compare(o2.getBlock(), o1.getBlock());
        }
    };

    public static TransactionHistoryFragment newInstance(int type, String address) {
        TransactionHistoryFragment fragment = new TransactionHistoryFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putString("address", address);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null) return;
        type = getArguments().getInt("type", 0);
        address = getArguments().getString("address");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getContext() == null) return null;

        FrameLayout mainLayout = new FrameLayout(getContext());

        contentsLayout = new LinearLayout(getContext());
        contentsLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.addView(contentsLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        adapter = new TransactionHistoryAdapter(getContext(), 0, address, transactionHistory);
        ListView listview = new ListView(getContext());
        listview.setDivider(new ColorDrawable(0x88E4E9FE));
        listview.setDividerHeight(AndroidUtilities.dp(1));
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (transactionHistory != null && transactionHistory.size() - 1 >= position)
                    showTransactionDetailActivity(transactionHistory.get(position).getHash());
            }
        });
        contentsLayout.addView(listview, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        contentsLayout.setVisibility(View.INVISIBLE);

        loadingLayout = new LinearLayout(getContext());
        loadingLayout.setGravity(Gravity.CENTER);
        mainLayout.addView(loadingLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        emptyTextview = new TextView(getContext());
        emptyTextview.setTextSize(0, AndroidUtilities.dp(16));
        emptyTextview.setTextColor(ActivityCompat.getColor(getContext(), R.color.payneGrey));
        emptyTextview.setGravity(Gravity.CENTER);
        emptyTextview.setText(LocaleController.getString("noTransactions", R.string.noTransactions));

        mainLayout.addView(emptyTextview, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, 20, 0, 20, 0));
        emptyTextview.setVisibility(View.INVISIBLE);

        ProgressBar progressBar = new ProgressBar(getContext());
        loadingLayout.addView(progressBar, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        return mainLayout;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        switch (type) {
            case 0:
                getTransactionHistorySent();
                break;
            case 1:
                getTransactionHistoryDelegate();
                break;
            case 2:
                getTransactionHistoryProposal();
                break;
        }
    }

    private void getTransactionHistorySent() {
        LcdService lcdService = ApiUtils.getLcdService();
        lcdService.getSendHistory(address).enqueue(new Callback<ArrayList<SendHistory>>() {
            @Override
            public void onResponse(Call<ArrayList<SendHistory>> call, Response<ArrayList<SendHistory>> response) {
                if (response.code() == 200 && response.body() != null) {
                    for (int i = 0; response.body().size() > i; i++) {
                        TransactionHistory history = Parser.getTransactionHistory(response.body().get(i));
                        if (history != null) transactionHistory.add(history);
                    }
                }
                getTransactionHistoryRecipient();
            }

            @Override
            public void onFailure(Call<ArrayList<SendHistory>> call, Throwable t) {
                getTransactionHistoryRecipient();
            }
        });
    }

    private void getTransactionHistoryRecipient() {
        LcdService lcdService = ApiUtils.getLcdService();
        lcdService.getRecipientHistory(address).enqueue(new Callback<ArrayList<SendHistory>>() {
            @Override
            public void onResponse(Call<ArrayList<SendHistory>> call, Response<ArrayList<SendHistory>> response) {
                if (response.code() == 200 && response.body() != null) {
                    for (int i = 0; response.body().size() > i; i++) {
                        TransactionHistory history = Parser.getTransactionHistory(response.body().get(i));
                        if (history != null) transactionHistory.add(history);
                    }
                }
                update();
            }

            @Override
            public void onFailure(Call<ArrayList<SendHistory>> call, Throwable t) {
                update();
            }
        });
    }

    private void getTransactionHistoryProposal() {
        LcdService lcdService = ApiUtils.getLcdService();
        lcdService.getProposalHistory(address).enqueue(new Callback<ArrayList<ProposalHistory>>() {
            @Override
            public void onResponse(Call<ArrayList<ProposalHistory>> call, Response<ArrayList<ProposalHistory>> response) {
                if (response.code() == 200 && response.body() != null) {
                    for (int i = 0; response.body().size() > i; i++) {
                        TransactionHistory history = Parser.getTransactionHistory(response.body().get(i));
                        if (history != null) transactionHistory.add(history);
                    }
                }
                update();
            }

            @Override
            public void onFailure(Call<ArrayList<ProposalHistory>> call, Throwable t) {
                update();
            }
        });
    }

    private void getTransactionHistoryDelegate() {
        LcdService lcdService = ApiUtils.getLcdService();
        lcdService.getDelegationHistory(address).enqueue(new Callback<ArrayList<DefaultHistory>>() {
            @Override
            public void onResponse(Call<ArrayList<DefaultHistory>> call, Response<ArrayList<DefaultHistory>> response) {
                if (response.code() == 200 && response.body() != null) {
                    for (int i = 0; response.body().size() > i; i++) {
                        TransactionHistory history = Parser.getTransactionHistory(response.body().get(i));
                        if (history != null) transactionHistory.add(history);
                    }
                } else {
                    String err = "";
                    try {
                        if (response.errorBody() != null) err = response.errorBody().string();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    emptyTextview.setText(LocaleController.getString("internalServerError", R.string.internalServerError) + "\n" + err);
                }
                update();
            }

            @Override
            public void onFailure(Call<ArrayList<DefaultHistory>> call, Throwable t) {
                update();
            }
        });
    }

    private void update() {
        if (transactionHistory != null) Collections.sort(transactionHistory, comparator);
        if (adapter != null) adapter.notifyDataSetChanged();
        loadingLayout.setVisibility(View.GONE);
        if (transactionHistory.size() > 0) {
            contentsLayout.setVisibility(View.VISIBLE);
            emptyTextview.setVisibility(View.GONE);
        } else {
            contentsLayout.setVisibility(View.GONE);
            emptyTextview.setVisibility(View.VISIBLE);
        }
    }

    private void showTransactionDetailActivity(String hash) {
        if (getContext() == null) return;
        Intent intent = new Intent(getContext(), TransactionDetailActivity.class);
        intent.putExtra("address", address);
        intent.putExtra("hash", hash);
        getContext().startActivity(intent);
    }
}
