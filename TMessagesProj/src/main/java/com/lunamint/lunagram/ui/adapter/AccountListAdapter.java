package com.lunamint.lunagram.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.lunamint.lunagram.ui.cell.AccountListCell;
import com.lunamint.wallet.model.AccountInfo;

import java.util.ArrayList;

public class AccountListAdapter extends ArrayAdapter {

    private Context mContext;
    private ArrayList<AccountInfo> dataList;

    public AccountListAdapter(@NonNull Context context, int resource, ArrayList<AccountInfo> accountList) {
        super(context, resource);

        this.mContext = context;
        this.dataList = accountList;
    }

    static class ViewHolder {
        AccountListCell listCell;
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
            convertView = new AccountListCell(mContext);
            viewHolder = new ViewHolder();
            viewHolder.listCell = (AccountListCell) convertView;
            viewHolder.position = position;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.listCell.update(dataList.get(position));
        viewHolder.position = position;
        return convertView;
    }

    public void update(ArrayList<AccountInfo> accountList){
        this.dataList = accountList;
    }
}
