package com.ravi.djmusic.ui.fragments.video;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import com.ravi.djmusic.R;
import com.ravi.djmusic.dataobjects.MediaFile;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoPlayerFragment extends Fragment {
    private VideoView videoView;
    private MediaController mediaController;
    private List<MediaFile> videoFiles;
    private int position = 0;

    public void setMediaController(MediaController mediaController) {
        this.mediaController = mediaController;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_player, container, false);

        videoView = view.findViewById(R.id.video_view);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videoFiles = (List<MediaFile>) getArguments().getSerializable("list");
        position = getArguments().getInt("position");

        videoView.setVideoURI(Uri.parse(videoFiles.get(position).getPath()));

        videoView.setOnPreparedListener(mp -> {
            int videoWidth = mp.getVideoWidth();
            int videoHeight = mp.getVideoHeight();

            // Get the dimensions of the VideoView
            int viewWidth = videoView.getWidth();
            int viewHeight = videoView.getHeight();

            // Calculate the scaling factors to fit the video within the view
            float scaleX = (float) viewWidth / videoWidth;
            float scaleY = (float) viewHeight / videoHeight;
            float scale = Math.min(scaleX, scaleY);

            // Apply the scaling to the VideoView
            videoView.setScaleX(scale);
            videoView.setScaleY(scale);
        });

        videoView.start();

        return view;
    }


}