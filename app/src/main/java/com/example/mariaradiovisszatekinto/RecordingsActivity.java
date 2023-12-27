package com.example.mariaradiovisszatekinto;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.opengl.Visibility;
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
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class RecordingsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView notFoundTextView;
    ArrayList<AudioModel> recordingList = new ArrayList<>();

    ImageView addButton, allowDeleteButton;

    int isDeleteActivated = INVISIBLE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordings);
        Objects.requireNonNull(getSupportActionBar()).hide();

        recyclerView = findViewById(R.id.recyclerView);
        notFoundTextView = findViewById(R.id.notFoundTextView);
        addButton = findViewById(R.id.addButton);
        allowDeleteButton = findViewById(R.id.allowDeleteButton);

        addButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        allowDeleteButton.setOnClickListener(view -> {
            if (isDeleteActivated == INVISIBLE) {
                isDeleteActivated = VISIBLE;
                allowDeleteButton.setBackgroundTintList(ColorStateList.valueOf(getApplicationContext().getColor(R.color.red)));
            }
            else {
                isDeleteActivated = INVISIBLE;
                allowDeleteButton.setBackgroundTintList(ColorStateList.valueOf(getApplicationContext().getColor(R.color.pink)));
            }
            fillList(isDeleteActivated);
        });


        if (! checkPermission()) {
            requestPermission();
        }
    }


    public void fillList(int isDeleteActivated) {
        recordingList.clear(); // not the best solution, I will change it in the future

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
                    notFoundTextView.setVisibility(VISIBLE);
                } else {
                    // recycler
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    recyclerView.setAdapter(new RecordingListAdapter(recordingList, isDeleteActivated, getApplicationContext()));

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
            fillList(isDeleteActivated);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (recyclerView != null) {
            recyclerView.setAdapter(new RecordingListAdapter(recordingList, isDeleteActivated, getApplicationContext()));
        }

        fillList(isDeleteActivated);
    }
}