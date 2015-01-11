package com.martin.knowledgebase;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Path;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Snackbar {

    private RelativeLayout mSnackbar;
    private TextView mSnackButton;
    private Context mContext;

    public Snackbar(RelativeLayout mSnackbar, TextView mSnackButton, Context mContext) {
        this.mSnackbar = mSnackbar;
        this.mSnackButton = mSnackButton;
        // I know, we're just waiting to leak a context here...
        this.mContext = mContext;

        mSnackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSnackbar();
            }
        });

        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, mContext.getResources().getDisplayMetrics());
        mSnackbar.setY(mSnackbar.getY() + px);
        mSnackbar.setVisibility(mSnackbar.VISIBLE);
    }

    public void showSnackbar() {
        float px = -TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, mContext.getResources().getDisplayMetrics());
        moveSnackbar(px);
    }

    public void hideSnackbar() {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, mContext.getResources().getDisplayMetrics());
        moveSnackbar(px);
    }

    private void moveSnackbar(float px) {
        Path p = new Path();

        ObjectAnimator sbAnimator;
        p.moveTo(mSnackbar.getX(), mSnackbar.getY());
        p.rLineTo(0, px);
        sbAnimator = ObjectAnimator.ofFloat(mSnackbar, View.X, View.Y, p);

        sbAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        sbAnimator.start();
    }

}
