package edu.sjsu.android.expensetracker;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragmentCompat {

    private int oldTheme, newTheme;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyTheme();

        // Getting menu from XML file
        addPreferencesFromResource(R.xml.pref_main);

        // Action Listeners
        Preference about = findPreference(getString(R.string.about));
        about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getActivity(), AboutActivity.class));
                return false;
            }
        });
        Preference help =  findPreference(getString(R.string.help));
        help.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getActivity(), HelpActivity.class));
                return false;
            }
        });
        Preference language =  findPreference(getString(R.string.language));
        language.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(Settings.ACTION_LOCALE_SETTINGS));
                return true;
            }
        });
        ListPreference mListPreference = (ListPreference) getPreferenceManager().findPreference(getString(R.string.theme));
        mListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
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
                        ((SettingsActivity)getActivity()).changeTheme(newTheme);
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }

    // Applying theme from the preferences
    private void applyTheme() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        oldTheme = Integer.parseInt(sharedPreferences.getString(getString(R.string.theme), "0"));
        if (oldTheme == 0) {
            int themeId = R.style.LightTheme;
            getActivity().setTheme(themeId);
        } else if (oldTheme == 1) {
            int themeId = R.style.DarkTheme;
            getActivity().setTheme(themeId);
        } else {
            Toast.makeText(getContext(), getString(R.string.no_theme), Toast.LENGTH_SHORT).show();
        }
    }

}