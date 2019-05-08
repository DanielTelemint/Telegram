package com.lunamint.lunagram.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.lunamint.lunagram.ui.cell.GovernanceListCell;
import com.lunamint.wallet.model.Proposal;

import java.util.ArrayList;

public class GovernanceListAdapter extends ArrayAdapter {

    private Context mContext;
    private ArrayList<Proposal> dataList;

    public GovernanceListAdapter(@NonNull Context context, int resource, ArrayList<Proposal> dataList) {
        super(context, resource);

        this.mContext = context;
        this.dataList = dataList;
    }

    static class ViewHolder {
        GovernanceListCell listCell;
        int position;
    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = new GovernanceListCell(mContext);
            viewHolder = new ViewHolder();
            viewHolder.listCell = (GovernanceListCell) convertView;
            viewHolder.position = position;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.listCell.update(dataList.get(position));
        viewHolder.position = position;
        return convertView;
    }

    public void update(ArrayList<Proposal> dataList){
        this.dataList = dataList;
    }
}
