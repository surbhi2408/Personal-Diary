package com.example.diary.model;

public class Note
{
    private String title;
    private String content;
    //private String image;

    public Note()
    {}
    public Note(String title,String content)
    {
        this.title = title;
        this.content = content;
        //this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

//    public String getImage() {
//        return image;
//    }
//
//    public void setImage(String image) {
//        this.image = image;
//    }
}
