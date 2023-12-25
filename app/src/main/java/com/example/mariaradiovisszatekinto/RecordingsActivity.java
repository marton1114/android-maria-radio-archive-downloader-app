package com.example.mariaradiovisszatekinto;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class RecordingsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView notFoundTextView;
    ArrayList<AudioModel> recordingList = new ArrayList<>();

    ImageView addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordings);
        Objects.requireNonNull(getSupportActionBar()).hide();


        Objects.requireNonNull(getSupportActionBar()).setTitle( (CharSequence)"Felvételek");

        recyclerView = findViewById(R.id.recyclerView);
        notFoundTextView = findViewById(R.id.notFoundTextView);
        addButton = findViewById(R.id.addButton);

        addButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        if (! checkPermission()) {
            requestPermission();
            return;
        }

        fillList();
    }

    public void fillList() {
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        //MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);


        while (cursor.moveToNext()) {

            if (checkFileDirection(cursor.getString(1))) {
                AudioModel recordingData = new AudioModel(cursor.getString(1), cursor.getString(0), cursor.getString(2));

                if (new File(recordingData.getPath()).exists()) {
                    recordingList.add(recordingData);
                }

                if (recordingList.size() == 0) {
                    notFoundTextView.setVisibility(View.VISIBLE);
                } else {
                    // recycler
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    recyclerView.setAdapter(new RecordingListAdapter(recordingList, getApplicationContext()));

                    notFoundTextView.setVisibility(View.GONE);
                }
            }
        }
        cursor.close();

    }

    private boolean checkFileDirection(String path) {
        return new File(path).getParent().equals(Environment.getExternalStorageDirectory().getPath() + "/Music/MariaRadio");
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(RecordingsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(RecordingsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(RecordingsActivity.this, "Olvasási engedély szükséges, kérlek engedélyezd a beállításokban!", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(RecordingsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
            fillList();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (recyclerView != null) {
            recyclerView.setAdapter(new RecordingListAdapter(recordingList, getApplicationContext()));
        }
    }
}