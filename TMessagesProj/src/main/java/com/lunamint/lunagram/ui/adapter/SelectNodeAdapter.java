package com.lunamint.lunagram.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.lunamint.lunagram.ui.cell.NodeCell;
import com.lunamint.wallet.model.BlockchainInfo;

public class SelectNodeAdapter extends ArrayAdapter {

    private Context mContext;
    private BlockchainInfo blockchainInfo;

    public SelectNodeAdapter(@NonNull Context context, int resource, BlockchainInfo blockchainInfo) {
        super(context, resource);

        this.mContext = context;
        this.blockchainInfo = blockchainInfo;
    }

    static class ViewHolder {
        NodeCell listCell;
        int position;
    }

    @Override
    public int getCount(){
        return blockchainInfo == null || blockchainInfo.getList() == null ? 0 : blockchainInfo.getList().size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = new NodeCell(mContext);
            viewHolder = new ViewHolder();
            viewHolder.listCell = (NodeCell) convertView;
            viewHolder.position = position;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.listCell.update(blockchainInfo.getList().get(position));
        viewHolder.position = position;
        return convertView;
    }

    public void update(BlockchainInfo blockchainInfo){
        this.blockchainInfo = blockchainInfo;
    }
}
