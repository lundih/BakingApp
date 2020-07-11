package com.lundih.android.bakingapp.Ingredient;

import android.os.Parcel;
import android.os.Parcelable;

public class Ingredient implements Parcelable {
    // In case the ingredient needs to be persisted in a database
    private final int recipeID;
    private final double quantity;
    private final String measure;
    private final String ingredient;

    public Ingredient(int recipeID, int quantity, String measure, String ingredient) {
        this.recipeID = recipeID;
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(recipeID);
        parcel.writeDouble(quantity);
        parcel.writeString(measure);
        parcel.writeString(ingredient);
    }

    public static final Parcelable.Creator<Ingredient> CREATOR = new Parcelable.Creator<Ingredient>(){

        @Override
        public Ingredient createFromParcel(Parcel parcel) {
            return new Ingredient(parcel);
        }

        @Override
        public Ingredient[] newArray(int i) {
            return new Ingredient[0];
        }
    };

    private Ingredient(Parcel in) {
        recipeID = in.readInt();
        quantity = in.readDouble();
        measure = in.readString();
        ingredient = in.readString();
    }

    public int getRecipeID() {
        return recipeID;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public String getIngredient() {
        return ingredient;
    }
}
