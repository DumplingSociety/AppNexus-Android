package com.example.multi_notepad;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


import android.content.Intent;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener { // interface for adapter
    // The above lines are important - them make this class a listener
    // for click and long click events in the ViewHolders (in the recycler

    //set up request ID
    private int position = -1;

    private static final int ADD_NOTE = 1;
    private static final int EDIT_NOTE = 2;

    // private TextView textView;
    private RecyclerView recyclerView; // Layout's recyclerview
    private ArrayList<Note> noteList = new ArrayList<>();
    private NotesAdapter mAdapter;

    private static final String TAG = "MainActivity";

    private Note notepad;
    //private NotesAdapter noteAdapter; // Data to recyclerview adapter


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) { //initialize activity.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // layout resource

        recyclerView = findViewById(R.id.recyclerView); // retireve the recyclerview

        // make an adapter
        mAdapter = new NotesAdapter(noteList, this);
        //sets up adapter to recycler
        recyclerView.setAdapter(mAdapter);
        //how to layout the recycler
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        loadFile();

    }


    // inflates menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "lonActivityResult has been called ");
        if (requestCode == ADD_NOTE) {
            if (resultCode == RESULT_OK) {

                if (data != null) {
                    notepad = (Note) data.getSerializableExtra("NOTE_OBJ");
                    if (notepad.getTitle() != null) {
                        // textView2.setText(notepad.toString());
                        //noteList.set(this.position, notepad);
                        //saveNotes(noteList);
                        noteList.add(notepad);
                        //   saveNote();
                        // Toast.makeText(this, "Un-titled activity was not saved !", Toast.LENGTH_SHORT).show();
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Un-titled activity was not saved !", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }

        if (requestCode == EDIT_NOTE) {
            if (resultCode == RESULT_OK) {

         //   Intent i = getIntent();
     //       if (i.hasExtra("NOTE_OBJ"))
                if (data != null) {
                    notepad = (Note) data.getSerializableExtra("NOTE_OBJ");
                   // ((TextView) findViewById(R.id.tv_notes_list_text)).setText(notepad.getTitle());
                    //TextView tvTitle = findViewById(R.id.title_tv);
                    //TextView tvTime = findViewById(R.id.time);
                    noteList.set(this.position, notepad);
                    mAdapter.notifyDataSetChanged();
                    //     }
                }
            }
        }



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.edit_menu:
                Intent intent = new Intent(this, EditActivity.class); // Intent is used for cause a new action to occur, here is to display a activity
                notepad = new Note();
                intent.putExtra("Note", notepad);
                startActivityForResult(intent, ADD_NOTE); // when need to get return data
                // startActivity(intent);
                break;
            case R.id.about_menu:
                Intent aboutDataReturn = new Intent(this, AboutActivity.class);
                aboutDataReturn.putExtra("title", "Multi Note");
                aboutDataReturn.putExtra("author", "2020, Xiangwei Li");
                aboutDataReturn.putExtra("version", "Version 1.0");
                startActivity(aboutDataReturn);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        position = pos;
        Note n = noteList.get(pos);
        Intent dataToSend = new Intent(this, EditActivity.class);
     //   Toast.makeText(v.getContext(), "SHORT " + n.toString(), Toast.LENGTH_SHORT).show();

        //notepad = new Note();
        dataToSend.putExtra("Note", n);
        startActivityForResult(dataToSend, EDIT_NOTE);

    }


    @Override
    public boolean onLongClick(final View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int pos = recyclerView.getChildLayoutPosition(v);
                Note m = noteList.get(pos);

                if (m != null) {
                    noteList.remove(pos);
                    mAdapter.notifyDataSetChanged();

                }

            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //        finish();
            }
        });
        builder.setTitle("Delete Note 'Set DVR'?");

        AlertDialog dialog = builder.create();
        dialog.show();
        return false; // true means the click event is consumed
    }

    private void saveNote() {

       // Log.d(TAG, "saveProduct: Saving JSON File");
        try {
            FileOutputStream fos = getApplicationContext().
                    openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);

            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, getString(R.string.encoding)));
            writer.setIndent("  ");
            writer.beginArray();
            for (int i = 0; i < noteList.size(); i++) {
                writer.beginObject();
                writer.name("title").value(noteList.get(i).getTitle());
                writer.name("noteText").value(noteList.get(i).getDescription());
                writer.name("date").value(noteList.get(i).getDate());
                writer.endObject();
            }
            writer.endArray();
            writer.close();


          //  Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    @Override
    protected void onPause() {
        saveNote(); // saving data to JSON file

        super.onPause();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void loadFile() {

        Log.d(TAG, "loadFile: Loading JSON File");
       // notepad = new Note();
        try {
            InputStream is = getApplicationContext().
                    openFileInput(getString(R.string.file_name));

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

      //      JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray jsonArray = new JSONArray(sb.toString());
            for(int i =0; i< jsonArray.length(); i++){ JSONObject jsonObject = jsonArray.getJSONObject(i);
            String name = jsonObject.getString("title");
            String desc = jsonObject.getString("noteText");
            String ldate = jsonObject.getString("date");
//            notepad.setTitle(name);
 //           notepad.setDescription(desc);
   //         notepad.setDate(ldate);
                Note nodeload = new Note(name, desc,ldate);
            noteList.add(nodeload);
            }

            mAdapter.notifyDataSetChanged();
        } catch (FileNotFoundException e) {
            Toast.makeText(this, getString(R.string.no_file), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



