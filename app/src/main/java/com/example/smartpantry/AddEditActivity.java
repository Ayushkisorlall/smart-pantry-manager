package com.example.smartpantry;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

// Add/Edit Ingredient screen
public class AddEditActivity extends AppCompatActivity {

    private EditText editName, editQuantity, editExpiry;
    private Spinner spinnerUnit;
    private String[] units;
    private DatabaseHelper db;
    private int itemId = -1; // -1 means we are adding a new item

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        ScreenUtils.fixInsets(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = new DatabaseHelper(this);
        editName = findViewById(R.id.editName);
        editQuantity = findViewById(R.id.editQuantity);
        editExpiry = findViewById(R.id.editExpiry);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        Button buttonSave = findViewById(R.id.buttonSave);

        units = getResources().getStringArray(R.array.units);
        spinnerUnit.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, units));

        editExpiry.setOnClickListener(v -> showDatePicker());
        editExpiry.setOnLongClickListener(v -> {
            editExpiry.setText(""); // long press clears the date
            return true;
        });

        // Read the id sent by the Intent (if any)
        itemId = getIntent().getIntExtra("item_id", -1);
        if (itemId != -1) {
            setTitle("Edit Ingredient");
            loadItem();
        } else {
            setTitle("Add Ingredient");
        }

        buttonSave.setOnClickListener(v -> saveItem());
    }

    private void loadItem() {
        PantryItem item = db.getItem(itemId);
        if (item == null) {
            finish();
            return;
        }
        editName.setText(item.name);
        editQuantity.setText(UnitConverter.formatQty(item.quantity));
        editExpiry.setText(item.expiry);
        for (int i = 0; i < units.length; i++) {
            if (units[i].equals(item.unit)) {
                spinnerUnit.setSelection(i);
            }
        }
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) ->
                editExpiry.setText(String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day)),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveItem() {
        String name = editName.getText().toString().trim();
        String qtyText = editQuantity.getText().toString().trim();

        // Input validation
        if (name.isEmpty()) {
            editName.setError("Please enter an ingredient name");
            return;
        }
        if (qtyText.isEmpty()) {
            editQuantity.setError("Please enter a quantity");
            return;
        }
        double quantity;
        try {
            quantity = Double.parseDouble(qtyText);
        } catch (NumberFormatException e) {
            editQuantity.setError("Quantity must be a number");
            return;
        }
        if (quantity <= 0) {
            editQuantity.setError("Quantity must be greater than 0");
            return;
        }

        String unit = spinnerUnit.getSelectedItem().toString();
        String expiry = editExpiry.getText().toString().trim();
        PantryItem item = new PantryItem(itemId, name, quantity, unit, expiry);

        if (itemId == -1) {
            db.addItem(item);
            Toast.makeText(this, "Ingredient added", Toast.LENGTH_SHORT).show();
        } else {
            db.updateItem(item);
            Toast.makeText(this, "Ingredient updated", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}