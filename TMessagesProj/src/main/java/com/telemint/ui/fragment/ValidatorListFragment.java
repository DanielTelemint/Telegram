package com.telemint.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.telemint.messenger.R;
import com.telemint.ui.adapter.ValidatorListAdapter;
import com.telemint.ui.test.TestData;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.LayoutHelper;

public class ValidatorListFragment extends BaseFragment {

    private ValidatorListAdapter adapter;

    public ValidatorListFragment(Bundle args) {
        super(args);
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();

    }
    @Override
    public View createView(Context context) {
        actionBar.setTitle("Stake");
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setBackgroundColor(context.getResources().getColor(R.color.actionbar_bg));

        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        fragmentView = mainLayout;
        makePrototypeLayout(context, mainLayout);
        return fragmentView;
    }

    protected void clearViews() {
        if (fragmentView != null) {
            ViewGroup parent = (ViewGroup) fragmentView.getParent();
            if (parent != null) {
                try {
                    onRemoveFromParent();
                    parent.removeView(fragmentView);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            fragmentView = null;
        }
        if (actionBar != null) {
            ViewGroup parent = (ViewGroup) actionBar.getParent();
            if (parent != null) {
                try {
                    parent.removeView(actionBar);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            actionBar = null;
        }
        parentLayout = null;
    }

    private void makePrototypeLayout(Context context, LinearLayout mainLayout){

        TextView searchTextview = new TextView(context);
        searchTextview.setTextColor(context.getResources().getColor(R.color.text_title_default));
        searchTextview.setTextSize(0, AndroidUtilities.dp(12));
        searchTextview.setPadding(AndroidUtilities.dp(35),AndroidUtilities.dp(14),0,AndroidUtilities.dp(14));
        searchTextview.setText("Search Validator");
        mainLayout.addView(searchTextview);

        ImageView searchbarLine = new ImageView(context);
        searchbarLine.setBackgroundColor(0xff000000);
        searchbarLine.setAlpha(0.6f);
        mainLayout.addView(searchbarLine, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));

        Button sortArea = new Button(context);
        sortArea.setText("Sorting function Area");
        mainLayout.addView(sortArea, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 100));

        ListView listView = new ListView(context);
        listView.setDividerHeight(1);
        listView.setPadding(AndroidUtilities.dp(10),0, AndroidUtilities.dp(10),0);
        listView.setOnItemClickListener(onItemClickListener);
        mainLayout.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 0, 1.0f));

        adapter = new ValidatorListAdapter(context, 0, TestData.getInstance().getValidatorList());
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            presentValidatorInfoFragment();
        }
    };

    private void presentValidatorInfoFragment(){
        Bundle args = new Bundle();
        args.putBoolean("test", true);
        presentFragment(new ValidatorInfoFragment(args));
    }
}
