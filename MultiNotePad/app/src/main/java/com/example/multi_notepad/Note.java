package com.example.multi_notepad;

import androidx.annotation.NonNull;
import java.io.Serializable;


public class Note implements Serializable{
    private String title;
    private String noteText;
    private String modifiedTime;
    private static  int ctr = 1;

    Note(String title, String noteText,String modifiedTime ) {
        this.title = title;
        this.noteText = noteText;
        this.modifiedTime = modifiedTime;
    }

    public String getDescription() {
        return noteText;
    }

    void setDescription(String noteText) {
        this.noteText = noteText;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return modifiedTime;
    }

    public void setDate(String modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public Note() {
//        this.title = "Title Name " + ctr;
//        this.noteText = "Written Notes " + ctr;
        ctr++;
    }

    @NonNull
    @Override
    public String toString() {
        return "Notes {" + "title= "+ title + '\''+ ", noteText=" + noteText +", last update time=" + modifiedTime + '}';
    }
}
