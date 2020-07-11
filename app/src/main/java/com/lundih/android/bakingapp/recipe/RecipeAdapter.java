package com.lundih.android.bakingapp.recipe;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.lundih.android.bakingapp.DetailsActivity;
import com.lundih.android.bakingapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private final List<Recipe> recipes;

    public RecipeAdapter(Context context, List<Recipe> recipes) {
        this.context = context;
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecipeAdapter.RecipeItem(LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (recipes != null) {
            if (recipes.get(position).getImage() != null) {
                if (!recipes.get(position).getImage().equals("")) {
                    Picasso.get().load(recipes.get(position).getImage())
                            .placeholder(R.drawable.image_placeholder)
                            .error(R.drawable.image_placeholder)
                            .into(((RecipeItem) holder).imageViewRecipeItem);
                } else {
                    ((RecipeItem) holder).imageViewRecipeItem.setImageResource(R.drawable.image_placeholder);
                }
            }
            ((RecipeItem)holder).textViewRecipeItem .setText(recipes.get(position).getName());
            ((RecipeItem)holder).cardViewRecipeItem.setOnClickListener(view -> {
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra(context.getString(R.string.intent_key_recipe), recipes.get(position));
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        if (recipes != null) return recipes.size();
        else return 0;
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    class RecipeItem extends RecyclerView.ViewHolder {
        final CardView cardViewRecipeItem;
        final ImageView imageViewRecipeItem;
        final TextView textViewRecipeItem;

        public RecipeItem(@NonNull View recipeItem) {
            super(recipeItem);

            cardViewRecipeItem = recipeItem.findViewById(R.id.cardViewRecipeItem);
            imageViewRecipeItem = recipeItem.findViewById(R.id.imageViewRecipeItemImage);
            textViewRecipeItem = recipeItem.findViewById(R.id.textViewRecipeItemLabel);
        }
    }
}
