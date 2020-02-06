package com.rizky92.latihansqlite;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.rizky92.latihansqlite.entities.Notes;
import com.rizky92.latihansqlite.helper.MappingHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.rizky92.latihansqlite.database.DatabaseContract.NoteColumns.CONTENT_URI;
import static com.rizky92.latihansqlite.database.DatabaseContract.NoteColumns.DATE;
import static com.rizky92.latihansqlite.database.DatabaseContract.NoteColumns.DESCRIPTION;
import static com.rizky92.latihansqlite.database.DatabaseContract.NoteColumns.TITLE;

public class NoteAddUpdateActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_NOTE = "extra_note";
    public static final String EXTRA_POSITION = "extra_position";

    public static final int REQUEST_ADD = 100;
    public static final int REQUEST_UPDATE = 200;
    public static final int RESULT_ADD = 101;
    public static final int RESULT_UPDATE = 201;
    public static final int RESULT_DELETE = 301;

    private final int ALERT_DIALOG_CLOSE = 10;
    private final int ALERT_DIALOG_DELETE = 20;

    private EditText edtTitle, edtDesc;
    private Button btnSubmit;
    private boolean isEdit = false;
    private Notes notes;
    private int pos;
    private Uri uriWithId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_add_update);

        edtTitle = findViewById(R.id.edt_title);
        edtDesc = findViewById(R.id.edt_desc);
        btnSubmit = findViewById(R.id.btn_submit);

        notes = getIntent().getParcelableExtra(EXTRA_NOTE);
        if (notes != null) {
            pos = getIntent().getIntExtra(EXTRA_POSITION, 0);
            isEdit = true;
        } else {
            notes = new Notes();
        }

        String abTitle, btnTitle;

        if (isEdit) {
            uriWithId = Uri.parse(CONTENT_URI + "/" + notes.getId());
            if (uriWithId != null) {
                Cursor cur = getContentResolver().query(uriWithId, null, null, null, null);

                if (cur != null) {
                    notes = MappingHelper.mapCursorToObject(cur);
                    cur.close();
                }
            }

            abTitle = "Ubah";
            btnTitle = "Update";

            if (notes != null) {
                edtTitle.setText(notes.getTitle());
                edtDesc.setText(notes.getDescription());
            }
        } else {
            abTitle = "Tambah";
            btnTitle = "Simpan";
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(abTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnSubmit.setText(btnTitle);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_submit) {
            String title = edtTitle.getText().toString().trim();
            String desc = edtDesc.getText().toString().trim();

            if (TextUtils.isEmpty(title)) {
                edtTitle.setError("Jangan kosong");
                return;
            }

            notes.setTitle(title);
            notes.setDescription(desc);

            ContentValues values = new ContentValues();
            values.put(TITLE, title);
            values.put(DESCRIPTION, desc);

            if (isEdit) {
                getContentResolver().update(uriWithId, values, null, null);
                Toast.makeText(NoteAddUpdateActivity.this, "Satu item berhasil diedit", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                notes.setDate(getCurrentDate());
                values.put(DATE, getCurrentDate());

                getContentResolver().insert(CONTENT_URI, values);
                Toast.makeText(NoteAddUpdateActivity.this, "Satu item berhasil disimpan", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isEdit) {
            getMenuInflater().inflate(R.menu.menu_form, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                showAlertDialog(ALERT_DIALOG_DELETE);
                break;

            case R.id.home:
                showAlertDialog(ALERT_DIALOG_CLOSE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE);
    }

    private String getCurrentDate() {
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();

        return format.format(date);
    }

    private void showAlertDialog(int type) {
        final boolean isDialogClose = type == ALERT_DIALOG_CLOSE;
        String dialogTitle, dialogMessage;

        if (isDialogClose) {
            dialogTitle = "Batal";
            dialogMessage = "Ingin membatalkan perubahan pada form?";
        } else {
            dialogTitle = "Hapus Note";
            dialogMessage = "Ingin menghapus note?";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(dialogTitle);
        builder
                .setMessage(dialogMessage)
                .setCancelable(true)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (isDialogClose) {
                            finish();
                        } else {
                            getContentResolver().delete(uriWithId, null, null);
                            Toast.makeText(NoteAddUpdateActivity.this, "Satu item berhasil dihapus", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
