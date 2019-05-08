package com.lunamint.lunagram.ui.cell;

import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lunamint.lunagram.R;
import com.lunamint.wallet.model.Node;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class NodeCell extends LinearLayout {

    private TextView nameTextview;
    private ImageView statusImgview;
    private TextView statusTextview;
    private ImageView selectedImgview;

    public NodeCell(Context context) {
        super(context);

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setBackgroundColor(ActivityCompat.getColor(context, R.color.white));
        setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(18), AndroidUtilities.dp(20), AndroidUtilities.dp(18));

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(VERTICAL);
        addView(linearLayout, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f, 0, 0, 12, 0));

        nameTextview = new TextView(context);
        nameTextview.setTextColor(ActivityCompat.getColor(context, R.color.charcoal));
        nameTextview.setTextSize(0, AndroidUtilities.dp(16));
        linearLayout.addView(nameTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 4));

        LinearLayout statusLayout = new LinearLayout(context);
        statusLayout.setOrientation(HORIZONTAL);
        statusLayout.setGravity(Gravity.CENTER_VERTICAL);
        linearLayout.addView(statusLayout, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        statusImgview = new ImageView(getContext());
        statusLayout.addView(statusImgview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 5, 0));

        statusTextview = new TextView(context);
        statusTextview.setTextColor(ActivityCompat.getColor(context, R.color.manatee));
        statusTextview.setTextSize(0, AndroidUtilities.dp(14));
        statusLayout.addView(statusTextview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        selectedImgview = new ImageView(getContext());
        selectedImgview.setImageResource(R.drawable.ic_check);
        addView(selectedImgview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
        selectedImgview.setVisibility(INVISIBLE);
    }

    public void update(Node node) {
        if (nameTextview == null) return;

        nameTextview.setText(node.getName());

        if (node.getIsActive()) {
            statusImgview.setImageResource(R.drawable.node_status_active);
            statusTextview.setText(LocaleController.getString("activeNode", R.string.activeNode));
        } else {
            statusImgview.setImageResource(R.drawable.node_status_inactive);
            statusTextview.setText(LocaleController.getString("inactiveNode", R.string.inactiveNode));
        }

        if (node.getIsSelected()) {
            selectedImgview.setVisibility(VISIBLE);
        } else {
            selectedImgview.setVisibility(GONE);
        }
    }
}
