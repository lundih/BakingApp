package com.lundih.android.bakingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.lundih.android.bakingapp.Recipe.Recipe;

import java.util.Objects;

public class DetailsActivity extends AppCompatActivity {
    CustomViewModel customViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        customViewModel = new ViewModelProvider(this).get(CustomViewModel.class);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            customViewModel.setLandscape(true);
        } else {
            customViewModel.setLandscape(false);
        }

        Intent intent  = getIntent();
        if (intent != null) {
            Recipe recipe = intent.getParcelableExtra(getString(R.string.intent_key_recipe));
            if (recipe != null) {
                // Intent extra was not null, put the recipe in the ViewModel so it can be passed to the fragments
                customViewModel.setRecipe(recipe);
                if (recipe.getName() != null) setTitle(recipe.getName());
            }
        }
        if (findViewById(R.id.linearLayoutTabMode) == null) {
            customViewModel.setTabMode(false);
            setUpStepsFragment(R.id.frameLayoutDetailsActivityContainer);
        } else {
            customViewModel.setTabMode(true);
            setUpStepsFragment(R.id.frameLayoutFragmentStepsContainer);
            setUpInstructionsFragment();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();

        return true;
    }

    @Override
    public void onBackPressed() {
        // Check if currently displayed fragment is InstructionsFragment
        // If true then make position in ViewModel null
        if (!customViewModel.isTabMode()) {
            StepsFragment stepsFragment = (StepsFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.fragment_tag_steps));
            try {
                InstructionsFragment instructionsFragment;
                if (stepsFragment != null) {
                    instructionsFragment = (InstructionsFragment) Objects.requireNonNull(stepsFragment.getActivity())
                            .getSupportFragmentManager()
                            .findFragmentByTag(getString(R.string.fragment_tag_instructions));
                    if (instructionsFragment != null && instructionsFragment.isVisible()) {
                        customViewModel.setPositionLiveData(null);
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        super.onBackPressed();
    }

    private void setUpStepsFragment(int fragmentContainer) {
        StepsFragment stepsFragment = new StepsFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(fragmentContainer, stepsFragment, getString(R.string.fragment_tag_steps)).commit();
    }

    private void setUpInstructionsFragment() {
        InstructionsFragment instructionsFragment = new InstructionsFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayoutFragmentInstructionsContainer, instructionsFragment, getString(R.string.fragment_tag_instructions)).commit();
    }
}