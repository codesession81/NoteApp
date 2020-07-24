package com.example.noteapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NoteViewModel noteViewModel;
    public static final int ADD_NOTE_REQUEST =1;
    public static final int EDIT_NOTE_REQUEST =2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FloatingActionButton buttonAddNote = findViewById(R.id.btn_add_note);
        buttonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                startActivityForResult(intent,ADD_NOTE_REQUEST);
            }
        });



        //Referenz zum RecyclerView erstellen
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        //Jeder RecyclerView braucht einen Layoutmanager, der sich um die Ausrichtung der ListItems kümmert
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);//Wenn die Größe des RecyclerViews immer gleich ist


        //Ein NoteAdapterObjekt erzeugen
        final NoteAdapter adapter = new NoteAdapter();

        //Dem RecyclerView den Adapter übergeben
        recyclerView.setAdapter(adapter);//Die Liste ist per Default leer


        /*
        Auf NoteViewModel wird nicht new angewendet, weil sonst mit jeder neuen Activity eine eneu ViewModelInstanz erzeugt werden würde.
        Statt dessen wird das AndroidSystem abgefragt, weil dies Bescheid weiß, wenn ein ViewModelexistiert, wie darauf zugegriffen werden kann.


        ViewModelProviders.of wird entweder eine Activity (mit this) oder ein Fragment übergeben.
        So weis das ViewModel auf welchen LifeCycle es zu achten hat, Activities und Fragmente haben unterschiedliche LifeCycles.
        get() wird die Klasse des zuständigen ViewModels übergeben, so ist eine Verbindung zwischen der Activity (View) und dem ViewModel
        aufgebaut.
         */

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);



        /*
        Jetzt kann auf das ViewModel zugegriffen werden, dass LifeData zurückgibt, this= LifeCycle-Owner, in diesen Fall, die Activity.
        LifeData werden nur aufgerufen und upgedatet, wenn die Activity im Vordergrund ist, wenn die Activity zerstört ist, wird die Referenz
        bereinigt, um MemorieLeaks und Crashes zu verhindern
         */
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            //onChanged wird nur aufgerufen, wenn Activity im Vordergrund ist, wenn durch eine Konfigurationsänderung die Activity zerstört wurde
            //wird diese Methode keine Referenz zur Activity besitzen.
            public void onChanged(@Nullable List<Note> notes) {
               //Über die setNotes Methode aus dem Adapter LifeData erhalten
                adapter.setNotes(notes);
            }
        });


        /*
        Zum Löschen von Notes Swipe-Gestige implementieren
         */

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
               noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                    Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                    intent.putExtra(AddEditNoteActivity.EXTRA_ID,note.getId());
                    intent.putExtra(AddEditNoteActivity.EXTRA_TITLE,note.getTitle());
                    intent.putExtra(AddEditNoteActivity.EXTRA_DESCRIPTION,note.getDescription());
                    intent.putExtra(AddEditNoteActivity.EXTRA_PRIORITY,note.getPriority());
                    startActivityForResult(intent,EDIT_NOTE_REQUEST);
            }
        });
    }


    //Die Benutzereingabe aus der AddEditNoteActivity empfangen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Wenn eine Eingabe erfolgte
        if(requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK){
            //Die Extra-Daten empfangen
            String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY,1);

            //ExtraDaten speichern
            Note note = new Note(title,description,priority);
            noteViewModel.insert(note);

            Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
        }else if(requestCode == EDIT_NOTE_REQUEST && resultCode == RESULT_OK){
            int id = data.getIntExtra(AddEditNoteActivity.EXTRA_ID,-1);
            if(id==-1){
                Toast.makeText(this, "Note can`t be updated", Toast.LENGTH_SHORT).show();
                return;
            }
            String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY,1);

            Note note = new Note(title,description,priority);
            note.setId(id);
            noteViewModel.update(note);
            Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
        }
        else{
            //Sollte AddEditNoteActivity ohne Eingabe beendet werden
            Toast.makeText(this, "Note not saved", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.delete_all_notes:
                noteViewModel.deleteAllNotes();
                Toast.makeText(this, "All notes deleted", Toast.LENGTH_SHORT).show();
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }
    }
}
