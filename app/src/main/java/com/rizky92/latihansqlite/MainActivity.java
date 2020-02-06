package com.rizky92.latihansqlite;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.rizky92.latihansqlite.adapter.Adapter;
import com.rizky92.latihansqlite.database.DatabaseContract;
import com.rizky92.latihansqlite.entities.Notes;
import com.rizky92.latihansqlite.helper.MappingHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

interface LoadNotesCallback {
    void preExecute();
    void postExecute(ArrayList<Notes> notes);
}

public class MainActivity extends AppCompatActivity implements LoadNotesCallback {

    private static final String EXTRA_STATE = "extra_state";

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private Adapter adapter;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Notes");
        }

        progressBar = findViewById(R.id.progress_circular);
        recyclerView = findViewById(R.id.rv_notes);
        fabAdd = findViewById(R.id.fab_add);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new Adapter(this);
        recyclerView.setAdapter(adapter);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NoteAddUpdateActivity.class);
                startActivityForResult(intent, NoteAddUpdateActivity.REQUEST_ADD);
            }
        });

        HandlerThread thread = new HandlerThread("DataObserver");
        thread.start();
        Handler handler = new Handler(thread.getLooper());

        DataObserver observer = new DataObserver(handler, this);
        getContentResolver().registerContentObserver(DatabaseContract.NoteColumns.CONTENT_URI, true, observer);

        if (savedInstanceState == null) {
            new LoadNotesAsync(this, this).execute();
        } else {
            ArrayList<Notes> list = savedInstanceState.getParcelableArrayList(EXTRA_STATE);
            if (list != null) {
                adapter.setList(list);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_STATE, adapter.getList());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (requestCode == NoteAddUpdateActivity.REQUEST_ADD) {
                if (resultCode == NoteAddUpdateActivity.RESULT_ADD) {
                    Notes notes = data.getParcelableExtra(NoteAddUpdateActivity.EXTRA_NOTE);

                    adapter.addItem(notes);
                    recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);

                    showSnackBarMessage("Satu item berhasil ditambahkan!");
                }
            } else if (requestCode == NoteAddUpdateActivity.REQUEST_UPDATE) {
                if (resultCode == NoteAddUpdateActivity.RESULT_UPDATE) {
                    Notes notes = data.getParcelableExtra(NoteAddUpdateActivity.EXTRA_NOTE);
                    int pos = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0);

                    adapter.updateItem(pos, notes);
                    recyclerView.smoothScrollToPosition(pos);

                    showSnackBarMessage("Satu item berhasil diubah!");
                } else if (resultCode == NoteAddUpdateActivity.RESULT_DELETE) {
                    int pos = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0);

                    adapter.removeItem(pos);

                    showSnackBarMessage("Satu item berhasil dihapus!");
                }
            }
        }
    }

    private void showSnackBarMessage(String s) {
        Snackbar.make(recyclerView, s, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void preExecute() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void postExecute(ArrayList<Notes> notes) {
        progressBar.setVisibility(View.INVISIBLE);
        if (notes.size() > 0) {
            adapter.setList(notes);
        } else {
            adapter.setList(new ArrayList<Notes>());
            showSnackBarMessage("Tidak ada data saat ini...");
        }
    }

    private static class LoadNotesAsync extends AsyncTask<Void, Void, ArrayList<Notes>> {

        private final WeakReference<Context> weakContext;
        private final WeakReference<LoadNotesCallback> weakCallback;

        LoadNotesAsync(Context context, LoadNotesCallback notesCallback) {
            weakContext = new WeakReference<>(context);
            weakCallback = new WeakReference<>(notesCallback);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            weakCallback.get().preExecute();
        }

        @Override
        protected ArrayList<Notes> doInBackground(Void... voids) {
            Context context = weakContext.get();
            Cursor dateCur = context.getContentResolver().query(DatabaseContract.NoteColumns.CONTENT_URI, null, null, null, null);
            return MappingHelper.mapCursorToArrayList(dateCur);
        }

        @Override
        protected void onPostExecute(ArrayList<Notes> notes) {
            super.onPostExecute(notes);
            weakCallback.get().postExecute(notes);
        }
    }

    public static class DataObserver extends ContentObserver {

        final Context context;

        DataObserver(Handler handler, Context context) {
            super(handler);
            this.context = context;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            new LoadNotesAsync(context, (LoadNotesCallback) context).execute();
        }
    }
}
