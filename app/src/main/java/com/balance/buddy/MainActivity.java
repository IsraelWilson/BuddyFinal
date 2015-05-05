package com.balance.buddy;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends Activity implements PictureTagDialogFragment.PictureTagDialogListener {
    LayoutInflater inflater;
    ScrollView scrollView;
    static LinearLayout ll;
    static Context ctx;
    View nRow;
    Button addSubject;
    public static DatabaseHandler db;

    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Buddy";

    private Uri fileUri; // file url to store image/video
    private Button btnCapturePicture;
    public static String strNewPictureLocation;

    List<Note> noteList;
    List<String> subjectList;

    static String[] FilePathStrings;
    GridView gridView;
    GridViewAdapter gridViewAdapter;

    static List<Button> btnList;
    DialogFragment tagDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ctx = getApplicationContext();
        btnList = new ArrayList<Button>();
        tagDialog = new PictureTagDialogFragment();

        // inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        ll = new LinearLayout(ctx);
        ll.setOrientation(LinearLayout.VERTICAL);

        addSubject = new Button(ctx);
        addSubject.setText("Add Subject");
        addSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dialog for new subject
                // Set text for image button
                // Set background for image button
                //  Set on click listener for button
                // Add button to linear layout
                Button newButton = new Button(ctx);
                newButton.setText("New Button");
                btnList.add(newButton);
                ll.addView(newButton);
            }
        });
        ll.addView(addSubject,0);
        scrollView.addView(ll);

        db = new DatabaseHandler(ctx);

        // Load all the Buttons for any classes that are in the db
        noteList = db.getAllNotes();
        subjectList = db.getDistinctSubjects();
        for(int i = 0; i < subjectList.size(); i++){
            Button btnSubject = new Button(ctx);
            btnSubject.setText(subjectList.get(i));
            btnSubject.setBackgroundResource(R.drawable.single_book_image);
            btnSubject.setTextColor(Color.parseColor("black"));
            btnList.add(btnSubject);
            btnSubject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open gallery with all the images for this subject
                    Button button = (Button)v;
                    List<Note> list = db.getSingleSubjectList(button.getText().toString());

                    // Set up the array to pass to the adapter for the gridview
                    FilePathStrings = new String[list.size()];
                    for(int i = 0; i < list.size(); i++){
                        FilePathStrings[i] = list.get(i).getLocation();
                    }
                    Intent intent = new Intent(MainActivity.this, NoteGallery.class);
                    intent.putExtra("filepath", FilePathStrings);
                    startActivity(intent);

                }
            });
            ll.addView(btnSubject);
        }

        btnCapturePicture = (Button)findViewById(R.id.btnCapturePicture);
        btnCapturePicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // capture picture
                captureImage();
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view
                showTagDialog();

            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        // save the picture location to add to database
        strNewPictureLocation = mediaFile.getAbsolutePath();
        // Log.d("Saved Location", strNewPictureLocation);

        return mediaFile;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, EditText tagSubject) {
        // User touched the dialog's positive button
        // TODO Make sure there is text in the tagSubject EditText
        db.addNote(new Note(tagSubject.getText().toString(),
                strNewPictureLocation));
        Log.d("Confirm Location", strNewPictureLocation);

        if (!subjectExist(tagSubject.getText().toString())) {
            Button newSub = new Button(ctx);
            newSub.setText(tagSubject.getText().toString());
            newSub.setBackgroundResource(R.drawable.single_book_image);
            newSub.setTextColor(Color.parseColor("black"));
            btnList.add(newSub);
            ll.addView(newSub);
            newSub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button button = (Button) v;
                    List<Note> list = db.getSingleSubjectList(button.getText().toString());

                    // Set up the array to pass to the adapter for the gridview
                    FilePathStrings = new String[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        FilePathStrings[i] = list.get(i).getLocation();
                    }
                    Intent intent = new Intent(ctx, NoteGallery.class);
                    intent.putExtra("filepath", FilePathStrings);
                    startActivity(intent);
                }
            });
        }

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        File file = new File(strNewPictureLocation);
        boolean deleted = file.delete();
        Log.d("Dialog Canceled", deleted ? "File Deleted" : "File Not Deleted");
        dialog.dismiss();

    }

    public void showTagDialog(){
        // TODO Implement support fragment manager
        tagDialog.show(getFragmentManager(), "Tag");
    }

    public boolean subjectExist(String subject){
        boolean found = false;

        if(btnList.isEmpty()){
            return false;
        }

        for(int i = 0; i < btnList.size(); i++){
            if(subject.toLowerCase().equals(btnList.get(i).getText().toString().toLowerCase())){
                found = true;
            }
        }
        return found;
    }
}
