package com.example.noteapp;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

//Ein Data Acces Objekt kann ein Interface oder eine abstrakre Klasse sein
@Dao
public interface NoteDao {

    /*
    Hier werden alle notwendigen DB-Operationen als abstrakte Methode
    definiert,durch die Annotations weiß Room automatisch, wie es die Operationen ausführen soll.
    Tippfehler werden mit einer Fehlermeldung sichtbar, so kann die App nicht gestartet werden
     */

    @Insert
    void insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);


    /*
     Room besitzt nicht für alle möglichen DB-Operationen, die entsprechende Logik, mit der Annotation
     "@Query" können eigene Operationen definiert werden
     */
    @Query("DELETE FROM note_table")
    void deleteAllNotes();


    //Room prüft ob zur Compilezeit ob die Spalten in der Tabelle zum Noteobjekt passen.
    //Wenn als Spalten abgefragt werden, die nicht in der entsprechenden Java-Klasse existieren, wird ein CompileTimeError ausgegeben
    //Durch die Angabe "LiveData", wird das Objekt "observed" beobachtet, wenn sich eine Änderung in der Tabelle ergeben hat, wird die Activity automatisch aktualisiert,
    //Room kümmert sich um die gesamte Arbeit, um dieses LiveData-Objekt upzudaten
    @Query("SELECT * FROM note_table ORDER BY priority DESC")
    LiveData<List<Note>> getAllNotes();
}
