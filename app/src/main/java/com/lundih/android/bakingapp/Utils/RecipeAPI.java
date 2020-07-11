package com.lundih.android.bakingapp.Utils;

import com.lundih.android.bakingapp.Recipe.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RecipeAPI {

    @GET("baking.json")
    Call<List<Recipe>> getRecipes();
}
