package com.lundih.android.bakingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreditsActivity extends AppCompatActivity {
    @BindView(R.id.textViewCredits1) TextView textView;
    @BindView(R.id.textViewCredits2) TextView textView2;
    @BindView(R.id.textViewCredits3) TextView textView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.credits_title));
        }
        // Set hyperlink in the textView to be clickable
        TextView[] textViews = {textView, textView2, textView3};
        for (TextView currentTextView: textViews)
            currentTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();

        return true;
    }
}
