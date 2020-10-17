package com.example.multi_notepad;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;
// create the layout for notes_list_row.xml
public class NotesHolder extends RecyclerView.ViewHolder {

    TextView title;
    TextView noteText;
    TextView recordTime;

    NotesHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title_tv);
        noteText = itemView.findViewById(R.id.tv_notes_list_text);
        recordTime = itemView.findViewById(R.id.time);
    }


}




