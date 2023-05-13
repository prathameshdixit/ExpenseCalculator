package edu.sjsu.android.expensetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.preference.Preference;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

public class SSettingsActivity extends AppCompatPreferenceActivity {
    private int oldTheme, newTheme;
    private boolean themeChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyTheme();
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_main);

        // Adding the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.about));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        // Action Listeners on click
        Preference about = findPreference(getString(R.string.about));
        about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(SSettingsActivity.this, AboutActivity.class));
                return false;
            }
        });
        Preference help =  findPreference(getString(R.string.help));
        help.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(SSettingsActivity.this, HelpActivity.class));
                return false;
            }
        });
        bindPreferenceSummaryToValue(findPreference(getString(R.string.theme)));
    }

    // Applying theme from the preferences
    private void applyTheme() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        oldTheme = Integer.parseInt(sharedPreferences.getString(getString(R.string.theme), "0"));
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

    // Change the theme
    public void changeTheme() {
        if (newTheme == 0) {
            int themeId = R.style.LightTheme;
            setTheme(themeId);
        } else if (newTheme == 1) {
            int themeId = R.style.DarkTheme;
            setTheme(themeId);
        } else {
            Toast.makeText(this, getString(R.string.no_theme), Toast.LENGTH_SHORT).show();
        }
        themeChanged = true;
        recreate();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SSettingsActivity.this, MainActivity.class));
        super.onBackPressed();
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    // Listening to preference change
    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

                newTheme = index;
                if (oldTheme != newTheme) {
                    changeTheme();
                }
            }
            return true;
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}