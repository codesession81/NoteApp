package com.example.noteapp;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "note_table")
public class Note {


    /*
    Romm erzeugt automatisch Spalten für diese Felder
     */

    @PrimaryKey(autoGenerate = true)//Mit jedem neuen Datensatz wird die ID automatisch hochgezählt
    private int id;


    private String title;
    private String description;
    private int priority;


    //Konstruktor
    public Note(String title, String description, int priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
    }


    //Setter

    public void setId(int id) {
        this.id = id;
    }


    //Getter

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }
}
