package com.example.smartpantry;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

// Settings screen: toggle for expiring-soon alerts
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ScreenUtils.fixInsets(this);
        setTitle("Settings");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Settings are saved with SharedPreferences
        SharedPreferences prefs = getSharedPreferences("pantry_settings", Context.MODE_PRIVATE);
        SwitchMaterial switchAlerts = findViewById(R.id.switchAlerts);
        switchAlerts.setChecked(prefs.getBoolean("expiry_alerts", true));
        switchAlerts.setOnCheckedChangeListener((button, checked) ->
                prefs.edit().putBoolean("expiry_alerts", checked).apply());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}