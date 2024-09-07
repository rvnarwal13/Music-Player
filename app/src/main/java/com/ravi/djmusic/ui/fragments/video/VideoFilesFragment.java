package com.ravi.djmusic.ui.fragments.video;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ravi.djmusic.R;
import com.ravi.djmusic.adapters.AudioAdapter;
import com.ravi.djmusic.adapters.VideoAdapter;
import com.ravi.djmusic.dataobjects.MediaFile;
import com.ravi.djmusic.helper.GetFilesHelper;
import com.ravi.djmusic.interfaces.DeviceEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFilesFragment extends Fragment implements VideoAdapter.VideoFileClickListener {

    private DeviceEvent deviceEvent;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_files, container, false);

        if(isAdded()) {
            deviceEvent = (DeviceEvent) getActivity();
        }

        RecyclerView recyclerView = view.findViewById(R.id.video_files_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        String folderPath = getArguments().getString("folderPath");
        List<MediaFile> videoFilesInFolder = getVideoFilesInFolder(GetFilesHelper.getAllVideoFilesFromStorage(requireContext()), folderPath);
        VideoAdapter videoAdapter = new VideoAdapter(videoFilesInFolder, this);
        recyclerView.setAdapter(videoAdapter);

        return view;
    }

    private List<MediaFile> getVideoFilesInFolder(List<MediaFile> allAudioFilesFromStorage, String folderPath) {
        List<MediaFile> audioFilesInFolder = new ArrayList<>();
        for (MediaFile audioFile : allAudioFilesFromStorage) {
            if (audioFile.getFolderPath().equals(folderPath)) {
                audioFilesInFolder.add(audioFile);
            }
        }
        return audioFilesInFolder;
    }

    @Override
    public void onVideoFileClick(List<MediaFile> videoFiles, int position) {
        deviceEvent.createVideoPlayerFragment(videoFiles, position);
    }
}