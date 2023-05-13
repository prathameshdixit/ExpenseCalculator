package edu.sjsu.android.expensetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        // Initializing toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.settings));
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.settings_fragment, new SettingsFragment()).commit();
    }

    // Applying theme from the preferences
    private void applyTheme() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int oldTheme = Integer.parseInt(sharedPreferences.getString(getString(R.string.theme), "0"));
        if (oldTheme == 0) {
            int themeId = R.style.LightTheme;
            setTheme(themeId);
        } else if (oldTheme == 1) {
            int themeId = R.style.DarkTheme;
            setTheme(themeId);
        } else {
            Toast.makeText(this, getString(R.string.no_theme), Toast.LENGTH_SHORT).show();
        }
    }

    // Change theme
    public void changeTheme(int newTheme) {
        if (newTheme == 0) {
            int themeId = R.style.LightTheme;
            setTheme(themeId);
        } else if (newTheme == 1) {
            int themeId = R.style.DarkTheme;
            setTheme(themeId);
        } else {
            Toast.makeText(this, "No theme", Toast.LENGTH_SHORT).show();
        }
        recreate();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SettingsActivity.this, MainActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}