package com.lunamint.wallet.utils;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;

public class AnimUtil {

    public static final void changeView(View preView, View currentView, boolean isGone) {
        if (preView == null || currentView == null) return;
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(300);

        Animation leftOut = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, -1f,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0);
        leftOut.setInterpolator(new AccelerateInterpolator());
        leftOut.setDuration(300);

        AnimationSet prevAnim = new AnimationSet(false);
        prevAnim.addAnimation(leftOut);
        prevAnim.addAnimation(fadeOut);
        preView.setAnimation(prevAnim);
        if (isGone) {
            preView.setVisibility(View.GONE);
        } else {
            preView.setVisibility(View.INVISIBLE);
        }

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(300);

        Animation rightIn = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0);
        rightIn.setInterpolator(new AccelerateInterpolator());
        rightIn.setDuration(300);

        AnimationSet currentAnim = new AnimationSet(false);
        currentAnim.addAnimation(fadeIn);
        currentAnim.addAnimation(rightIn);
        currentView.setAnimation(currentAnim);
        currentView.setVisibility(View.VISIBLE);
    }

    public static final void changePrevView(View preView, View currentView, boolean isGone) {
        if (preView == null || currentView == null) return;
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setDuration(300);

        Animation leftIn = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, -1f,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0);
        leftIn.setInterpolator(new AccelerateInterpolator());
        leftIn.setDuration(300);

        AnimationSet prevAnim = new AnimationSet(false);
        prevAnim.addAnimation(leftIn);
        prevAnim.addAnimation(fadeIn);
        preView.setAnimation(prevAnim);
        preView.setVisibility(View.VISIBLE);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new DecelerateInterpolator());
        fadeOut.setDuration(300);

        Animation rightOut = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0);
        rightOut.setInterpolator(new AccelerateInterpolator());
        rightOut.setDuration(300);

        AnimationSet currentAnim = new AnimationSet(false);
        currentAnim.addAnimation(fadeOut);
        currentAnim.addAnimation(rightOut);
        currentView.setAnimation(currentAnim);

        if (isGone) {
            currentView.setVisibility(View.GONE);
        } else {
            currentView.setVisibility(View.INVISIBLE);
        }
    }

    public static final void showView(View view) {
        if (view == null) return;
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(300);

        AnimationSet currentAnim = new AnimationSet(false);
        currentAnim.addAnimation(fadeIn);
        view.setAnimation(currentAnim);
        view.setVisibility(View.VISIBLE);
    }

    public static final void hideView(View view, boolean isGone) {
        if (view == null) return;
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(300);

        Animation leftOut = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, -1f,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0);
        leftOut.setInterpolator(new AccelerateInterpolator());
        leftOut.setDuration(300);

        AnimationSet prevAnim = new AnimationSet(false);
        prevAnim.addAnimation(leftOut);
        prevAnim.addAnimation(fadeOut);
        view.setAnimation(prevAnim);
        if (isGone) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.INVISIBLE);
        }

    }
}
