package com.lundih.android.bakingapp;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lundih.android.bakingapp.ingredient.Ingredient;
import com.lundih.android.bakingapp.recipe.Recipe;
import com.lundih.android.bakingapp.utils.InternetCheck;
import com.lundih.android.bakingapp.utils.RecipeAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Implementation of App Widget functionality.
 */
@SuppressWarnings("NullableProblems")
public class BakingAppWidgetProvider extends AppWidgetProvider implements Callback<List<Recipe>> {
    static AppWidgetManager appWidgetManager1;
    static ComponentName componentName;
    private static RemoteViews views;
    Context context1;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        appWidgetManager1 = AppWidgetManager.getInstance(context);
        componentName = new ComponentName(context, BakingAppWidgetProvider.class);

        // Construct the RemoteViews object
        views = new RemoteViews(context.getPackageName(), R.layout.baking_app_widget_provider);

        views.setOnClickPendingIntent(R.id.appwidgetLinearLayout, setAppPendingIntent(context));
        views.setOnClickPendingIntent(R.id.appwidgetButtonPrevious, setSelfPendingIntent(context,
                context.getString(R.string.action_appwidget_button_previous_click)));
        views.setOnClickPendingIntent(R.id.appwidgetButtonNext, setSelfPendingIntent(context,
                context.getString(R.string.action_appwidget_button_next_click)));
        views.setOnClickPendingIntent(R.id.appwidgetButtonRefresh, setSelfPendingIntent(context,
                context.getString(R.string.action_appwidget_button_refresh_click)));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        getRecipes(context);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    // Pending intent to start the app
    public static PendingIntent setAppPendingIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(context, 0, intent, 0);
    }

    // Method to create PendingIntents for appwidget views' actions
    public static PendingIntent setSelfPendingIntent(Context context, String action) {
        Intent intent = new Intent(context, BakingAppWidgetProvider.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    // onReceive perform actions depending on the view clicked in the widget
    @SuppressLint("ApplySharedPref")
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int position = sharedPreferences.getInt(context.getString(R.string.shared_preferences_key_position), 0);
        appWidgetManager1 = AppWidgetManager.getInstance(context);
        views = new RemoteViews(context.getPackageName(), R.layout.baking_app_widget_provider);
        if (context.getString(R.string.action_appwidget_button_previous_click).equals(intent.getAction())) {
            position -= 1;
            if (position >= 0) {
                sharedPreferences.edit().putInt(context.getString(R.string.shared_preferences_key_position), position).commit();
                views.setViewVisibility(R.id.appwidgetProgressBar, View.VISIBLE);
                getRecipes(context);
                appWidgetManager1.updateAppWidget(componentName, views);
            }
        } else if (context.getString(R.string.action_appwidget_button_next_click).equals(intent.getAction())) {
            position += 1;
            sharedPreferences.edit().putInt(context.getString(R.string.shared_preferences_key_position), position).commit();
            views.setViewVisibility(R.id.appwidgetProgressBar, View.VISIBLE);
            getRecipes(context);
            appWidgetManager1.updateAppWidget(componentName, views);
        } else if (context.getString(R.string.action_appwidget_button_refresh_click).equals(intent.getAction())) {
            views.setViewVisibility(R.id.appwidgetProgressBar, View.VISIBLE);
            getRecipes(context);
            appWidgetManager1.updateAppWidget(componentName, views);
        }
    }

    @SuppressLint("ApplySharedPref")
    @Override
    public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
        views.setViewVisibility(R.id.appwidgetProgressBar, View.GONE);
        if (response.isSuccessful()) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context1);
            int position = sharedPreferences.getInt(context1.getString(R.string.shared_preferences_key_position), 0);
            List<Recipe> recipes =  response.body();
            if (recipes != null) {
                if (position > recipes.size()-1) {
                    position = 0;
                    sharedPreferences.edit().putInt(context1.getString(R.string.shared_preferences_key_position), position).commit();
                } try {
                    views.setTextViewText(R.id.appwidgetTextViewRecipeTitle, recipes.get(position).getName() +
                            context1.getString(R.string.white_space) + context1.getString(R.string.opening_brace)+ (position+1) + context1.getString(R.string.forward_slash) + recipes.size() + context1.getString(R.string.closing_brace));
                    views.setTextViewText(R.id.appwidgetTextViewIngredientsLabel, context1.getString(R.string.text_view_ingredients_label));
                    views.setTextViewText(R.id.appwidgetTextViewIngredients, getIngredients(context1, recipes.get(position).getIngredients(), recipes.get(position).getServings()));
                } catch (NullPointerException e) {
                    views.setTextViewText(R.id.appwidgetTextViewRecipeTitle, "");
                    views.setTextViewText(R.id.appwidgetTextViewIngredientsLabel, "");
                    views.setTextViewText(R.id.appwidgetTextViewIngredients,context1.getString(R.string.message_get_network_resource_unsuccessful_widget));
                }
            }
        } else {
            views.setTextViewText(R.id.appwidgetTextViewRecipeTitle, "");
            views.setTextViewText(R.id.appwidgetTextViewIngredientsLabel, "");
            views.setTextViewText(R.id.appwidgetTextViewIngredients,context1.getString(R.string.message_get_network_resource_failure_widget));
        }
        appWidgetManager1.updateAppWidget(componentName, views);
    }

    @Override
    public void onFailure(Call<List<Recipe>> call, Throwable t) {
        views.setTextViewText(R.id.appwidgetTextViewRecipeTitle, "");
        views.setTextViewText(R.id.appwidgetTextViewIngredientsLabel, "");
        views.setTextViewText(R.id.appwidgetTextViewIngredients,context1.getString(R.string.message_get_network_resource_failure_widget));
        appWidgetManager1.updateAppWidget(componentName, views);
    }

    public void getRecipes(Context context) {
        context1 = context;
        appWidgetManager1 = AppWidgetManager.getInstance(context);
        componentName = new ComponentName(context, BakingAppWidgetProvider.class);
        if (InternetCheck.checkInternetConnectionState(context) && InternetCheck.canPing()) {
            Gson gson = new GsonBuilder().setLenient().create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(context.getString(R.string.url_base))
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            RecipeAPI recipeAPI = retrofit.create(RecipeAPI.class);

            Call<List<Recipe>> call = recipeAPI.getRecipes();
            call.enqueue(this);
        } else {
            views = new RemoteViews(context.getPackageName(), R.layout.baking_app_widget_provider);
            views.setTextViewText(R.id.appwidgetTextViewRecipeTitle, "");
            views.setTextViewText(R.id.appwidgetTextViewIngredientsLabel, "");
            views.setTextViewText(R.id.appwidgetTextViewIngredients,context.getString(R.string.message_no_internet));
            appWidgetManager1.updateAppWidget(componentName, views);
        }
    }


    private String getIngredients(Context context, List<Ingredient> ingredients, int servings) {
        String ingredientString = "";
        if (ingredients != null) {
            for (int i = 0; i < ingredients.size(); i++) {
                if (ingredients.get(i).getQuantity() % 1 == 0) { // if there is just a zero after the decimal point in quantity remove it
                    ingredientString = ingredientString.concat(Integer.toString(i + 1)).concat(context.getString(R.string.period)).concat(context.getString(R.string.white_space))
                            .concat(ingredients.get(i).getIngredient()).concat(context.getString(R.string.colon)).concat(context.getString(R.string.white_space))
                            .concat(Integer.toString((int) ingredients.get(i).getQuantity())).concat(context.getString(R.string.white_space))
                            .concat(ingredients.get(i).getMeasure().concat(context.getString(R.string.new_line)));
                } else {
                    ingredientString = ingredientString.concat(Integer.toString(i + 1)).concat(context.getString(R.string.period)).concat(context.getString(R.string.white_space))
                            .concat(ingredients.get(i).getIngredient()).concat(context.getString(R.string.colon)).concat(context.getString(R.string.white_space))
                            .concat(Double.toString(ingredients.get(i).getQuantity())).concat(context.getString(R.string.white_space))
                            .concat(ingredients.get(i).getMeasure().concat(context.getString(R.string.new_line)));
                }
            }
            if (servings > 0) {
                ingredientString = ingredientString.concat(context.getString(R.string.new_line)).concat(context.getString(R.string.text_view_ingredients_servings_label).concat(Integer.toString(servings))).concat(context.getString(R.string.new_line));
            }
        }

        return ingredientString;
    }
}