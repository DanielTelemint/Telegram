package com.telemint.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.telemint.ui.cell.DefaultInfoCell;
import com.telemint.ui.model.WalletTabMenu;

import java.util.ArrayList;

public class MainTabWalletAdapter extends ArrayAdapter {

    private Context mContext;
    private ArrayList<WalletTabMenu> dataList;

    public MainTabWalletAdapter(@NonNull Context context, int resource, ArrayList<WalletTabMenu> dataList) {
        super(context, resource);

        this.mContext = context;
        this.dataList = dataList;
    }

    static class ViewHolder {
        DefaultInfoCell listCell;
        int position;
    }

    @Override
    public int getCount(){
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = new DefaultInfoCell(mContext);
            viewHolder = new ViewHolder();
            viewHolder.listCell = (DefaultInfoCell) convertView;
            viewHolder.position = position;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.listCell.update(dataList.get(position).getTitle(), dataList.get(position).getVar());


        viewHolder.position = position;
        return convertView;
    }
}
