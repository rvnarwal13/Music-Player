package com.ravi.djmusic.ui.fragments.audio;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chibde.visualizer.BarVisualizer;
import com.ravi.djmusic.dataobjects.AudioFileMetaData;
import com.ravi.djmusic.dataobjects.MediaFile;
import com.ravi.djmusic.helper.AudioMetadataHelper;
import com.ravi.djmusic.R;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class AudioPlayerFragment extends Fragment {
    private final int REPEAT_ALL = 0;
    private final int REPEAT_ONE = 1;
    private final int SHUFFLE = 2;
    private List<MediaFile> audioFiles;
    private int position = 0;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private ImageButton playPause;
    private ImageButton repeatToggle;
    private ImageView mediaImage;
    private TextView mediaName, artistName, albumName, totalTime, timeElapsed;
    private static boolean isStop = false;
    private Handler handler;
    private AudioFileMetaData audioFileMetaData;
    private static int toggleMusic = 0;
    private BarVisualizer barVisualizer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_audio_player, container, false);

        barVisualizer = view.findViewById(R.id.audio_visualizer);
        seekBar = view.findViewById(R.id.music_progress);
        ImageButton stopMusic = view.findViewById(R.id.stop_music);
        ImageButton reverse10Sec = view.findViewById(R.id.reverse_10sec);
        ImageButton playPrev = view.findViewById(R.id.play_prev);
        playPause = view.findViewById(R.id.play_pause);
        ImageButton playNext = view.findViewById(R.id.play_next);
        ImageButton forward10Sec = view.findViewById(R.id.forward_10sec);
        repeatToggle = view.findViewById(R.id.repeat_toggle);
        mediaImage = view.findViewById(R.id.music_image);
        mediaName = view.findViewById(R.id.music_name);
        artistName = view.findViewById(R.id.artist_name);
        albumName = view.findViewById(R.id.album_name);
        timeElapsed = view.findViewById(R.id.time_elapsed);
        totalTime = view.findViewById(R.id.total_time);
        mediaPlayer = new MediaPlayer();
        MusicPlayerLifecycleObserver musicPlayerLifecycleObserver = new MusicPlayerLifecycleObserver(mediaPlayer);
        getLifecycle().addObserver(musicPlayerLifecycleObserver);

        barVisualizer.setColor(ContextCompat.getColor(requireContext(), R.color.white));
        barVisualizer.setDensity(70);
        barVisualizer.setPlayer(mediaPlayer.getAudioSessionId());

        assert getArguments() != null;
        if (requireArguments().getSerializable("list") instanceof List<?>) {
            audioFiles = (List<MediaFile>) requireArguments().getSerializable("list");
        }
        position = getArguments().getInt("position");

        handler = new Handler();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    mediaPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            try {
//                mediaPlayer.release();
                playNextAudio();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        try {
            playMusic();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        playPause.setOnClickListener(view1 -> {
            if (audioFileMetaData != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    playPause.setBackground(requireContext().getDrawable(R.drawable.play_music));
                } else {
                    if (isStop) {
                        try {
                            playMusic();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        mediaPlayer.start();
                        playPause.setBackground(requireContext().getDrawable(R.drawable.pause_music));
                        handler.postDelayed(updateSeekBarProgress, 10);
                    }
                }
            } else {
                Toast.makeText(getContext(), "Unable to play media.", Toast.LENGTH_SHORT).show();
            }
        });

        reverse10Sec.setOnClickListener(view17 -> mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10000));

        forward10Sec.setOnClickListener(view13 -> mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000));

        stopMusic.setOnClickListener(view12 -> {
            mediaPlayer.stop();
            seekBar.setProgress(0);
            playPause.setBackground(requireContext().getDrawable(R.drawable.play_music));
            isStop = true;
            timeElapsed.setText("00:00");
        });

        playPrev.setOnClickListener(view14 -> {
            try {
                playPrevAudio();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        playNext.setOnClickListener(view15 -> {
            try {
                playNextAudio();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        repeatToggle.setOnClickListener(view16 -> {
            if (toggleMusic == REPEAT_ALL) {
                toggleMusic = REPEAT_ONE;
                repeatToggle.setBackground(requireContext().getDrawable(R.drawable.repeat_one));
            } else if (toggleMusic == REPEAT_ONE) {
                toggleMusic = SHUFFLE;
                repeatToggle.setBackground(requireContext().getDrawable(R.drawable.shuffle));
            } else if (toggleMusic == SHUFFLE) {
                toggleMusic = REPEAT_ALL;
                repeatToggle.setBackground(requireContext().getDrawable(R.drawable.repeat_all));
            }
        });

        return view;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onResume() {
        super.onResume();
        if (isStop) {
            try {
                playMusic();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            mediaPlayer.start();
            playPause.setBackground(requireContext().getDrawable(R.drawable.pause_music));
            handler.postDelayed(updateSeekBarProgress, 10);
        }
    }

    private void playPrevAudio() throws IOException {
        if (toggleMusic == REPEAT_ALL) {
            if (position == 0) {
                position = audioFiles.size() - 1;
            } else {
                position--;
            }
        } else if (toggleMusic == SHUFFLE) {
            Random random = new Random();
            position = random.nextInt(audioFiles.size());
        }
        playMusic();
    }

    private void playNextAudio() throws IOException {
        if (toggleMusic == REPEAT_ALL) {
            if (position == audioFiles.size() - 1) {
                position = 0;
            } else {
                position++;
            }
        } else if (toggleMusic == SHUFFLE) {
            Random random = new Random();
            position = random.nextInt(1000)%audioFiles.size();
        }
        playMusic();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void playMusic() throws IOException {
        setMusicContent();
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioFiles.get(position).getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            playPause.setBackground(requireContext().getDrawable(R.drawable.pause_music));
            seekBar.setMax(mediaPlayer.getDuration());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                totalTime.setText(formatDuration(Duration.ofMillis(mediaPlayer.getDuration())));
            }
            handler.postDelayed(updateSeekBarProgress, 10);
        } catch (IOException e) {
            Log.d("AudioPlayerFragment", e.toString());
        }
    }

    private String formatDuration(Duration duration) {
        long seconds = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            seconds = duration.getSeconds();
        }
        long absSeconds = Math.abs(seconds);
        @SuppressLint("DefaultLocale") String formattedDuration = String.format("%02d:%02d", absSeconds / 60, absSeconds % 60);
        return seconds < 0 ? "-" + formattedDuration : formattedDuration;
    }

    private void setMusicContent() throws IOException {
        audioFileMetaData = AudioMetadataHelper.getAudioMetadataString(audioFiles.get(position));
        if (audioFileMetaData != null) {
            mediaName.setText(audioFiles.get(position).getName());
            if (audioFileMetaData.getArtist() != null) {
                artistName.setText(audioFileMetaData.getArtist());
            }
            if (audioFileMetaData.getAlbum() != null) {
                albumName.setText(audioFileMetaData.getAlbum());
            }
            if (audioFileMetaData.getAlbumArt() != null) {
                mediaImage.setImageBitmap(audioFileMetaData.getAlbumArt());
                mediaImage.setVisibility(View.VISIBLE);
                barVisualizer.setVisibility(View.GONE);
            } else {
                barVisualizer.setVisibility(View.VISIBLE);
                mediaImage.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(getContext(), "Unable to play media.", Toast.LENGTH_SHORT).show();
        }
    }

    private final Runnable updateSeekBarProgress = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer.isPlaying()) {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                handler.postDelayed(this, 10);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    timeElapsed.setText(formatDuration(Duration.ofMillis(mediaPlayer.getCurrentPosition())));
                }
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }

    static class MusicPlayerLifecycleObserver implements DefaultLifecycleObserver {
        private final MediaPlayer mediaPlayer;

        public MusicPlayerLifecycleObserver(MediaPlayer mediaPlayer) {
            this.mediaPlayer = mediaPlayer;
        }

        @Override
        public void onStart(@NonNull LifecycleOwner owner) {
            mediaPlayer.start();
        }

        @Override
        public void onStop(@NonNull LifecycleOwner owner) {
            mediaPlayer.pause();
        }
    }
}