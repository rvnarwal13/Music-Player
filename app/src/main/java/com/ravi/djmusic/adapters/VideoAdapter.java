package com.ravi.djmusic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ravi.djmusic.R;
import com.ravi.djmusic.dataobjects.MediaFile;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    private List<MediaFile> videoFiles;
    private VideoFileClickListener videoFileClickListener;

    public VideoAdapter(List<MediaFile> videoFiles, VideoFileClickListener videoFileClickListener) {
        this.videoFiles = videoFiles;
        this.videoFileClickListener = videoFileClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_files_list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MediaFile videoFile = videoFiles.get(position);
        holder.videoNameTextView.setText(videoFile.getName());
    }

    @Override
    public int getItemCount() {
        return videoFiles.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView videoNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            videoNameTextView = itemView.findViewById(R.id.files);

            itemView.setOnClickListener(v -> {
                if(videoFileClickListener != null) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION) {
                        videoFileClickListener.onVideoFileClick(videoFiles, position);
                    }
                }
            });
        }
    }

    public interface VideoFileClickListener {
        void onVideoFileClick(List<MediaFile> videoFiles, int position);
    }
}
