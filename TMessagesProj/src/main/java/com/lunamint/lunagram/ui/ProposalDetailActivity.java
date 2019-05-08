package com.lunamint.lunagram.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.lunamint.lunagram.R;
import com.lunamint.lunagram.ui.component.DefaultButton;
import com.lunamint.lunagram.ui.component.LunagramBaseActivity;
import com.lunamint.lunagram.ui.view.ProposalDetailView;
import com.lunamint.wallet.model.Proposal;

import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class ProposalDetailActivity extends LunagramBaseActivity {

    private String accountName;
    private String address;
    private Proposal proposal;

    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(LocaleController.getString("proposalDetail", R.string.proposalDetail));

        accountName = getIntent().getStringExtra("accountName");
        address = getIntent().getStringExtra("address");
        proposal = (Proposal) getIntent().getSerializableExtra("proposal");

        mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        setContentView(mainLayout);

        ScrollView scrollView = new ScrollView(this);
        mainLayout.addView(scrollView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 1, 1.0f));

        scrollView.addView(new ProposalDetailView(this, proposal));
        createVoteButton();

    }

    private void createVoteButton() {
        if (proposal != null && proposal.getProposalStatus().equals("DepositPeriod"))
            return;

        if (proposal != null && proposal.getProposalStatus().equals("VotingPeriod")) {
            DefaultButton voteButton = new DefaultButton(this, 4, true, LocaleController.getString("vote", R.string.vote), R.drawable.btn_radius4_blue, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showVoteActivity();
                }
            });
            mainLayout.addView(voteButton, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 20, 6, 20, 20));

        } else {
            DefaultButton voteButton = new DefaultButton(this, 4, true, LocaleController.getString("votingFinished", R.string.votingFinished), R.drawable.btn_grey, null);
            mainLayout.addView(voteButton, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 20, 6, 20, 20));
        }
    }

    private void showVoteActivity() {
        Intent intent = new Intent(this, VoteActivity.class);
        intent.putExtra("accountName", accountName);
        intent.putExtra("address", address);
        intent.putExtra("proposalId", proposal.getProposalId());
        startActivity(intent);
    }
}