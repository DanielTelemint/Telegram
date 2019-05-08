package com.lunamint.lunagram.ui.cell;

import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lunamint.lunagram.R;
import com.lunamint.wallet.model.Proposal;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class GovernanceListCell extends LinearLayout {

    private TextView proposalIdTextview;
    private TextView titleTextview;
    private TextView proposalTypeTextview;
    private TextView statusTextview;

    public GovernanceListCell(Context context) {
        super(context);

        setBackgroundColor(ActivityCompat.getColor(context, R.color.white));
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(12), AndroidUtilities.dp(16), AndroidUtilities.dp(12));

        LinearLayout infoLayout = new LinearLayout(context);
        infoLayout.setOrientation(VERTICAL);
        addView(infoLayout, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.f, 0, 0, 10, 0));

        proposalIdTextview = new TextView(context);
        proposalIdTextview.setTextSize(0, AndroidUtilities.dp(10));
        proposalIdTextview.setTextColor(ActivityCompat.getColor(context, R.color.languid_lavender));
        infoLayout.addView(proposalIdTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        titleTextview = new TextView(context);
        titleTextview.setTextSize(0, AndroidUtilities.dp(16));
        titleTextview.setTextColor(ActivityCompat.getColor(context, R.color.charcoal));
        titleTextview.setSingleLine(true);
        titleTextview.setEllipsize(TextUtils.TruncateAt.END);
        infoLayout.addView(titleTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        proposalTypeTextview = new TextView(context);
        proposalTypeTextview.setTextSize(0, AndroidUtilities.dp(12));
        proposalTypeTextview.setTextColor(ActivityCompat.getColor(context, R.color.manatee));
        infoLayout.addView(proposalTypeTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        statusTextview = new TextView(context);
        statusTextview.setTextSize(0, AndroidUtilities.dp(16));
        statusTextview.setTextColor(ActivityCompat.getColor(context, R.color.charcoal));
        addView(statusTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
    }

    public void update(Proposal data) {

        if (proposalIdTextview == null) return;

        proposalIdTextview.setText(LocaleController.getString("proposal", R.string.proposal) + " #" + String.valueOf(data.getProposalId()));
        titleTextview.setText(data.getProposalContent().getProposalDetail().getTitle());
        proposalTypeTextview.setText(data.getProposalContent().getType());

        switch (data.getProposalStatus()) {
            case "DepositPeriod":
                statusTextview.setText(LocaleController.getString("pending", R.string.pending));
                break;
            case "VotingPeriod":
                statusTextview.setText(LocaleController.getString("active", R.string.active));
                break;
            case "Passed":
                statusTextview.setText(LocaleController.getString("passed", R.string.passed));
                break;
            case "Rejected":
                statusTextview.setText(LocaleController.getString("rejected", R.string.rejected));
                break;
        }
    }

    private String getVote(String v) {
        return v.substring(0, v.indexOf("."));
    }
}
