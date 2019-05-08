package com.lunamint.lunagram.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.lunamint.lunagram.ui.cell.SettingsCell;
import com.lunamint.wallet.model.Setting;

public class SettingsAdapter extends ArrayAdapter {

    private Context mContext;
    private boolean canUseFingerprint;
    private Setting setting;

    public SettingsAdapter(@NonNull Context context, int resource, boolean canUseFingerprint) {
        super(context, resource);

        this.mContext = context;
        this.canUseFingerprint = canUseFingerprint;
    }

    static class ViewHolder {
        SettingsCell listCell;
        int position;
    }

    @Override
    public int getCount(){
        return canUseFingerprint ? 5 : 4;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = new SettingsCell(mContext, canUseFingerprint);
            viewHolder = new ViewHolder();
            viewHolder.listCell = (SettingsCell) convertView;
            viewHolder.position = position;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.listCell.update(position, setting);
        viewHolder.position = position;
        return convertView;
    }

    public void update(Setting setting){
        this.setting = setting;
    }
}
