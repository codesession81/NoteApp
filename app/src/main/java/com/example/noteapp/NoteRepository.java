package com.example.noteapp;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class NoteRepository {

    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;

    public NoteRepository(Application application){
        NoteDatabase database = NoteDatabase.getInstance(application);

        /*
        noteDao() ist eine abstrakte Methode und kann normalerweise nicht aufgerufen werden,
        das wird durch den databaseBuilder() möglich gemacht
         */
        noteDao = database.noteDao();

        //Auf die selbstdefinierte Methode aus dem Interface zugreifen
        allNotes = noteDao.getAllNotes();
    }

    /*
    Methoden für alle notwendigen DB-Operations definieren.
    Room erlaubt es nicht, DB-Operationen über den Mainthread laufen zu lassen, da sonst die App einfrieren könnte.
    Um dies zu lösen, werden Asynctask genutzt

    Diese Methoden werden vom ViewModel aufgerufen, wobei sich das ViewModel nicht um die Logik der Operationen kümmern muss
     */

    public void insert(Note note){
        //Eine Instanz der Aysnctask InsertNote erzeugen
        new InsertNoteAsyncTask(noteDao).execute(note);
    }

    public void update(Note note){
        new UpdateNoteAsyncTask(noteDao).execute(note);
    }

    public void delete(Note note){
        new DeleteNoteAsyncTask(noteDao).execute(note);
    }

    public void deleteAllNotes(){
        new DeleteAllNotesAsyncTask(noteDao).execute();
    }


    public LiveData<List<Note>> getAllNotes(){
        return allNotes;
    }



    /*
    Für jede DB-Operation eine Asynctask als innere Klasse definieren, die Klasse muss statisch sein, um keine Referenz zu dem Repository zu haben, dies würde ein Memoryleak
    verursachen
     */

    private static class InsertNoteAsyncTask extends AsyncTask<Note,Void,Void> {//1. Übergabetyp, 2.Progressupdates, 3.Rückgabetyp

        private NoteDao noteDao;

        private InsertNoteAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.insert(notes[0]);//Da dieser Methode Varargs übergeben wird, muss über den Index auf das Parameter zugegriffen werden
            return null;
        }
    }



    private static class UpdateNoteAsyncTask extends AsyncTask<Note,Void,Void> {//1. Übergabetyp, 2.Progressupdates, 3.Rückgabetyp

        private NoteDao noteDao;

        private UpdateNoteAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.update(notes[0]);//Da dieser Methode Varargs übergeben wird, muss über den Index auf das Parameter zugegriffen werden
            return null;
        }
    }




    private static class DeleteNoteAsyncTask extends AsyncTask<Note,Void,Void> {//1. Übergabetyp, 2.Progressupdates, 3.Rückgabetyp

        private NoteDao noteDao;

        private DeleteNoteAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.delete(notes[0]);//Da dieser Methode Varargs übergeben wird, muss über den Index auf das Parameter zugegriffen werden
            return null;
        }
    }


    private static class DeleteAllNotesAsyncTask extends AsyncTask<Void,Void,Void> {//1. Übergabetyp, 2.Progressupdates, 3.Rückgabetyp

        private NoteDao noteDao;

        private DeleteAllNotesAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.deleteAllNotes();//Da dieser Methode Varargs übergeben wird, muss über den Index auf das Parameter zugegriffen werden
            return null;
        }
    }



}
