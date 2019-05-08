package com.lunamint.lunagram.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lunamint.lunagram.R;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class VoteFormView extends LinearLayout implements View.OnClickListener {

    private int selected = 1;

    private int voteTitles[] = {R.string.yes, R.string.no, R.string.noWithVeto, R.string.abstain};
    private String values[] = {"Yes", "No", "NoWithVeto", "Abstain"};

    private VoteCardView feeCardViews[];

    public VoteFormView(Context context) {
        super(context);

        setOrientation(VERTICAL);

        setClipToPadding(false);

        TextView titleTextview = new TextView(context);
        titleTextview.setTextColor(ActivityCompat.getColor(context, R.color.charcoal));
        titleTextview.setTextSize(0, AndroidUtilities.dp(32));
        titleTextview.setTypeface(Typeface.DEFAULT_BOLD);
        titleTextview.setText(LocaleController.getString("castVote", R.string.castVote));
        addView(titleTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 20, 10, 20, 0));

        TextView descTextview = new TextView(context);
        descTextview.setTextColor(ActivityCompat.getColor(context, R.color.payneGrey));
        descTextview.setTextSize(0, AndroidUtilities.dp(14));
        descTextview.setText(LocaleController.getString("chooseVote", R.string.chooseVote));
        addView(descTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 20, 0, 20, 24));

        LinearLayout voteLayout = new LinearLayout(context);
        voteLayout.setOrientation(HORIZONTAL);
        voteLayout.setClipToPadding(false);
        voteLayout.setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(10), AndroidUtilities.dp(20), AndroidUtilities.dp(20));
        addView(voteLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 20));

        int rightMargin = 10;
        feeCardViews = new VoteCardView[values.length];
        for (int i = 0; feeCardViews.length > i; i++) {
            boolean isSelected = (selected == i);
            feeCardViews[i] = new VoteCardView(context, i, isSelected, context.getString(voteTitles[i]));
            feeCardViews[i].setId(i);
            feeCardViews[i].setOnClickListener(this);
            if (i == feeCardViews.length - 1) rightMargin = 0;
            voteLayout.addView(feeCardViews[i], LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f, 0, 0, rightMargin, 0));
        }
    }

    public String getValue() {
        return values[selected];
    }

    @Override
    public void onClick(View v) {
        selected = v.getId();
        for (int i = 0; feeCardViews.length > i; i++) {
            boolean isSelected = (selected == i);
            feeCardViews[i].update(isSelected);
        }
    }
}