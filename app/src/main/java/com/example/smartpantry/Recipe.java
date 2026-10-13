package com.example.smartpantry;

import java.util.ArrayList;
import java.util.List;

// A recipe with its ingredients and preparation steps
public class Recipe {
    public int id;
    public String name;
    public String steps;
    public List<Ingredient> ingredients = new ArrayList<>();

    public Recipe(int id, String name, String steps) {
        this.id = id;
        this.name = name;
        this.steps = steps;
    }
}
