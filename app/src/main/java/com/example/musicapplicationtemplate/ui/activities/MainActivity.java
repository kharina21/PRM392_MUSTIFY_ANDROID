package com.example.musicapplicationtemplate.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

import com.example.musicapplicationtemplate.R;
import com.example.musicapplicationtemplate.ui.fragments.HomeFragment;
import com.example.musicapplicationtemplate.ui.fragments.LibraryFragment;
import com.example.musicapplicationtemplate.ui.fragments.MiniPlayerFragment;
import com.example.musicapplicationtemplate.ui.fragments.ProfileFragment;
import com.example.musicapplicationtemplate.ui.fragments.SearchFragment;
import com.example.musicapplicationtemplate.utils.MusicPlayerManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private MusicPlayerManager musicPlayerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicPlayerManager = MusicPlayerManager.getInstance();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.miniPlayerFragment, new MiniPlayerFragment())
                .commit();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
    }

    public void toggleMiniPlayerVisibility(boolean isVisible) {
        MiniPlayerFragment miniPlayerFragment = (MiniPlayerFragment) getSupportFragmentManager().findFragmentById(R.id.miniPlayerFragment);
        if (miniPlayerFragment != null && miniPlayerFragment.getView() != null) {
            miniPlayerFragment.getView().setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }

    public void toggleBottomNavigationVisibility(boolean isVisible) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            float targetTranslationY = isVisible ? 0 : bottomNavigationView.getHeight();
            bottomNavigationView.animate().translationY(targetTranslationY).setDuration(300).start();
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    if (item.getItemId() == R.id.nav_home) {
                        selectedFragment = new HomeFragment();
                    } else if (item.getItemId() == R.id.nav_search) {
                        selectedFragment = new SearchFragment();
                    } else if (item.getItemId() == R.id.nav_library) {
                        selectedFragment = new LibraryFragment();
                    } else if (item.getItemId() == R.id.nav_profile) {
                        selectedFragment = new ProfileFragment();
                    }

                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, selectedFragment)
                                .commit();

                        // Chỉ hiển thị MiniPlayerFragment nếu có bài hát đang phát
                        if (musicPlayerManager.getCurrentSong() != null && musicPlayerManager.isPlaying() ) {
                            toggleMiniPlayerVisibility(true);
                        } else {
                            toggleMiniPlayerVisibility(false);
                        }
                    }

                    return true;
                }
            };
}
