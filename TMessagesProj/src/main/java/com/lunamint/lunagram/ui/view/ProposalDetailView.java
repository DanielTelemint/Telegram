package com.lunamint.lunagram.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lunamint.lunagram.R;
import com.lunamint.wallet.model.Proposal;
import com.lunamint.wallet.utils.UiUtil;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class ProposalDetailView extends RelativeLayout {

    private TextView statusTextview;
    private TextView proposalIdTextview;
    private TextView titleTextview;
    private TextView proposalTypeTextview;
    private TextView endTimeTextview;
    private TextView tallyTextviews[] = new TextView[4];
    private TextView descTextview;


    public ProposalDetailView(Context context, Proposal proposal) {
        super(context);

        setClipToPadding(false);
        setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(20), AndroidUtilities.dp(20), AndroidUtilities.dp(10));

        CardView cardView = new CardView(context);
        cardView.setUseCompatPadding(true);
        cardView.setRadius(AndroidUtilities.dp(8));
        cardView.setCardElevation(UiUtil.getDefaultElevation());
        cardView.setBackgroundResource(R.drawable.validator_profile_bg);
        addView(cardView, LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 45, 0, 0));

        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setGravity(Gravity.CENTER);
        mainLayout.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(16), AndroidUtilities.dp(16), AndroidUtilities.dp(16));
        cardView.addView(mainLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        statusTextview = new TextView(context);
        statusTextview.setTextSize(0, AndroidUtilities.dp(12));
        statusTextview.setGravity(Gravity.RIGHT);
        mainLayout.addView(statusTextview, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 10));

        proposalIdTextview = new TextView(context);
        proposalIdTextview.setTextSize(0, AndroidUtilities.dp(12));
        proposalIdTextview.setTextColor(ActivityCompat.getColor(context, R.color.columbia_blue));
        mainLayout.addView(proposalIdTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        titleTextview = new TextView(context);
        titleTextview.setTextSize(0, AndroidUtilities.dp(24));
        titleTextview.setTextColor(ActivityCompat.getColor(context, R.color.white));
        titleTextview.setTypeface(Typeface.DEFAULT_BOLD);
        mainLayout.addView(titleTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 10));

        LinearLayout typeLayout = new LinearLayout(context);
        typeLayout.setOrientation(LinearLayout.HORIZONTAL);
        typeLayout.setPadding(0, AndroidUtilities.dp(15), 0, AndroidUtilities.dp(15));
        mainLayout.addView(typeLayout);

        TextView typeTextview = new TextView(context);
        typeTextview.setTextSize(0, AndroidUtilities.dp(14));
        typeTextview.setTextColor(ActivityCompat.getColor(context, R.color.languid_lavender));
        typeTextview.setText(LocaleController.getString("proposalType", R.string.proposalType));
        typeLayout.addView(typeTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 10, 0));

        proposalTypeTextview = new TextView(context);
        proposalTypeTextview.setTextSize(0, AndroidUtilities.dp(14));
        proposalTypeTextview.setTextColor(ActivityCompat.getColor(context, R.color.languid_lavender));
        proposalTypeTextview.setGravity(Gravity.RIGHT);
        typeLayout.addView(proposalTypeTextview, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f));

        if(!proposal.getProposalStatus().equals("DepositPeriod")){
            LinearLayout dateLayout = new LinearLayout(context);
            dateLayout.setOrientation(LinearLayout.HORIZONTAL);
            dateLayout.setPadding(0, AndroidUtilities.dp(15), 0, AndroidUtilities.dp(15));
            mainLayout.addView(dateLayout);

            TextView dateTextview = new TextView(context);
            dateTextview.setTextSize(0, AndroidUtilities.dp(14));
            dateTextview.setTextColor(ActivityCompat.getColor(context, R.color.languid_lavender));
            dateTextview.setText(LocaleController.getString("votingEnd", R.string.votingEnd) + " (UTC)");
            dateLayout.addView(dateTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 10, 0));

            endTimeTextview = new TextView(context);
            endTimeTextview.setTextSize(0, AndroidUtilities.dp(14));
            endTimeTextview.setTextColor(ActivityCompat.getColor(context, R.color.languid_lavender));
            endTimeTextview.setGravity(Gravity.RIGHT);
            dateLayout.addView(endTimeTextview, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f));
        }

        int descMarginTop = 16;
        if (!proposal.getProposalStatus().equals("VotingPeriod") && !proposal.getProposalStatus().equals("DepositPeriod")) {
            LinearLayout tallyLinearlayout = new LinearLayout(context);
            tallyLinearlayout.setOrientation(LinearLayout.HORIZONTAL);
            tallyLinearlayout.setBackgroundResource(R.drawable.bg_card_blue_opacity20);
            tallyLinearlayout.setPadding(AndroidUtilities.dp(10), AndroidUtilities.dp(8), AndroidUtilities.dp(10), AndroidUtilities.dp(8));
            mainLayout.addView(tallyLinearlayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, descMarginTop));
            descMarginTop = 0;

            int rightMargin = AndroidUtilities.dp(10);

            int titles[] = {R.string.yes, R.string.no, R.string.noWithVeto, R.string.abstain};
            for (int i = 0; tallyTextviews.length > i; i++) {
                if (i == tallyTextviews.length - 1) rightMargin = 0;
                LinearLayout tallyLayout = new LinearLayout(context);
                tallyLayout.setOrientation(LinearLayout.VERTICAL);
                tallyLayout.setGravity(Gravity.CENTER);
                tallyLinearlayout.addView(tallyLayout, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f, 0, 0, rightMargin, 0));

                TextView tallyTitleTextview = new TextView(context);
                tallyTitleTextview.setTextSize(0, AndroidUtilities.dp(10));
                tallyTitleTextview.setGravity(Gravity.CENTER);
                tallyTitleTextview.setTextColor(ActivityCompat.getColor(context, R.color.columbia_blue));
                tallyTitleTextview.setText(titles[i]);
                tallyLayout.addView(tallyTitleTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

                tallyTextviews[i] = new TextView(context);
                tallyTextviews[i].setTextSize(0, AndroidUtilities.dp(12));
                tallyTextviews[i].setTextColor(ActivityCompat.getColor(context, R.color.white));
                tallyLayout.addView(tallyTextviews[i], LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
            }
        }

        descTextview = new TextView(context);
        descTextview.setTextSize(0, AndroidUtilities.dp(14));
        descTextview.setTextColor(ActivityCompat.getColor(context, R.color.white));
        mainLayout.addView(descTextview, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, descMarginTop, 0, 0));

        LinearLayout profileImageLayout = new LinearLayout(context);
        profileImageLayout.setOrientation(LinearLayout.HORIZONTAL);
        profileImageLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            profileImageLayout.setElevation(AndroidUtilities.dp(20));
        }
        addView(profileImageLayout, 0, LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        ImageView profileImageview = new ImageView(context);
        profileImageview.setImageResource(R.drawable.ic_cosmos_tools_vote);
        profileImageLayout.addView(profileImageview, LayoutHelper.createLinear(64, 64));

        update(proposal);
    }

    public void update(Proposal data) {
        if (statusTextview == null) return;

        switch (data.getProposalStatus()) {
            case "DepositPeriod":
                statusTextview.setText(LocaleController.getString("pending", R.string.pending));
                statusTextview.setTextColor(ActivityCompat.getColor(getContext(), R.color.mango_tango));
                break;
            case "VotingPeriod":
                statusTextview.setText(LocaleController.getString("active", R.string.active));
                statusTextview.setTextColor(ActivityCompat.getColor(getContext(), R.color.dark_turqouise));
                break;
            case "Passed":
                statusTextview.setText(LocaleController.getString("passed", R.string.passed));
                statusTextview.setTextColor(ActivityCompat.getColor(getContext(), R.color.white));
                break;
            case "Rejected":
                statusTextview.setText(LocaleController.getString("rejected", R.string.rejected));
                statusTextview.setTextColor(ActivityCompat.getColor(getContext(), R.color.coral_red));
                break;
        }

        proposalIdTextview.setText(LocaleController.getString("proposal", R.string.proposal) + " #" + data.getProposalId());
        titleTextview.setText(data.getProposalContent().getProposalDetail().getTitle());
        proposalTypeTextview.setText(data.getProposalContent().getType());
        if (endTimeTextview != null) endTimeTextview.setText(data.getVotingEndTime());

        if (!data.getProposalStatus().equals("VotingPeriod") && !data.getProposalStatus().equals("DepositPeriod")) {
            try {
                Double yes = Double.parseDouble(data.getFinalTallyResult().getYes());
                Double no = Double.parseDouble(data.getFinalTallyResult().getNo());
                Double noWithVeto = Double.parseDouble(data.getFinalTallyResult().getNoWithVeto());
                Double abstain = Double.parseDouble(data.getFinalTallyResult().getAbstain());
                Double totalVoted = yes + no + noWithVeto + abstain;

                yes = yes == 0 ? 0 : ((yes / totalVoted) * 100);
                no = no == 0 ? 0 : ((no / totalVoted) * 100);
                noWithVeto = noWithVeto == 0 ? 0 : ((noWithVeto / totalVoted) * 100);
                abstain = abstain == 0 ? 0 : ((abstain / totalVoted) * 100);

                tallyTextviews[0].setText(String.format("%.1f", yes) + "%");
                tallyTextviews[1].setText(String.format("%.1f", no) + "%");
                tallyTextviews[2].setText(String.format("%.1f", noWithVeto) + "%");
                tallyTextviews[3].setText(String.format("%.1f", abstain) + "%");

            } catch (Exception e) {
                if (data.getFinalTallyResult() != null) {
                    tallyTextviews[0].setText(getVote(data.getFinalTallyResult().getYes()));
                    tallyTextviews[1].setText(getVote(data.getFinalTallyResult().getNo()));
                    tallyTextviews[2].setText(getVote(data.getFinalTallyResult().getNoWithVeto()));
                    tallyTextviews[3].setText(getVote(data.getFinalTallyResult().getAbstain()));
                }
            }
        }
        descTextview.setText(data.getProposalContent().getProposalDetail().getDescription());
    }

    private String getVote(String v) {
        return v.substring(0, v.indexOf(".") + 2);
    }
}
