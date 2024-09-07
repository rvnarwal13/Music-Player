package com.ravi.djmusic.ui.avtivities;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.MediaController;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ravi.djmusic.ui.fragments.audio.AudioFilesFragment;
import com.ravi.djmusic.ui.fragments.audio.AudioPlayerFragment;
import com.ravi.djmusic.interfaces.DeviceEvent;
import com.ravi.djmusic.ui.fragments.audio.AudioFolderFragment;
import com.ravi.djmusic.dataobjects.MediaFile;
import com.ravi.djmusic.R;
import com.ravi.djmusic.ui.fragments.profile.ProfileFragment;
import com.ravi.djmusic.ui.fragments.video.VideoFilesFragment;
import com.ravi.djmusic.ui.fragments.video.VideoFolderFragment;
import com.ravi.djmusic.ui.fragments.video.VideoPlayerFragment;

import java.io.Serializable;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DeviceEvent {

    private static final String[] permissionsToGrant = {
            "android.permission.READ_MEDIA_AUDIO",
            "android.permission.READ_MEDIA_VIDEO",
            "android.permission.RECORD_AUDIO"
    };
    private ActivityResultLauncher<String[]> grantPermissions;

    private BottomNavigationView bottomNavigationView;

    private MediaPlayer mediaPlayer;

    private MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = new MediaPlayer();
        mediaController = new MediaController(this);

        bottomNavigationView = findViewById(R.id.bottom_nav_view);

        grantPermissions = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                permissions -> {
                    boolean isGranted = true;
                    for (String permission : permissions.keySet()) {
                        if (!Boolean.TRUE.equals(permissions.get(permission))) {
                            isGranted = false;
                            break;
                        }
                    }
                    if (isGranted) {
                        loadFolderFragments();
                    } else {
                        grantPermissions();
                    }
                }
        );

        if (savedInstanceState == null) {
            grantPermissions();
        }

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                if (item.getItemId() == R.id.action_video) {
                    selectedFragment = new VideoFolderFragment();
                } else if (item.getItemId() == R.id.action_audio) {
                    selectedFragment = new AudioFolderFragment();
                } else if (item.getItemId() == R.id.action_profile) {
                    selectedFragment = new ProfileFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_loader, selectedFragment)
                            .commit();
                }

                return true;
            }
        });
    }

    public void loadFolderFragments() {
        VideoFolderFragment videoFolderFragment = new VideoFolderFragment();
        loadFragment(videoFolderFragment, false);
    }

    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.replace(R.id.fragment_loader, fragment);
        transaction.commit();
    }

    @Override
    public void createAudioFragment(String folderPath) {
        AudioFilesFragment audioFilesFragment = new AudioFilesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("folderPath", folderPath);
        audioFilesFragment.setArguments(bundle);
        loadFragment(audioFilesFragment, true);
    }

    @Override
    public void createAudioPlayerFragment(List<MediaFile> audioFiles, int position) {
        AudioPlayerFragment audioPlayerFragment = new AudioPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("list", (Serializable) audioFiles);
        bundle.putInt("position", position);
        audioPlayerFragment.setArguments(bundle);
        audioPlayerFragment.setMediaPlayer(new MediaPlayer());
        loadFragment(audioPlayerFragment, true);
    }

    @Override
    public void createVideoFragment(String folderPath) {
        VideoFilesFragment videoFilesFragment = new VideoFilesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("folderPath", folderPath);
        videoFilesFragment.setArguments(bundle);
        loadFragment(videoFilesFragment, true);
    }

    @Override
    public void createVideoPlayerFragment(List<MediaFile> videoFiles, int position) {
        VideoPlayerFragment videoPlayerFragment = new VideoPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("list", (Serializable) videoFiles);
        bundle.putInt("position", position);
        videoPlayerFragment.setArguments(bundle);
        videoPlayerFragment.setMediaController(mediaController);
        loadFragment(videoPlayerFragment, true);
    }

    private void grantPermissions() {
        boolean allPermissionsGranted = true;
        for (String permission : permissionsToGrant) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }
        if (!allPermissionsGranted) {
            grantPermissions.launch(permissionsToGrant);
        } else {
            loadFolderFragments();
        }
    }
}