package com.example.noteapp;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

/*
Die DB definieren, welche Tabellen sollen in die DB gespeichert werden?
Tabellen durch "entities" getrennt durch Komatas aufzählen.
Anschließend wird die DB Version angegeben
 */

@Database(entities ={Note.class}, version = 1)
public abstract class NoteDatabase extends RoomDatabase {

    /*
    Diese Klasse muss in ein Singleton umgewandelt werden, um sicherzustellen,
    dass nur eine Instance der DB erzeugt werden kann, die überall in der Anwendung über die
     Variable "instance" zugänglich ist.
     */

    private static NoteDatabase instance;

    //Mit dieser Methode kann später auf die DB-Operationen zugegriffen werden
    public abstract NoteDao noteDao();

    //Mit synchronized kann nur ein Thread auf diese Methode zugreifen
    public static synchronized NoteDatabase getInstance(Context context){
        //Hier wird die einzige Singleton DB Instance erzeugt, die Methode kann von Aussen aufgerufen werden

        //Prüfen, ob schon die DB noch nicht instanziiert wurde, es darf nur eine Instance existieren
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    NoteDatabase.class,"note_database")//1. Namen der DB-Klasse,2. der Name des Files
                    //Wenn die DB upgedatet wird, muss Room gesagt werden, wie es zu dem neuen Schema gelangt. Wird diese angabe weggelassen und die DB upgedatet, stürzt die App
                    //mit einer IllegalStateException ab.Durch "fallbackToDestructiveMigration" wird die DB gelöscht und sauber neu erzeugt
                    .fallbackToDestructiveMigration()
                    //Callback aufrufen, um erste Informationen in die DB zu schreiben
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }


    /*
    DB beim erzeugen mit Informationen füllen
     */
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new InsertDataAsynctask(instance).execute();
        }
    };


    //Für das erstmalige Befüllen der DB, eine weitere Asynctask anlegen
    private static class InsertDataAsynctask extends AsyncTask<Void,Void,Void> {

        private NoteDao noteDao;

        private InsertDataAsynctask(NoteDatabase db){
            noteDao = db.noteDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.insert(new Note("Titel 1","Description 1",1));
            noteDao.insert(new Note("Titel 2","Description 2",2));
            noteDao.insert(new Note("Titel 3","Description 3",3));
            return null;
        }
    }

}
