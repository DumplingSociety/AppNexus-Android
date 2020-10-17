package com.example.multi_notepad;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.content.Intent;
import org.w3c.dom.Text;

public class AboutActivity extends AppCompatActivity {

    private TextView title;
    private TextView author;
    private TextView version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide(); // hide the status bar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        title = findViewById(R.id.about_title);
        author = findViewById(R.id.about_author);
        version = findViewById(R.id.about_version);


        Intent i = getIntent();
        if(i.hasExtra("title"))
        {
            String title1 = i.getStringExtra("title");
            String author1 = i.getStringExtra("author");
            String version1 = i.getStringExtra("version");
            title.setText(title1);
            author.setText(author1);
            version.setText(version1);
        }
    }
}