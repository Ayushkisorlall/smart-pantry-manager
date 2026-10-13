package com.example.smartpantry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// Represents one ingredient the user has at home
public class PantryItem {
    public int id;
    public String name;
    public double quantity;
    public String unit;
    public String expiry; // format yyyy-MM-dd, may be empty

    public PantryItem(int id, String name, double quantity, String unit, String expiry) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiry = expiry;
    }

    // True if the item expires within 3 days (or is already expired)
    public boolean isExpiringSoon() {
        if (expiry == null || expiry.isEmpty()) return false;
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(expiry);
            long days = (date.getTime() - System.currentTimeMillis()) / (24L * 60 * 60 * 1000);
            return days <= 3;
        } catch (ParseException e) {
            return false;
        }
    }
}
