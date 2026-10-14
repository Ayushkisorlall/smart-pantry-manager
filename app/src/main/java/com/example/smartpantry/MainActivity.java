package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

// Pantry List screen (home screen)
public class MainActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private PantryAdapter adapter;
    private TextView textEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("My Pantry");

        db = new DatabaseHelper(this);
        textEmpty = findViewById(R.id.textEmpty);

        RecyclerView recyclerView = findViewById(R.id.recyclerPantry);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PantryAdapter(new ArrayList<>(), new PantryAdapter.Listener() {
            @Override
            public void onEdit(PantryItem item) {
                // added in the next commit
            }

            @Override
            public void onDelete(PantryItem item) {
                // added in the next commit
            }
        });
        recyclerView.setAdapter(adapter);

        // The + button opens the Add Ingredient screen
        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> startActivity(new Intent(this, AddEditActivity.class)));
    }

    // Reload the list every time the screen comes back into view
    @Override
    protected void onResume() {
        super.onResume();
        loadItems();
    }

    private void loadItems() {
        List<PantryItem> items = db.getAllItems();
        adapter.setItems(items, true);
        textEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
    }
}