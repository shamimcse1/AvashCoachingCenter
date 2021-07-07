package codercamp.com.avashcoachingcenter.Model;

import androidx.annotation.Keep;

@Keep
public class NoticeDataModel {
    public  String title,imageUrl,date,time,key;

    public NoticeDataModel() {
    }

    public NoticeDataModel(String title, String imageUrl, String date, String time, String key) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.date = date;
        this.time = time;
        this.key = key;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getKey() {
        return key;
    }
}
