package com.ravi.djmusic.ui.fragments.audio;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ravi.djmusic.interfaces.DeviceEvent;
import com.ravi.djmusic.adapters.FolderAdapter;
import com.ravi.djmusic.helper.GetFilesHelper;
import com.ravi.djmusic.dataobjects.MediaFile;
import com.ravi.djmusic.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class AudioFolderFragment extends Fragment implements FolderAdapter.FolderClickListener {

    private DeviceEvent deviceEvent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_audio_folder, container, false);

        if(isAdded()) {
            deviceEvent = (DeviceEvent) getActivity();
        }

        RecyclerView recyclerView = view.findViewById(R.id.audio_folders_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<String> folderPaths = getDistinctFolderPaths(GetFilesHelper.getAllAudioFilesFromStorage(requireContext()));
        FolderAdapter folderAdapter = new FolderAdapter(folderPaths, this);
        recyclerView.setAdapter(folderAdapter);

        return view;
    }

    private List<String> getFolderNames(List<String> folderPaths) {
        List<String> folderNames = new ArrayList<>();

        for(String folderPath : folderPaths) {
            folderNames.add(folderPath.substring(folderPath.lastIndexOf('/')+1));
        }

        return folderNames;
    }

    private List<String> getDistinctFolderPaths(List<MediaFile> audioFiles) {
        Set<String> folderPathsSet = new HashSet<>();
        for (MediaFile audioFile : audioFiles) {
            folderPathsSet.add(audioFile.getFolderPath());
        }

        return new ArrayList<>(folderPathsSet);
    }

    @Override
    public void onFolderClick(String folderPath) {
        if(deviceEvent != null) {
            deviceEvent.createAudioFragment(folderPath);
        }
    }
}