package com.example.smartpantry;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Suggested Recipes screen: only recipes the user can make right now
public class SuggestedRecipesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested);
        setTitle("Suggested Recipes");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        DatabaseHelper db = new DatabaseHelper(this);
        // Run the strict-matching logic against the current pantry
        List<Recipe> suggested = RecipeMatcher.getSuggested(db.getAllRecipes(), db.getAllItems());

        RecyclerView recyclerView = findViewById(R.id.recyclerRecipes);
        TextView textEmpty = findViewById(R.id.textEmptyRecipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecipeAdapter(suggested, recipe ->
                Toast.makeText(this, recipe.name, Toast.LENGTH_SHORT).show()));

        // Feedback when nothing matches
        textEmpty.setVisibility(suggested.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}