package com.telemint.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;

import com.telemint.ui.component.CardAdapter;

import java.util.ArrayList;
import java.util.List;

public class CardFragmentPagerAdapter extends PagerAdapter implements CardAdapter {

    private List<CardFragment> fragments;
    private float baseElevation;
    private Context mContext;



    public CardFragmentPagerAdapter(Context context, float baseElevation) {
        fragments = new ArrayList<>();
        this.baseElevation = baseElevation;
        this.mContext = context;

        for(int i = 0; i< 4; i++){
            fragments.add(new CardFragment(context, i));
        }
    }

    @Override
    public float getBaseElevation() {
        return baseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return fragments.get(position).getCardView();
    }


    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }


    @Override
    public Object instantiateItem(ViewGroup collection, int position) {

        CardFragment cardView = fragments.get(position);
        collection.addView(cardView);
        return cardView;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }


}
