package com.example.smartpantry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// Core business logic: the strict-matching rule.
// A recipe is suggested ONLY if every ingredient is in the pantry
// in at least the required quantity.
public class RecipeMatcher {

    public static List<Recipe> getSuggested(List<Recipe> recipes, List<PantryItem> pantry) {
        Map<String, Double> totals = buildPantryTotals(pantry);
        List<Recipe> result = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (canMake(recipe, totals)) {
                result.add(recipe);
            }
        }
        return result;
    }

    // Adds up the pantry quantities per ingredient (e.g. two entries of "egg")
    private static Map<String, Double> buildPantryTotals(List<PantryItem> pantry) {
        Map<String, Double> totals = new HashMap<>();
        for (PantryItem item : pantry) {
            String key = makeKey(item.name, item.unit);
            double qty = UnitConverter.toBase(item.quantity, item.unit);
            Double current = totals.get(key);
            totals.put(key, (current == null ? 0 : current) + qty);
        }
        return totals;
    }

    // Returns false as soon as one ingredient is missing or too small
    private static boolean canMake(Recipe recipe, Map<String, Double> totals) {
        for (Ingredient ing : recipe.ingredients) {
            Double have = totals.get(makeKey(ing.name, ing.unit));
            double need = UnitConverter.toBase(ing.quantity, ing.unit);
            if (have == null || have < need) {
                return false;
            }
        }
        return true;
    }

    private static String makeKey(String name, String unit) {
        return normalizeName(name) + "|" + UnitConverter.type(unit);
    }

    // Lower-case, trim and make the last word singular so
    // "Tomatoes" and "tomato" are treated as the same ingredient
    public static String normalizeName(String name) {
        String n = name.trim().toLowerCase(Locale.US).replaceAll("\\s+", " ");
        if (n.endsWith("ies") && n.length() > 3) {
            return n.substring(0, n.length() - 3) + "y";
        }
        if (n.endsWith("oes")) {
            return n.substring(0, n.length() - 2);
        }
        if (n.endsWith("s") && !n.endsWith("ss") && n.length() > 2) {
            return n.substring(0, n.length() - 1);
        }
        return n;
    }
}
