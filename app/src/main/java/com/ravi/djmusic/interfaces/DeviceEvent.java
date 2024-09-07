package com.ravi.djmusic.interfaces;

import com.ravi.djmusic.dataobjects.MediaFile;

import java.util.List;

public interface DeviceEvent {
    void createAudioFragment(String folderPath);
    void createAudioPlayerFragment(List<MediaFile> audioFiles, int position);
    void createVideoFragment(String folderPath);
    void createVideoPlayerFragment(List<MediaFile> videoFiles, int position);
}
