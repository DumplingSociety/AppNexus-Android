package com.example.multi_notepad;



import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;


public class NotesAdapter extends RecyclerView.Adapter<NotesHolder> {


    private static final String TAG = "NoteAdapter";

    private List<Note> noteList;
    private MainActivity mainAct;

    NotesAdapter(List<Note> noteList, MainActivity ma) { // pass the mainactivity and sets the two varibles for me
        this.noteList = noteList;
        this.mainAct = ma;
    }

    @NonNull
    @Override
    public NotesHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) { //create view holders
        Log.d(TAG, "onCreateViewHolder: MAKING NEW NotesHolder");

        View itemView = LayoutInflater.from(parent.getContext())                 // inflates the layout and create view holder and populate data and passing into the recyclerview
                .inflate(R.layout.notes_list_row, parent, false);

        itemView.setOnClickListener(mainAct);       // (onClinckListener in the MainActivity) who response for the onClinckListener
        itemView.setOnLongClickListener(mainAct);    // (onLongClickListerner in the MainActivity)
        return new NotesHolder(itemView); // call the NotesHolder(which I created ) and passing the itemview layout


    }

        // onBind is setting the data
        @Override
        public void onBindViewHolder (@NonNull NotesHolder holder,int position){ // position is the index of the list
            Log.d(TAG, "onBindViewHolder: FILLING VIEW HOLDER  " + position);

            Note n = noteList.get(position);

            holder.title.setText(n.getTitle()); //set holder title to the notes title
            holder.noteText.setText(n.getDescription());
            holder.recordTime.setText(n.getDate());
        }

        @Override
        public int getItemCount () { // counting how many items in the list
            return noteList.size();
        }


    }






