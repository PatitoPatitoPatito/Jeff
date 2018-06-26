package xyz.misterkozo.rcjeff;

import android.graphics.Bitmap;

public class Video {

    public String url, thumb, name, date;
    public String token = null;
    public Bitmap bitthumb;
    public String key;

    public Video() {}

    public Video(String url, String thumb, String name, String date) {
        this.url  = url;
        this.thumb = thumb;
        this.name = name;
        this.date = date;
    }

    public Video(String url) {
        this.url = url;
        this.thumb = thumb;
        //TODO: inferring
    }

}
