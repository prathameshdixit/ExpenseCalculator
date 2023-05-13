package edu.sjsu.android.expensetracker;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, HistoryFragment.OnFragmentInteractionListener, StatsFragment.OnFragmentInteractionListener {

    private BottomNavigationView navigation;
    private FragmentManager manager;

    private int selectedItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        applyTheme();

        setContentView(R.layout.main_activity);

        // Initializing bottom navbar
        makeBottomNavBar();


    }

    @Override
    public void onBackPressed() {
        Fragment frag = manager.findFragmentById(R.id.frame_layout);

        if (frag instanceof HomeFragment) {
            super.onBackPressed();
        } else {
            manager.beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
            navigation.setSelectedItemId(R.id.navigation_home);
        }
    }

    // Applying theme from the preferences
    private void applyTheme() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPreferences.getString(getString(R.string.theme), "0");
        switch (theme) {
            case "0": {
                int themeId = R.style.LightTheme;
                setTheme(themeId);
                break;
            }
            case "1": {
                int themeId = R.style.DarkTheme;
                setTheme(themeId);
                break;
            }
            default:
                Toast.makeText(this, getString(R.string.no_theme), Toast.LENGTH_SHORT).show();
                break;
        }

    }

    private void makeBottomNavBar() {
        manager = getSupportFragmentManager();
        navigation = findViewById(R.id.navigation);

        // Retrieve the ID of the selected item from SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int selectedItemId = preferences.getInt("selectedItemId", R.id.navigation_home);
        navigation.setSelectedItemId(selectedItemId);

        System.out.println(selectedItemId);


        System.out.println(selectedItemId);

        boolean x = navigate(selectedItemId);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                return navigate(menuItem.getItemId());
            }
        });
    }

    // Navigate to other components
    public boolean navigate(int itemId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Fragment currentFragment = manager.findFragmentById(R.id.frame_layout);
        Fragment selectedFragment = null;

        switch (itemId) {
            case R.id.navigation_history:
                selectedFragment = new HistoryFragment();
                break;
            case R.id.navigation_home:
                selectedFragment = new HomeFragment();
                break;
            case R.id.navigation_stats:
                selectedFragment = new StatsFragment();
                break;
            case R.id.navigation_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                finish();
                return true;
            default:
                selectedFragment = new HomeFragment();
                break;
        }

        if (selectedFragment != null) {
            if (currentFragment != null && currentFragment.getClass().equals(selectedFragment.getClass())) {
                // Reloading fragment if it is already displayed
                manager.beginTransaction().detach(currentFragment).attach(currentFragment).commit();
            } else {
                // Replacing fragment
                manager.beginTransaction().replace(R.id.frame_layout, selectedFragment).commit();
            }

            // Saving the ID of the selected item in SharedPreferences
            preferences.edit().putInt("selectedItemId", itemId).apply();
        }

        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
