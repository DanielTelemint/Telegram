package com.lunamint.lunagram.ui.component;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lunamint.lunagram.R;
import com.lunamint.lunagram.ui.adapter.DefaultSelectListAdapter;

import java.util.ArrayList;

public class DefaultSelectDialog extends Dialog {

    private String title;

    private AdapterView.OnItemClickListener onItemClickListener;
    private ArrayList<String> dataList;

    public DefaultSelectDialog(Context context, String title, ArrayList<String> dataList, AdapterView.OnItemClickListener onItemClickListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.title = title;
        this.dataList = dataList;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.select_list_dialog);

        LinearLayout mainLayout = findViewById(R.id.main_layout);
        TextView titleTextview = findViewById(R.id.title_textview);
        ListView listView = findViewById(R.id.listview);

        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        titleTextview.setText(title);

        listView.setDividerHeight(0);
        listView.setOnItemClickListener(onItemClickListener);

        DefaultSelectListAdapter tokenListAdapter = new DefaultSelectListAdapter(getContext(), 0, dataList);
        listView.setAdapter(tokenListAdapter);
    }
}
