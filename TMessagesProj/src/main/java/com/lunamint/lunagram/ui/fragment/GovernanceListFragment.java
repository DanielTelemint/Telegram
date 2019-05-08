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
import com.lunamint.lunagram.ui.ProposalDetailActivity;
import com.lunamint.lunagram.ui.adapter.GovernanceListAdapter;
import com.lunamint.wallet.ApiUtils;
import com.lunamint.wallet.LcdService;
import com.lunamint.wallet.model.Proposal;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GovernanceListFragment extends Fragment {

    private int type;
    private String address;
    private String accountName;

    private ArrayList<Proposal> proposals = new ArrayList<>();

    private GovernanceListAdapter adapter;

    private LinearLayout contentsLayout;
    private LinearLayout loadingLayout;
    private TextView emptyTextview;

    private Comparator<Proposal> comparator = new Comparator<Proposal>() {
        @Override
        public int compare(Proposal o1, Proposal o2) {
            return Long.compare(o2.getVotingEndTimeWithLong(), o1.getVotingEndTimeWithLong());
        }
    };

    public static GovernanceListFragment newInstance(int type, String accountName, String address) {
        GovernanceListFragment fragment = new GovernanceListFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putString("accountName", accountName);
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
        accountName = getArguments().getString("accountName");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getContext() == null) return null;

        FrameLayout mainLayout = new FrameLayout(getContext());

        contentsLayout = new LinearLayout(getContext());
        contentsLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.addView(contentsLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        adapter = new GovernanceListAdapter(getContext(), 0, proposals);
        ListView listview = new ListView(getContext());
        listview.setDivider(new ColorDrawable(0x88E4E9FE));
        listview.setDividerHeight(AndroidUtilities.dp(1));
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showProposalDetailActivity(position);
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

        String proposalType = "";
        switch (type) {
            case 0:
                proposalType = "voting_period";
                break;
            case 1:
                proposalType = "passed";
                break;
            case 2:
                proposalType = "deposit_period";
                break;
        }

        getProposals(proposalType);
    }

    private void getProposals(final String proposalType) {

        LcdService lcdService = ApiUtils.getLcdService();
        lcdService.getProposals(proposalType).enqueue(new Callback<ArrayList<Proposal>>() {
            @Override
            public void onResponse(Call<ArrayList<Proposal>> call, Response<ArrayList<Proposal>> response) {

                if (response.code() == 200) {
                    switch (proposalType) {
                        case "passed":
                            proposals = new ArrayList<>();
                            addProposals(response.body());
                            getProposals("rejected");
                            return;
                        case "rejected":
                            addProposals(response.body());
                            break;
                        default:
                            proposals = response.body();
                            break;
                    }
                }

                update();
            }

            @Override
            public void onFailure(Call<ArrayList<Proposal>> call, Throwable t) {
                update();
            }
        });
    }

    private void addProposals(ArrayList<Proposal> proposals) {
        this.proposals.addAll(proposals);
    }

    private void update() {
        if (type == 1 && proposals != null) Collections.sort(proposals, comparator);

        loadingLayout.setVisibility(View.GONE);
        if (proposals != null && proposals.size() > 0) {
            if (adapter != null) {
                adapter.update(proposals);
                adapter.notifyDataSetChanged();
            }
            contentsLayout.setVisibility(View.VISIBLE);
            emptyTextview.setVisibility(View.GONE);
        } else {
            contentsLayout.setVisibility(View.GONE);
            emptyTextview.setVisibility(View.VISIBLE);
        }
    }

    private void showProposalDetailActivity(int pos) {
        if (getContext() == null) return;
        Intent intent = new Intent(getContext(), ProposalDetailActivity.class);
        intent.putExtra("accountName", accountName);
        intent.putExtra("address", address);
        intent.putExtra("proposal", proposals.get(pos));

        getContext().startActivity(intent);
    }
}
