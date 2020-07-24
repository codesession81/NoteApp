package com.example.noteapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {

    //Da alle Items in einer Liste gespeichert werden, eine ArrayListe deklarieren und initialisieren
    private List<Note> noteList = new ArrayList<>();
    private OnItemClickListener listener;


    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {//parent ist der RecyclerView

        //Eine Referenz zwischen dem RecyclerView und dem Layout eines ListItems erzeugen
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item,parent,false);

        return new NoteHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        //Hier werden die Informationen in die einzelen Views eines ListItems geladen
        Note currentNote = noteList.get(position);
        holder.tvTitle.setText(currentNote.getTitle());
        holder.tvDescription.setText(currentNote.getDescription());
        holder.tvPriority.setText(String.valueOf(currentNote.getPriority())); //Priority ist ein Integer und kann nicht als Text dargestellt werden, mit String.valueOf in einen String umwandeln

    }

    @Override
    public int getItemCount() {
        //Es sollen immer so viele Items angezeigt werden, wie aktuell in der Liste gespeichert sind
        return noteList.size();
    }


    //Die Activity achtet auf Anderungen und zeigt diese in der onChanged Methode an, diese Methode sorgt
    //dafür das die LifeData in den RecyclerView geladen werden
    public void setNotes(List<Note> notes){
        this.noteList = notes;
        notifyDataSetChanged();
    }


    //Ermittle das NoteObjekt an der Position, wo es gelöscht werden soll
    public Note getNoteAt(int position){
        return noteList.get(position);
    }

    class NoteHolder extends RecyclerView.ViewHolder {

        /*
        Für jeden View in einer ItemRow wird eine Variable angelegt
         */
        private TextView tvTitle;
        private TextView tvDescription;
        private TextView tvPriority;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);

            //In NoteHolder wird eine Referenz zwischen einem RecyclerViewItem und dem NoteAdapter erzeugt
            tvTitle = itemView.findViewById(R.id.tv_titel);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvPriority = itemView.findViewById(R.id.tv_priority);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener !=null && position != RecyclerView.NO_POSITION){
                        listener.onItemClick(noteList.get(position));
                    }

                }
            });
        }
    }


    public interface OnItemClickListener {
        void onItemClick(Note note);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }


}
