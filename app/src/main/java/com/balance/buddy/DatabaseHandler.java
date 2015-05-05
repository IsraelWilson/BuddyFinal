package com.balance.buddy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by israe_000 on 4/22/2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "my_notes";
    private static final String TABLE_NOTE = "note";

    private static final String KEY_SUBJECT = "subject";
    private static final String KEY_LOCATION = "location";

    DatabaseHandler(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NOTE_TABLE = "CREATE TABLE " + TABLE_NOTE + "("
                + KEY_SUBJECT + " TEXT," + KEY_LOCATION + " TEXT"
                + ")";
        db.execSQL(CREATE_NOTE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE);
        onCreate(db);
    }

    public void addNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SUBJECT, note.getSubject());
        values.put(KEY_LOCATION, note.getLocation());

        // Inserting Row
        db.insert(TABLE_NOTE, null, values);
        db.close(); // Closing database connection
    }

    public List<Note> getAllNotes() {
        List<Note> noteList = new ArrayList<Note>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NOTE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setSubject(cursor.getString(0));
                note.setLocation(cursor.getString(1));

                noteList.add(note);
            } while (cursor.moveToNext());
        }

        // return contact list
        return noteList;
    }

    public List<String> getDistinctSubjects(){
        List<String> distinctList = new ArrayList<String>();

        // Select Distinct Query
        String distinctQuery = "SELECT DISTINCT " + KEY_SUBJECT + " FROM " + TABLE_NOTE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(distinctQuery, null);

        // looping through all rows and adding to the list
        if(cursor.moveToFirst()){
            do{
                distinctList.add(cursor.getString(0));
            }while (cursor.moveToNext());
        }

        // return distinct list
        return distinctList;

    }

    public List<Note> getSingleSubjectList(String subject){
        List<Note> subjectList = new ArrayList<Note>();

        // Select all notes with given subject name
        String selectQuery = "SELECT * FROM " + TABLE_NOTE + " WHERE " + KEY_SUBJECT + " = '"
                + subject + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to the list
        if(cursor.moveToFirst()){
            do{
                Note note = new Note();
                note.setSubject(cursor.getString(0));
                note.setLocation(cursor.getString(1));

                 String log = "Query Result: " + Integer.toString(cursor.getColumnCount());
                 Log.d("NOTE ", log);

                subjectList.add(note);
            }while (cursor.moveToNext());
        }

        // return distinct list
        return subjectList;
    }

    public int getNoteCount(){
        String countQuery = "SELECT  * FROM " + TABLE_NOTE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    public void deleteNote(Note note){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTE, KEY_LOCATION + " = ?",
                new String[] { note.getLocation()});
        db.close();
    }

    public int updateNote(Note note){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SUBJECT, note.getSubject());
        values.put(KEY_LOCATION, note.getLocation());

        // updating row
        return db.update(TABLE_NOTE, values, KEY_LOCATION + " = ?",
                new String[]{note.getLocation()});
    }

}
