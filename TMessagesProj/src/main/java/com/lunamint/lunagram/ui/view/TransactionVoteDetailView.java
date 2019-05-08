package com.lunamint.lunagram.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lunamint.lunagram.R;
import com.lunamint.wallet.Blockchain;
import com.lunamint.wallet.utils.NumberFormatter;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;


public class TransactionVoteDetailView extends LinearLayout {

    public static int TYPE_CONFIRM = 0;
    public static int TYPE_RESULT = 1;

    private String txHash;

    public TransactionVoteDetailView(Context context, int type, boolean result, String txHash, String proposalId, String vote, String fee) {
        super(context);

        this.txHash = txHash;

        fee = NumberFormatter.getNumber(fee);

        String resultText;

        if (type == TYPE_CONFIRM) {
            resultText = null;
        } else if (result) {
            resultText = LocaleController.getString("successed", R.string.successed);
        } else {
            resultText = LocaleController.getString("failed", R.string.failed);
        }

        setOrientation(LinearLayout.VERTICAL);
        setPadding(AndroidUtilities.dp(20), 0, AndroidUtilities.dp(20), 0);

        String titles[] = {LocaleController.getString("result", R.string.result), LocaleController.getString("cosmosTxHash", R.string.cosmosTxHash), LocaleController.getString("proposalId", R.string.proposalId), LocaleController.getString("vote", R.string.vote), LocaleController.getString("transactionFee", R.string.transactionFee), LocaleController.getString("chain", R.string.chain)};
        String desc[] = {resultText, txHash, proposalId, vote, fee + "\n" + Blockchain.getInstance().getReserveDisplayName(), Blockchain.getInstance().getChainId()};

        for (int i = 0; titles.length > i; i++) {
            if (desc[i] == null) continue;
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setGravity(Gravity.CENTER_VERTICAL);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setPadding(0, AndroidUtilities.dp(14), 0, AndroidUtilities.dp(14));
            if (i == 1) linearLayout.setOnClickListener(onClickListener);
            addView(linearLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

            TextView titleTextview = new TextView(context);
            titleTextview.setTextSize(0, AndroidUtilities.dp(14));
            titleTextview.setTextColor(ActivityCompat.getColor(context, R.color.manatee));
            titleTextview.setText(titles[i]);
            titleTextview.setTypeface(Typeface.DEFAULT_BOLD);
            linearLayout.addView(titleTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 60, 0));

            TextView valueTextview = new TextView(context);
            valueTextview.setTextSize(0, AndroidUtilities.dp(14));
            if (i == 0 && !result) {
                valueTextview.setTextColor(ActivityCompat.getColor(context, R.color.coral_red));
            } else {
                valueTextview.setTextColor(ActivityCompat.getColor(context, R.color.charcoal));
            }
            valueTextview.setGravity(Gravity.RIGHT);
            valueTextview.setText(desc[i]);
            linearLayout.addView(valueTextview, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f));

            if (i < titles.length - 1) {
                ImageView line = new ImageView(context);
                line.setBackgroundColor(0x88E4E9FE);
                addView(line, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 1));
            }
        }
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (txHash == null || txHash.length() == 0) return;
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText(LocaleController.getString("copyTxHashTitle", R.string.copyTxHashTitle), txHash);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), LocaleController.getString("txHashCopied", R.string.txHashCopied), Toast.LENGTH_LONG).show();
            }
        }
    };
}
