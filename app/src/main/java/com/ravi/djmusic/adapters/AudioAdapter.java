package com.ravi.djmusic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ravi.djmusic.dataobjects.MediaFile;
import com.ravi.djmusic.R;

import java.util.List;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder> {
    private List<MediaFile> audioFiles;
    private AudioFileClickListener audioFileClickListener;

    public AudioAdapter(List<MediaFile> audioFiles, AudioFileClickListener audioFileClickListener) {
        this.audioFiles = audioFiles;
        this.audioFileClickListener = audioFileClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_files_list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MediaFile audioFile = audioFiles.get(position);
        holder.audioNameTextView.setText(audioFile.getName());
    }

    @Override
    public int getItemCount() {
        return audioFiles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView audioNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            audioNameTextView = itemView.findViewById(R.id.files);

            itemView.setOnClickListener(v -> {
                if (audioFileClickListener != null) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION) {
                        audioFileClickListener.onAudioFileClick(audioFiles, position);
                    }
                }
            });
        }
    }

    public interface AudioFileClickListener {
        void onAudioFileClick(List<MediaFile> audioFiles, int position);
    }
}

