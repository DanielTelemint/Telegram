package com.lunamint.lunagram.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.lunamint.lunagram.ui.cell.TransactionHistoryCell;
import com.lunamint.wallet.model.TransactionHistory;

import java.util.ArrayList;

public class TransactionHistoryAdapter extends ArrayAdapter {

    private Context mContext;
    private ArrayList<TransactionHistory> dataList;
    private String address;

    public TransactionHistoryAdapter(@NonNull Context context, int resource, String address, ArrayList<TransactionHistory> dataList) {
        super(context, resource);

        this.mContext = context;
        this.address = address;
        this.dataList = dataList;
    }

    static class ViewHolder {
        TransactionHistoryCell listCell;
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
            convertView = new TransactionHistoryCell(mContext);
            viewHolder = new ViewHolder();
            viewHolder.listCell = (TransactionHistoryCell) convertView;
            viewHolder.position = position;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.listCell.update(address, dataList.get(position));
        viewHolder.position = position;
        return convertView;
    }
}
