package com.telemint.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.telemint.ui.cell.ValidatorListCell;
import com.telemint.ui.model.ValidatorList;

import java.util.ArrayList;

public class ValidatorListAdapter extends ArrayAdapter {

    private Context mContext;
    private ArrayList<ValidatorList> dataList;

    public ValidatorListAdapter(@NonNull Context context, int resource, ArrayList<ValidatorList> dataList) {
        super(context, resource);

        this.mContext = context;
        this.dataList = dataList;
    }

    static class ViewHolder {
        ValidatorListCell listCell;
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
            convertView = new ValidatorListCell(mContext);
            viewHolder = new ViewHolder();
            viewHolder.listCell = (ValidatorListCell) convertView;
            viewHolder.position = position;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.listCell.update(position, dataList.get(position).getName(), dataList.get(position).getFee(), dataList.get(position).getVotingPower());


        viewHolder.position = position;
        return convertView;
    }
}
