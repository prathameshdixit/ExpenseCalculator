package edu.sjsu.android.expensetracker;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyTheme();
        setContentView(R.layout.about_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.about));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }
    private void applyTheme() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this );
        int oldTheme = Integer.parseInt(sharedPreferences.getString("Theme", "0"));
        if(oldTheme ==0){
            int themeId = R.style.LightTheme;
            setTheme(themeId);
        }
        else if(oldTheme ==1){
            int themeId = R.style.DarkTheme;
            setTheme(themeId);
        }
        else {
            Toast.makeText(this, "No theme", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
