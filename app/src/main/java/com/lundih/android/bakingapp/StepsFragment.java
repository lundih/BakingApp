package com.lundih.android.bakingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lundih.android.bakingapp.ingredient.Ingredient;
import com.lundih.android.bakingapp.recipe.Recipe;
import com.lundih.android.bakingapp.step.Step;
import com.lundih.android.bakingapp.step.StepAdapter;

import java.util.List;
import java.util.Objects;

public class StepsFragment extends Fragment implements StepAdapter.OnStepItemClickedListener {
    CustomViewModel customViewModel;

    public StepsFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ingredients_and_steps, container, false);

        TextView textViewIngredients = rootView.findViewById(R.id.textViewIngredients);
        RecyclerView recyclerViewSteps = rootView.findViewById(R.id.recyclerViewSteps);

        // Use the ViewModel instance of the parent activity of this Fragment
        customViewModel = new ViewModelProvider(requireActivity()).get(CustomViewModel.class);

        Recipe recipe = customViewModel.getRecipe();
        if (recipe != null) {
            List<Ingredient> ingredients = recipe.getIngredients();
            List<Step> steps = recipe.getSteps();
            int servings = recipe.getServings();
            String ingredientString = "";
            if (ingredients != null) {
                for (int i = 0; i < ingredients.size(); i++) {
                    if (ingredients.get(i).getQuantity()%1 == 0) { // if there is just a zero after the decimal point in quantity remove it
                        ingredientString = ingredientString.concat(Integer.toString(i + 1)).concat(getString(R.string.period)).concat(getString(R.string.white_space))
                                .concat(ingredients.get(i).getIngredient()).concat(getString(R.string.colon)).concat(getString(R.string.white_space))
                                .concat(Integer.toString((int)ingredients.get(i).getQuantity())).concat(getString(R.string.white_space))
                                .concat(ingredients.get(i).getMeasure().concat(getString(R.string.new_line)));
                    } else {
                        ingredientString = ingredientString.concat(Integer.toString(i + 1)).concat(getString(R.string.period)).concat(getString(R.string.white_space))
                                .concat(ingredients.get(i).getIngredient()).concat(getString(R.string.colon)).concat(getString(R.string.white_space))
                                .concat(Double.toString(ingredients.get(i).getQuantity())).concat(getString(R.string.white_space))
                                .concat(ingredients.get(i).getMeasure().concat(getString(R.string.new_line)));
                    }
                }
                if (servings > 0) {
                    ingredientString = ingredientString.concat(getString(R.string.new_line)).concat(getString(R.string.text_view_ingredients_servings_label).concat(Integer.toString(servings))).concat(getString(R.string.new_line));
                }
                textViewIngredients.setText(ingredientString);
            }

            if (steps != null) {
                StepAdapter stepAdapter = new StepAdapter(getContext(), steps, StepsFragment.this, customViewModel.isTabMode(), customViewModel.getPosition());
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                recyclerViewSteps.setLayoutManager(layoutManager);
                recyclerViewSteps.setAdapter(stepAdapter);

                // If positionLiveData in the ViewModel is not null then InstructionsFragment was started then destroyed
                //probably by some config changes
                if (customViewModel.getPositionLiveData() != null) {
                    if (!customViewModel.isTabMode()) {
                        InstructionsFragment instructionsFragment = new InstructionsFragment();
                        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                        fragmentManager.popBackStack(getString(R.string.fragment_back_stack_name_instructions), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        fragmentManager.beginTransaction()
                                .add(R.id.frameLayoutDetailsActivityContainer, instructionsFragment, getString(R.string.fragment_tag_instructions))
                                .addToBackStack(getString(R.string.fragment_back_stack_name_instructions))
                                .commit();
                    }
                } else {
                    final NestedScrollView nestedScrollView = rootView.findViewById(R.id.nestedScrollViewIngredientsAndSteps);
                    nestedScrollView.post(() -> nestedScrollView.fullScroll(ScrollView.FOCUS_UP));
                }
            }
        }

        return rootView;
    }

    @Override
    public void onStepItemClicked(int position) {
        customViewModel.setPosition(position);
        if (!customViewModel.isTabMode()) {
            InstructionsFragment instructionsFragment = new InstructionsFragment();
            FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.frameLayoutDetailsActivityContainer, instructionsFragment, getString(R.string.fragment_tag_instructions))
                    .addToBackStack(getString(R.string.fragment_back_stack_name_instructions))
                    .commit();
        }
    }
}
