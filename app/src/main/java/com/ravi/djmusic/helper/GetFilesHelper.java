package com.ravi.djmusic.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.ravi.djmusic.dataobjects.MediaFile;

import java.util.ArrayList;
import java.util.List;

public class GetFilesHelper {
    public static List<MediaFile> getAllAudioFilesFromStorage(Context context) {
        List<MediaFile> audioFiles = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA};

        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";

        Cursor cursor = context.getContentResolver().query(uri, projection, selection, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String folderPath = path.substring(0, path.lastIndexOf('/')); // Extract folder path
                audioFiles.add(new MediaFile(name, path, folderPath));
            }
            cursor.close();
        }
        return audioFiles;
    }

    public static List<MediaFile> getAllVideoFilesFromStorage(Context context) {
        List<MediaFile> videoFiles = new ArrayList<>();

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DATA};

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                String folderPath = path.substring(0, path.lastIndexOf('/')); // Extract folder path
                videoFiles.add(new MediaFile(name, path, folderPath));
            }
            cursor.close();
        }
        return videoFiles;
    }
}
