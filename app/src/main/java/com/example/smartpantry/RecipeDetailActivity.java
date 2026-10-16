package com.example.smartpantry;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

// Recipe Detail screen: full ingredient list and method
public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        ScreenUtils.fixInsets(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Read the recipe id sent by the Intent
        int recipeId = getIntent().getIntExtra("recipe_id", -1);
        Recipe recipe = new DatabaseHelper(this).getRecipe(recipeId);
        if (recipe == null) {
            finish();
            return;
        }

        setTitle(recipe.name);
        ((TextView) findViewById(R.id.textDetailName)).setText(recipe.name);

        // Build the ingredient list as text
        StringBuilder sb = new StringBuilder();
        for (Ingredient ing : recipe.ingredients) {
            sb.append("\u2022 ").append(UnitConverter.formatQty(ing.quantity)).append(" ")
                    .append(ing.unit).append(" ").append(ing.name).append("\n");
        }
        ((TextView) findViewById(R.id.textDetailIngredients)).setText(sb.toString().trim());
        ((TextView) findViewById(R.id.textDetailSteps)).setText(recipe.steps);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}