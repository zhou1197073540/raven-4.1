package com.tenthousand.fileupload.bean;

public class IIRIReportIndexBean {

    String fid;
    String title;
    String author;
    String key_words;
    String date;
    String summary;
    String html_path;
    String img_path;
    String create_date;
    String create_time;

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getHtml_path() {
        return html_path;
    }

    public void setHtml_path(String html_path) {
        this.html_path = html_path;
    }

    public String getImg_path() {
        return img_path;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getKey_words() {
        return key_words;
    }

    public void setKey_words(String key_words) {
        this.key_words = key_words;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    @Override
    public String toString() {
        return "IIRIReportIndexBean{" +
                "fid='" + fid + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", key_words='" + key_words + '\'' +
                ", date='" + date + '\'' +
                ", summary='" + summary + '\'' +
                ", html_path='" + html_path + '\'' +
                ", img_path='" + img_path + '\'' +
                ", create_date='" + create_date + '\'' +
                ", create_time='" + create_time + '\'' +
                '}';
    }
}
