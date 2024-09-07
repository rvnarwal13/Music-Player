package com.ravi.djmusic.ui.fragments.audio;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
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
import java.util.Objects;
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
    private ImageButton stopMusic, reverse10Sec, playPrev, playPause, playNext, forward10Sec, repeatToggle;
    private ImageView mediaImage;
    private TextView mediaName, artistName, albumName, totalTime, timeElapsed;
    private static boolean isStop = false;
    private Handler handler;
    private AudioFileMetaData audioFileMetaData;
    private static int toggleMusic = 0;
    private BarVisualizer barVisualizer;

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_audio_player, container, false);

        barVisualizer = view.findViewById(R.id.audio_visualizer);
        seekBar = view.findViewById(R.id.music_progress);
        stopMusic = view.findViewById(R.id.stop_music);
        reverse10Sec = view.findViewById(R.id.reverse_10sec);
        playPrev = view.findViewById(R.id.play_prev);
        playPause = view.findViewById(R.id.play_pause);
        playNext = view.findViewById(R.id.play_next);
        forward10Sec = view.findViewById(R.id.forward_10sec);
        repeatToggle = view.findViewById(R.id.repeat_toggle);
        mediaImage = view.findViewById(R.id.music_image);
        mediaName = view.findViewById(R.id.music_name);
        artistName = view.findViewById(R.id.artist_name);
        albumName = view.findViewById(R.id.album_name);
        timeElapsed = view.findViewById(R.id.time_elapsed);
        totalTime = view.findViewById(R.id.total_time);

        barVisualizer.setColor(ContextCompat.getColor(requireContext(), R.color.white));
        barVisualizer.setDensity(70);
        barVisualizer.setPlayer(mediaPlayer.getAudioSessionId());

        audioFiles = (List<MediaFile>) getArguments().getSerializable("list");
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

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                try {
                    mediaPlayer.release();
                    playNextAudio();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        try {
            playMusic();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

        reverse10Sec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10000);
            }
        });

        forward10Sec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
            }
        });

        stopMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                seekBar.setProgress(0);
                playPause.setBackground(requireContext().getDrawable(R.drawable.play_music));
                isStop = true;
                timeElapsed.setText("00:00");
            }
        });

        playPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    playPrevAudio();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        playNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    playNextAudio();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        repeatToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

        return view;
    }

    private void playPrevAudio() throws IOException {
        if (toggleMusic == REPEAT_ALL) {
            if (position == 0) {
                position = audioFiles.size() - 1;
            } else {
                position--;
            }
        } else if (toggleMusic == REPEAT_ONE) {
            // do nothing
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
        } else if (toggleMusic == REPEAT_ONE) {
            // do nothing
        } else if (toggleMusic == SHUFFLE) {
            Random random = new Random();
            position = random.nextInt(audioFiles.size());
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
            e.printStackTrace();
        }
    }

    private String formatDuration(Duration duration) {
        long seconds = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            seconds = duration.getSeconds();
        }
        long absSeconds = Math.abs(seconds);
        String formattedDuration = String.format("%02d:%02d", absSeconds / 60, absSeconds % 60);
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

    private void pauseMusicPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            mediaPlayer.release();
        }
    }

    private void destroyMusicPlayer() {
        if (handler != null) {
            handler.removeCallbacks(updateSeekBarProgress);
        }

        if (mediaPlayer != null) {
            mediaPlayer.pause();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        destroyMusicPlayer();
    }
}