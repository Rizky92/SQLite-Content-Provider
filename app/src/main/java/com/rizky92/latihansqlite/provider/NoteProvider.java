package com.rizky92.latihansqlite.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.rizky92.latihansqlite.database.NoteHelper;

import static com.rizky92.latihansqlite.database.DatabaseContract.AUTHORITY;
import static com.rizky92.latihansqlite.database.DatabaseContract.NoteColumns.CONTENT_URI;
import static com.rizky92.latihansqlite.database.DatabaseContract.TABLE_NAME;

public class NoteProvider extends ContentProvider {

    private static final int NOTE = 1;
    private static final int NOTE_ID = 2;
    private NoteHelper helper;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, TABLE_NAME, NOTE);
        uriMatcher.addURI(AUTHORITY, TABLE_NAME + "/#", NOTE_ID);
    }

    @Override
    public boolean onCreate() {
        helper = NoteHelper.getInstance(getContext());
        helper.open();
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cur;
        switch (uriMatcher.match(uri)) {
            case NOTE:
                cur = helper.queryAll();
                break;

            case NOTE_ID:
                cur = helper.queryById(uri.getLastPathSegment());
                break;

            default:
                cur = null;
                break;
        }
        return cur;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long added;
        switch (uriMatcher.match(uri)) {
            case NOTE:
                added = helper.insert(values);
                break;

            default:
                added = 0;
                break;
        }

        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return Uri.parse(CONTENT_URI + "/" + added);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int updated;
        switch (uriMatcher.match(uri)) {
            case NOTE:
                updated = helper.update(uri.getLastPathSegment(), values);
                break;

            default:
                updated = 0;
                break;
        }

        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return updated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int deleted;
        switch (uriMatcher.match(uri)) {
            case NOTE:
                deleted = helper.deleteById(uri.getLastPathSegment());
                break;

            default:
                deleted = 0;
                break;
        }

        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return deleted;
    }
}
