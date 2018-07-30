package xyz.lebalex.mysongs;

public class SongModel {
    private String fileName;
    private String songName;

    public SongModel(String fileName, String songName) {
        this.fileName = fileName;
        this.songName = songName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }
}
