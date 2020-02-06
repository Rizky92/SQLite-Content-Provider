package com.rizky92.latihansqlite.helper;

import android.database.Cursor;

import com.rizky92.latihansqlite.database.DatabaseContract;
import com.rizky92.latihansqlite.entities.Notes;

import java.util.ArrayList;

public class MappingHelper {

    public static ArrayList<Notes> mapCursorToArrayList(Cursor cursor) {
        ArrayList<Notes> list = new ArrayList<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.NoteColumns._ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.NoteColumns.TITLE));
            String desc = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.NoteColumns.DESCRIPTION));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.NoteColumns.DATE));

            list.add(new Notes(id, title, desc, date));
        }

        return list;
    }

    public static Notes mapCursorToObject(Cursor notesCur) {
        notesCur.moveToFirst();
        int id = notesCur.getInt(notesCur.getColumnIndexOrThrow(DatabaseContract.NoteColumns._ID));
        String title = notesCur.getString(notesCur.getColumnIndexOrThrow(DatabaseContract.NoteColumns.TITLE));
        String desc = notesCur.getString(notesCur.getColumnIndexOrThrow(DatabaseContract.NoteColumns.DESCRIPTION));
        String date = notesCur.getString(notesCur.getColumnIndexOrThrow(DatabaseContract.NoteColumns.DATE));

        return new Notes(id, title, desc, date);
    }
}
