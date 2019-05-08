package com.lunamint.lunagram.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.lunamint.lunagram.ui.cell.ValidatorListCell;
import com.lunamint.wallet.model.Validator;

import java.util.ArrayList;

public class ValidatorListAdapter extends ArrayAdapter {

    private ArrayList<Validator> dataList;

    private double totalPower = 0.d;

    public ValidatorListAdapter(@NonNull Context context, int resource, ArrayList<Validator> dataList) {
        super(context, resource);
        this.dataList = dataList;
    }

    static class ViewHolder {
        ValidatorListCell listCell;
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
            convertView = new ValidatorListCell(getContext());
            viewHolder = new ViewHolder();
            viewHolder.listCell = (ValidatorListCell) convertView;
            viewHolder.position = position;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.listCell.update(dataList.get(position), totalPower);

        viewHolder.position = position;
        return convertView;
    }

    public void update(ArrayList<Validator> dataList, double totalPower) {
        this.dataList = dataList;
        setTotalPower(totalPower);
    }

    private void setTotalPower(double totalPower) {
        if (dataList == null || dataList.size() == 0) return;
        this.totalPower = totalPower;
    }

    public double getTotalPower() {
        return totalPower;
    }
}
