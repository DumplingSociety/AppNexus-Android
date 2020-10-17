package com.example.multi_notepad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EditActivity extends AppCompatActivity {
    private Note notepad;
    private EditText edittitle;
    private EditText noteText;
    private EditText lastDate;
    private static final int ADD_NOTE = 1;
    private static final int EDIT_NOTE = 2;
    private String dateFormat;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        edittitle = findViewById(R.id.inputTitle);
        noteText = findViewById(R.id.inputNote);
      //  lastDate = findViewById(R.id.time);

        Intent intent = getIntent();
        //if (intent.hasExtra(Intent.EXTRA_TEXT)) {
        //  String text = intent.getStringExtra(Intent.EXTRA_TEXT);
        notepad = (Note) intent.getSerializableExtra("Note");
  //      if (notepad!= null) {
        if (intent.hasExtra("Note")){

            edittitle.setText(notepad.getTitle());
            noteText.setText(notepad.getDescription());
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save_menu:

                Intent dataToReturn = new Intent();
                dateFormat =new SimpleDateFormat("EEE MMM  d, HH:mm a").format(Calendar.getInstance().getTime());
                notepad.setDate(dateFormat);
                notepad.setTitle(edittitle.getText().toString());
                notepad.setDescription(noteText.getText().toString());
            //    if (notepad.getTitle() == (edittitle.getText().toString()) && (notepad.getDescription() == noteText.getText().toString())) {
               //     finish();
              //  }
                if (edittitle.length() ==0)
                {
                    Toast.makeText(this, "un-titled activity was not saved!!!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                dataToReturn.putExtra("NOTE_OBJ", notepad); // this is basic works like a hashmap
                setResult(RESULT_OK, dataToReturn);  //setResult automatically calls startActivityForResult
                finish();

            default:
                return super.onOptionsItemSelected(item);
        }
       // return true;
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
         //       if (notepad != null) {
                    Intent dataToReturn = new Intent();
                    dateFormat =new SimpleDateFormat("EEE MMM  d, HH:mm a").format(Calendar.getInstance().getTime());
                    notepad.setTitle(edittitle.getText().toString());
                    notepad.setDescription(noteText.getText().toString());
                    dataToReturn.putExtra("NOTE_OBJ", notepad); // this is basic works like a hashmap
                    setResult(RESULT_OK, dataToReturn);  //setResult automatically calls startActivityForResult
                finish();
                    //  startActivityForResult(dataToReturn, ADD_NOTE); // this does calling
   //             }
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setMessage("Save Your Note  'Set DVR'?");
        builder.setTitle("Your Note is Not Saved !");

        AlertDialog dialog = builder.create();
        dialog.show();

    }

//super.onBackPressed();
}