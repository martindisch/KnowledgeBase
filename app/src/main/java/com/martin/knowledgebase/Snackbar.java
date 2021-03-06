package com.martin.knowledgebase;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Path;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Snackbar {

    private RelativeLayout mSnackbar;
    private TextView mSnackButton, mSnackText;
    private Context mContext;
    private boolean mVisible;

    public Snackbar(RelativeLayout snackbar, String text, Context context) {
        mSnackbar = snackbar;
        mSnackButton = (TextView) mSnackbar.findViewById(R.id.snackbar_button);
        mSnackText = (TextView) mSnackbar.findViewById(R.id.snackbar_text);

        mSnackText.setText(text);
        mContext = context;

        mSnackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSnackbar();
            }
        });

        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, mContext.getResources().getDisplayMetrics());
        mSnackbar.setY(mSnackbar.getY() + px);
        mSnackbar.setVisibility(View.VISIBLE);
        mVisible = false;
    }

    public Snackbar(RelativeLayout snackbar, Context context) {
        mSnackbar = snackbar;
        mSnackButton = (TextView) mSnackbar.findViewById(R.id.snackbar_button);
        mSnackText = (TextView) mSnackbar.findViewById(R.id.snackbar_text);

        mContext = context;

        mSnackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSnackbar();
            }
        });

        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, mContext.getResources().getDisplayMetrics());
        mSnackbar.setY(mSnackbar.getY() + px);
        mSnackbar.setVisibility(View.VISIBLE);
        mVisible = false;
    }

    public void show() {
        if (mContext != null) {
            if (!mVisible) {
                float px = -TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, mContext.getResources().getDisplayMetrics());
                moveSnackbar(px);
                mVisible = true;
            }
        } else {
            Log.e("Snackbar", "Reference to old Context - reinstantiate Snackbar with current Context");
        }
    }

    public void setText(String text) {
        mSnackText.setText(text);
    }

    public void hideSnackbar() {
        if (mContext != null) {
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, mContext.getResources().getDisplayMetrics());
            moveSnackbar(px);
            mVisible = false;
        } else {
            Log.e("Snackbar", "Reference to old Context - reinstantiate Snackbar with current Context");
        }
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