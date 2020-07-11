package com.lundih.android.bakingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lundih.android.bakingapp.utils.ColumnCalc;
import com.lundih.android.bakingapp.utils.InternetCheck;
import com.lundih.android.bakingapp.utils.RecipeAPI;
import com.lundih.android.bakingapp.recipe.Recipe;
import com.lundih.android.bakingapp.recipe.RecipeAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@SuppressWarnings("NullableProblems")
public class MainActivity extends AppCompatActivity implements Callback<List<Recipe>> {
    private String BASE_URL;
    List<Recipe> recipes;
    RecyclerView recyclerViewRecipes;
    ProgressBar progressBarLoadingRecipes;
    TextView textViewNetworkRequestMessage;

    CustomViewModel customViewModel;
    int screenOrientation;
    boolean isTabMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        screenOrientation = getResources().getConfiguration().orientation;
        isTabMode = findViewById(R.id.nestedScrollViewMainActivityTabMode) != null;

        BASE_URL = getString(R.string.url_base);

        recyclerViewRecipes = findViewById(R.id.recyclerViewRecipes);
        progressBarLoadingRecipes = findViewById(R.id.progressBarLoadingRecipes);
        textViewNetworkRequestMessage = findViewById(R.id.textViewNetworkResourceRequestMessage);
        FloatingActionButton buttonRefreshRecipes = findViewById(R.id.fabRefreshRecipes);

        customViewModel = new ViewModelProvider(this).get(CustomViewModel.class);
        if (customViewModel.getRecipes() == null) {
           tryToGetNetworkResource();
        } else {
            recipes = customViewModel.getRecipes();
            setUpRecyclerView(recipes);
        }

        buttonRefreshRecipes.setOnClickListener(view -> tryToGetNetworkResource());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main_activity, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_item_credits) {
            Intent intent = new Intent(this, CreditsActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
        if (response.isSuccessful()) {
            recipes =  response.body();
            customViewModel.setRecipes(recipes);
            showRecipes();
            setUpRecyclerView(recipes);
        } else {
            showMessage(R.string.message_get_network_resource_unsuccessful);
        }
    }

    @Override
    public void onFailure(Call<List<Recipe>> call, Throwable t) {
        showMessage(R.string.message_get_network_resource_failure);
    }

    public void getNetworkResource() {
        showLoading();
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RecipeAPI recipeAPI = retrofit.create(RecipeAPI.class);

        Call<List<Recipe>> call = recipeAPI.getRecipes();
        call.enqueue(this);
    }

    private void tryToGetNetworkResource () {
        if (InternetCheck.checkInternetConnectionState(this) && InternetCheck.canPing()) {
            getNetworkResource();
        } else {
            showMessage(R.string.message_no_internet);
        }
    }

    private void setUpRecyclerView(List<Recipe> recipes) {
        RecipeAdapter recipeAdapter = new RecipeAdapter(this, recipes);
        RecyclerView.LayoutManager layoutManager;
        if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE || isTabMode) {
            layoutManager = new GridLayoutManager(this, ColumnCalc.calculate(this, 400));
        } else {
            layoutManager = new LinearLayoutManager(this);
        }
        recyclerViewRecipes.setLayoutManager(layoutManager);
        recyclerViewRecipes.setAdapter(recipeAdapter);
    }

    private void showLoading() {
        recyclerViewRecipes.setVisibility(View.GONE);
        textViewNetworkRequestMessage.setVisibility(View.GONE);
        progressBarLoadingRecipes.setVisibility(View.VISIBLE);
    }

    private void showMessage(int messageID) {
        progressBarLoadingRecipes.setVisibility(View.GONE);
        recyclerViewRecipes.setVisibility(View.GONE);
        textViewNetworkRequestMessage.setVisibility(View.VISIBLE);
        textViewNetworkRequestMessage.setText(getString(messageID));
    }

    private void showRecipes() {
        progressBarLoadingRecipes.setVisibility(View.GONE);
        textViewNetworkRequestMessage.setVisibility(View.GONE);
        recyclerViewRecipes.setVisibility(View.VISIBLE);
    }
}