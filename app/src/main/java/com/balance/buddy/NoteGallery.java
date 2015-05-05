package com.balance.buddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by israe_000 on 4/25/2015.
 */
public class NoteGallery extends Activity {
    ImageView imageview;

    GridView grid;
    GridViewAdapter adapter;

    Intent intent;
    String[] filePaths;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from view_image.xml
        setContentView(R.layout.activity_gallery_note);

        intent = getIntent();
        filePaths = intent.getStringArrayExtra("filepath");

        // Locate the GridView in gridview_main.xml
        grid = (GridView) findViewById(R.id.gridview);
        // Pass String arrays to LazyAdapter Class
        adapter = new GridViewAdapter(this, filePaths);
        // Set the LazyAdapter to the GridView
        grid.setAdapter(adapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle Grid View Item Clicks Here
                Intent i = new Intent(NoteGallery.this, ViewImage.class);
                // Pass String arrays FilePathStrings
                i.putExtra("filepath", filePaths);
                // Pass click position
                i.putExtra("position", position);
                startActivity(i);
            }
        });

    }
}
