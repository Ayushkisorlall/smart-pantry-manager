# Smart Pantry Manager

An Android app written in Java that helps reduce food waste. The user records
the ingredients they have at home, and the app suggests only the recipes they
can cook right now using those ingredients, with no shopping trip needed.

Built for the Mobile App Development 700 practical assignment.

## Features
- Add, edit and delete pantry items (name, quantity, unit, optional expiry date)
- Input validation on the add/edit form
- Pantry list screen using a RecyclerView with a custom adapter
- 18 recipes pre-loaded into the database on first run
- Suggested Recipes screen using strict matching: a recipe is shown only if
  every ingredient is in the pantry in at least the required quantity
- Matching handles plural names (tomato / tomatoes) and unit differences
  (for example 0.1 kg equals 100 g)
- Recipe Detail screen with the full ingredient list and method
- Settings screen with a toggle for expiring-soon highlights
- A message is shown when no recipes match the pantry

## Database choice
SQLite, using SQLiteOpenHelper. I chose it because it runs on the device with
no internet or server, it is covered in the module's persistent data chapter,
and it is simple to set up for a small app that needs full CRUD and data that
survives closing the app.

## Screens
1. Pantry List (home)
2. Add / Edit Ingredient
3. Suggested Recipes
4. Recipe Detail
5. Settings

## Setup and run instructions
1. Install Android Studio.
2. Clone this repository:
   `git clone https://github.com/Ayushkisorlall/smart-pantry-manager.git`
3. Open the project in Android Studio and wait for Gradle to sync.
4. Connect an Android phone (with USB debugging on) or start an emulator.
5. Click Run. The minimum supported version is Android 7.0 (API 24).

## Project structure
- `DatabaseHelper.java` - SQLite tables, CRUD methods and recipe seed data
- `RecipeMatcher.java` - the strict-matching logic
- `UnitConverter.java` - unit conversion helper
- `PantryAdapter.java` and `RecipeAdapter.java` - RecyclerView adapters
- `MainActivity`, `AddEditActivity`, `SuggestedRecipesActivity`,
  `RecipeDetailActivity` and `SettingsActivity` - the five screens

## Author
Ayush Kisorlall, [402410864]