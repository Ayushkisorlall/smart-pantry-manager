package com.example.smartpantry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

// SQLite database: pantry items (CRUD) plus pre-loaded recipes
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, quantity REAL NOT NULL, unit TEXT NOT NULL, expiry TEXT)");
        db.execSQL("CREATE TABLE recipes (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, steps TEXT NOT NULL)");
        db.execSQL("CREATE TABLE recipe_ingredients (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "recipe_id INTEGER NOT NULL, name TEXT NOT NULL, quantity REAL NOT NULL, "
                + "unit TEXT NOT NULL, FOREIGN KEY(recipe_id) REFERENCES recipes(id))");
        seedRecipes(db); // runs once, when the database is first created
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS recipe_ingredients");
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS pantry");
        onCreate(db);
    }

    // ---------- Pantry CRUD ----------

    public long addItem(PantryItem item) {
        return getWritableDatabase().insert("pantry", null, toValues(item));
    }

    public List<PantryItem> getAllItems() {
        List<PantryItem> list = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query("pantry", null, null, null, null, null, "name COLLATE NOCASE")) {
            while (c.moveToNext()) {
                list.add(fromCursor(c));
            }
        }
        return list;
    }

    public PantryItem getItem(int id) {
        PantryItem item = null;
        try (Cursor c = getReadableDatabase().query("pantry", null, "id=?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            if (c.moveToFirst()) {
                item = fromCursor(c);
            }
        }
        return item;
    }

    public int updateItem(PantryItem item) {
        return getWritableDatabase().update("pantry", toValues(item), "id=?",
                new String[]{String.valueOf(item.id)});
    }

    public int deleteItem(int id) {
        return getWritableDatabase().delete("pantry", "id=?", new String[]{String.valueOf(id)});
    }

    private ContentValues toValues(PantryItem item) {
        ContentValues v = new ContentValues();
        v.put("name", item.name);
        v.put("quantity", item.quantity);
        v.put("unit", item.unit);
        v.put("expiry", item.expiry);
        return v;
    }

    private PantryItem fromCursor(Cursor c) {
        return new PantryItem(
                c.getInt(c.getColumnIndexOrThrow("id")),
                c.getString(c.getColumnIndexOrThrow("name")),
                c.getDouble(c.getColumnIndexOrThrow("quantity")),
                c.getString(c.getColumnIndexOrThrow("unit")),
                c.getString(c.getColumnIndexOrThrow("expiry")));
    }

    // ---------- Recipes ----------

    public List<Recipe> getAllRecipes() {
        List<Recipe> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query("recipes", null, null, null, null, null, "name")) {
            while (c.moveToNext()) {
                Recipe r = new Recipe(c.getInt(c.getColumnIndexOrThrow("id")),
                        c.getString(c.getColumnIndexOrThrow("name")),
                        c.getString(c.getColumnIndexOrThrow("steps")));
                loadIngredients(db, r);
                list.add(r);
            }
        }
        return list;
    }

    public Recipe getRecipe(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Recipe r = null;
        try (Cursor c = db.query("recipes", null, "id=?", new String[]{String.valueOf(id)}, null, null, null)) {
            if (c.moveToFirst()) {
                r = new Recipe(id, c.getString(c.getColumnIndexOrThrow("name")),
                        c.getString(c.getColumnIndexOrThrow("steps")));
                loadIngredients(db, r);
            }
        }
        return r;
    }

    private void loadIngredients(SQLiteDatabase db, Recipe r) {
        try (Cursor c = db.query("recipe_ingredients", null, "recipe_id=?",
                new String[]{String.valueOf(r.id)}, null, null, null)) {
            while (c.moveToNext()) {
                r.ingredients.add(new Ingredient(
                        c.getString(c.getColumnIndexOrThrow("name")),
                        c.getDouble(c.getColumnIndexOrThrow("quantity")),
                        c.getString(c.getColumnIndexOrThrow("unit"))));
            }
        }
    }

    // ---------- Seed data (18 recipes) ----------
    // Ingredient format: "name|quantity|unit"

    private void addRecipe(SQLiteDatabase db, String name, String steps, String... ingredients) {
        ContentValues rv = new ContentValues();
        rv.put("name", name);
        rv.put("steps", steps);
        long recipeId = db.insert("recipes", null, rv);
        for (String s : ingredients) {
            String[] p = s.split("\\|");
            ContentValues iv = new ContentValues();
            iv.put("recipe_id", recipeId);
            iv.put("name", p[0]);
            iv.put("quantity", Double.parseDouble(p[1]));
            iv.put("unit", p[2]);
            db.insert("recipe_ingredients", null, iv);
        }
    }

    private void seedRecipes(SQLiteDatabase db) {
        addRecipe(db, "Tomato Pasta",
                "1. Boil the pasta until soft.\n2. Fry garlic in olive oil.\n3. Add chopped tomatoes and cook for 10 minutes.\n4. Mix with the pasta and serve.",
                "pasta|200|g", "tomato|3|pcs", "garlic|2|pcs", "olive oil|2|tbsp");
        addRecipe(db, "Scrambled Eggs on Toast",
                "1. Toast the bread.\n2. Beat the eggs with salt.\n3. Melt butter and scramble the eggs.\n4. Serve on the toast.",
                "egg|3|pcs", "bread|2|pcs", "butter|10|g", "salt|1|tsp");
        addRecipe(db, "Egg Fried Rice",
                "1. Fry chopped onion.\n2. Add peas and rice.\n3. Push to the side and scramble the eggs.\n4. Mix and add soy sauce.",
                "rice|200|g", "egg|2|pcs", "soy sauce|2|tbsp", "onion|1|pcs", "pea|100|g");
        addRecipe(db, "Pancakes",
                "1. Mix flour, sugar, milk and eggs into a batter.\n2. Pour a small amount into a hot pan.\n3. Flip when bubbles form.",
                "flour|200|g", "milk|300|ml", "egg|2|pcs", "sugar|2|tbsp");
        addRecipe(db, "Cheese Omelette",
                "1. Beat the eggs.\n2. Melt butter in a pan and add the eggs.\n3. Add grated cheese and fold in half.",
                "egg|3|pcs", "cheese|50|g", "butter|10|g");
        addRecipe(db, "Vegetable Soup",
                "1. Chop the carrots, potatoes and onion.\n2. Add to the stock.\n3. Simmer for 25 minutes.",
                "carrot|2|pcs", "potato|2|pcs", "onion|1|pcs", "vegetable stock|500|ml");
        addRecipe(db, "Garlic Bread",
                "1. Mix soft butter with crushed garlic.\n2. Spread on the bread.\n3. Bake at 180C for 10 minutes.",
                "bread|4|pcs", "butter|50|g", "garlic|2|pcs");
        addRecipe(db, "Mashed Potatoes",
                "1. Boil the potatoes until soft.\n2. Drain and mash.\n3. Stir in butter, milk and salt.",
                "potato|4|pcs", "butter|30|g", "milk|100|ml", "salt|1|tsp");
        addRecipe(db, "Chicken Rice Bowl",
                "1. Cook the rice.\n2. Fry sliced chicken and onion in oil.\n3. Serve the chicken over the rice.",
                "chicken breast|300|g", "rice|200|g", "onion|1|pcs", "olive oil|1|tbsp");
        addRecipe(db, "Cheese Toastie",
                "1. Butter the bread.\n2. Place cheese between the slices.\n3. Fry both sides until golden.",
                "bread|2|pcs", "cheese|60|g", "butter|10|g");
        addRecipe(db, "Tuna Pasta",
                "1. Boil the pasta.\n2. Fry onion in oil.\n3. Add the tuna and mix with the pasta.",
                "pasta|200|g", "tuna|1|pcs", "onion|1|pcs", "olive oil|1|tbsp");
        addRecipe(db, "Baked Potatoes",
                "1. Prick the potatoes and bake at 200C for 50 minutes.\n2. Cut open and add butter, cheese and salt.",
                "potato|2|pcs", "butter|20|g", "cheese|40|g", "salt|1|tsp");
        addRecipe(db, "Banana Pancakes",
                "1. Mash the bananas.\n2. Mix with eggs and flour.\n3. Fry small spoonfuls until golden.",
                "banana|2|pcs", "egg|2|pcs", "flour|100|g");
        addRecipe(db, "Creamy Porridge",
                "1. Heat the oats and milk together.\n2. Stir for 5 minutes.\n3. Sweeten with sugar.",
                "oat|80|g", "milk|300|ml", "sugar|1|tbsp");
        addRecipe(db, "Egg Fried Noodles",
                "1. Boil the noodles.\n2. Fry grated carrot.\n3. Add eggs and noodles.\n4. Season with soy sauce.",
                "noodle|200|g", "egg|2|pcs", "soy sauce|2|tbsp", "carrot|1|pcs");
        addRecipe(db, "Tomato Soup",
                "1. Fry onion in butter.\n2. Add tomatoes and stock.\n3. Simmer for 20 minutes and blend.",
                "tomato|6|pcs", "onion|1|pcs", "vegetable stock|400|ml", "butter|20|g");
        addRecipe(db, "Rice and Beans",
                "1. Cook the rice.\n2. Fry onion in oil.\n3. Add beans and heat through.\n4. Serve over the rice.",
                "rice|200|g", "bean|200|g", "onion|1|pcs", "olive oil|1|tbsp");
        addRecipe(db, "Banana Smoothie",
                "1. Peel the bananas.\n2. Blend with milk and yoghurt until smooth.",
                "banana|2|pcs", "milk|250|ml", "yoghurt|100|g");
    }
}
