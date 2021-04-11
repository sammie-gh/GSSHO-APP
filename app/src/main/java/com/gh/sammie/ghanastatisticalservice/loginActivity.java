package com.gh.sammie.ghanastatisticalservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gh.sammie.ghanastatisticalservice.Utils.ProgressBarAnimator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
import java.util.Objects;

public class loginActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView textView, title, title2;
    private static final String START_TAG = "START_LOG";
    private FirebaseAuth firebaseAuth;
    private TextView startFeedbackText;
    private ProgressBar startProgress;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            startProgress.setVisibility(View.VISIBLE);
            startFeedbackText.setText("Creating Account...");
            //Create a new account
            firebaseAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        startFeedbackText.setText("Account Created Succesful ...");
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        startProgress.setVisibility(View.GONE);
                        startFeedbackText.setText(Objects.requireNonNull(task.getException()).getMessage());
                        Toast.makeText(loginActivity.this, "" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d(START_TAG, "Start Log : " + Objects.requireNonNull(task.getException()).getMessage());
                    }
                }
            });
        } else {
            //Navigate to Homepage
            startFeedbackText.setText("Logged in...");
            progressAnimation();

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//
//        getWindow().setFlags(SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION,
//                SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        firebaseAuth = FirebaseAuth.getInstance();
        initViews();

        progressBar.setMax(100);
        progressBar.setScaleY(3f);
    }


    private void initViews() {
        startFeedbackText = findViewById(R.id.start_feedback);
        startFeedbackText.setText("Checking User Account...");
        progressBar = findViewById(R.id.progress_bar);
        textView = findViewById(R.id.textView);
        startProgress = findViewById(R.id.start_progress);

        title = findViewById(R.id.title2);
        progressBar = findViewById(R.id.progress_bar);
        title2 = findViewById(R.id.title2);

    }

    private void progressAnimation() {
        ProgressBarAnimator animator = new ProgressBarAnimator(this, progressBar, textView,
                0f, 100f);
        animator.setDuration(1000);
        progressBar.setAnimation(animator);
    }



}