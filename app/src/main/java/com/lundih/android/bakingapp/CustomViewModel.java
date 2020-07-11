package com.lundih.android.bakingapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.lundih.android.bakingapp.recipe.Recipe;

import java.util.List;

public class CustomViewModel extends AndroidViewModel {
    private boolean isLandscape = false;
    private boolean isTabMode = false;
    private List<Recipe> recipes = null;
    private Recipe recipe = null;
    private MutableLiveData<Integer> position;

    public CustomViewModel(@NonNull Application application) {
        super(application);
    }

    public boolean isLandscape() {
        return isLandscape;
    }

    public boolean isTabMode() {
        return isTabMode;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public int getPosition() {
        if (position == null || position.getValue() == null) return 0;
        else return position.getValue();
    }

    public MutableLiveData<Integer> getPositionLiveData() {
        return position;
    }

    public void setLandscape(boolean landscape) {
        isLandscape = landscape;
    }

    public void setTabMode(boolean tabMode) {
        isTabMode = tabMode;
    }

    public void setPositionLiveData(MutableLiveData<Integer> position) {
        this.position = position;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public void setPosition(int position) {
        if (this.position == null)
            this.position = new MutableLiveData<>();
        this.position.setValue(position);
    }
}
