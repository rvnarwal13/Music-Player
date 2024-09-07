package com.ravi.djmusic.dataobjects;

public class MediaFile {
    private String name;
    private String path;
    private String folderPath;

    public MediaFile(String name, String path, String folderPath) {
        this.name = name;
        this.path = path;
        this.folderPath = folderPath;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
}
