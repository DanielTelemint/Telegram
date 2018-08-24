package com.telemint.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.telemint.messenger.R;
import com.telemint.ui.component.CardAdapter;

import org.telegram.ui.Components.LayoutHelper;


public class CardFragment extends LinearLayout {

    private CardView cardView;

    public CardFragment(@NonNull Context context, int position) {
        super(context);

        if(position != 3){
            LayoutInflater inflater = (LayoutInflater)   context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            cardView = (CardView) inflater.inflate(R.layout.sample_cardview, null);
            cardView.setMaxCardElevation(cardView.getCardElevation() * CardAdapter.MAX_ELEVATION_FACTOR);
            addView(cardView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

            TextView title = (TextView) findViewById(R.id.title);
            Button button = (Button) findViewById(R.id.button);


            title.setText("Acc name "+position);
        }else{
            LayoutInflater inflater = (LayoutInflater)   context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            cardView = (CardView) inflater.inflate(R.layout.sample_cardview2, null);
            cardView.setMaxCardElevation(cardView.getCardElevation() * CardAdapter.MAX_ELEVATION_FACTOR);
            addView(cardView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        }


    }

    public CardView getCardView() {
        return cardView;
    }
}
