package com.gh.sammie.ghanastatisticalservice.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gh.sammie.ghanastatisticalservice.MainActivity;


public class ProgressBarAnimator extends Animation {
    private Context context;
    private ProgressBar progressBar;
    private TextView textView;
    private float from;
    private float to;


    public ProgressBarAnimator(Context context, ProgressBar progressBar, TextView textView, float from, float to) {
        this.context = context;
        this.progressBar = progressBar;
        this.textView = textView;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) * interpolatedTime;
        progressBar.setProgress((int) value);

        textView.setText("loading " + (int) value + " %");

        if (value == to) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);


        }
    }

}
