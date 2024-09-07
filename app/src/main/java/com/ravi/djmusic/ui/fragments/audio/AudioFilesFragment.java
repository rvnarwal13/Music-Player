package com.ravi.djmusic.ui.fragments.audio;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ravi.djmusic.adapters.AudioAdapter;
import com.ravi.djmusic.interfaces.DeviceEvent;
import com.ravi.djmusic.helper.GetFilesHelper;
import com.ravi.djmusic.dataobjects.MediaFile;
import com.ravi.djmusic.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AudioFilesFragment extends Fragment implements AudioAdapter.AudioFileClickListener {

    private DeviceEvent deviceEvent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_audio_files, container, false);

        if(isAdded()) {
            deviceEvent = (DeviceEvent) getActivity();
        }

        RecyclerView recyclerView = view.findViewById(R.id.audio_files_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        String folderPath = getArguments().getString("folderPath");
        List<MediaFile> audioFilesInFolder = getAudioFilesInFolder(GetFilesHelper.getAllAudioFilesFromStorage(requireContext()), folderPath);
        AudioAdapter audioAdapter = new AudioAdapter(audioFilesInFolder, this);
        recyclerView.setAdapter(audioAdapter);

        return view;
    }

    private List<MediaFile> getAudioFilesInFolder(List<MediaFile> allAudioFilesFromStorage, String folderPath) {
        List<MediaFile> audioFilesInFolder = new ArrayList<>();
        for (MediaFile audioFile : allAudioFilesFromStorage) {
            if (audioFile.getFolderPath().equals(folderPath)) {
                audioFilesInFolder.add(audioFile);
            }
        }
        return audioFilesInFolder;
    }

    @Override
    public void onAudioFileClick(List<MediaFile> audioFiles, int position) {
        deviceEvent.createAudioPlayerFragment(audioFiles, position);
    }
}