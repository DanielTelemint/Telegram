package com.lunamint.lunagram.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.lunamint.lunagram.ui.cell.TokenListCell;
import com.lunamint.wallet.model.Coin;

import java.util.ArrayList;

public class TokenListAdapter extends ArrayAdapter {

    private Context mContext;
    private ArrayList<Coin> dataList;

    public TokenListAdapter(@NonNull Context context, int resource, ArrayList<Coin> dataList) {
        super(context, resource);

        this.mContext = context;
        this.dataList = dataList;
    }

    static class ViewHolder {
        TokenListCell listCell;
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
            convertView = new TokenListCell(mContext);
            viewHolder = new ViewHolder();
            viewHolder.listCell = (TokenListCell) convertView;
            viewHolder.position = position;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.listCell.update(dataList.get(position));
        viewHolder.position = position;
        return convertView;
    }
}
