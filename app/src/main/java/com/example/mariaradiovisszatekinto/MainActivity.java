package com.example.mariaradiovisszatekinto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    private Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd.", Locale.ENGLISH);

    private List<String> listOfIntervals = new ArrayList<>();
    int intervalIndex = 0;

    TextView datePickerTextview, intervalTextView;
    Button leftDatePickerButton, rightDatePickerButton, leftIntervalButton, rightIntervalButton,
            downloadButton, savedRecordingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET}, 124);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 125);
        }


        // findViewById
        {
            datePickerTextview = findViewById(R.id.datePickerTextView);
            intervalTextView = findViewById(R.id.intervalTextView);

            leftDatePickerButton = findViewById(R.id.dateLeftButton);
            rightDatePickerButton = findViewById(R.id.dateRightButton);
            leftIntervalButton = findViewById(R.id.intervalLeftButton);
            rightIntervalButton = findViewById(R.id.intervalRightButton);
            downloadButton = findViewById(R.id.downloadButton);
            savedRecordingsButton = findViewById(R.id.savedRecordingsButton);
        }

        // initializing
        {
            datePickerTextview.setText(dateFormat.format(calendar.getTime()));
            for (int i = 0; i <= calendar.get(Calendar.HOUR_OF_DAY); i++) {
                listOfIntervals.add(String.format(Locale.ENGLISH, "%02d-%02d", i, i + 1));
            }
            intervalIndex = listOfIntervals.size() - 1;
            intervalTextView.setText(listOfIntervals.get(intervalIndex));
        }

        // onClickListeners
        {
            leftDatePickerButton.setOnClickListener(view -> {
                if (calendar.getTimeInMillis() > 1656277200000L) {
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    updateUI();
                }
            });
            datePickerTextview.setOnClickListener(view -> {
                DialogFragment fragment = new DatePickerFragment();
                fragment.show(getSupportFragmentManager(), "DatePicker");
            });
            rightDatePickerButton.setOnClickListener(view -> {
                Calendar today = Calendar.getInstance();
                today.set(Calendar.HOUR, 0);
                today.set(Calendar.MINUTE, 0);
                today.set(Calendar.SECOND, 0);
                today.set(Calendar.MILLISECOND, 0);

                if (calendar.getTimeInMillis() < today.getTimeInMillis()) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    updateUI();
                }

            });

            leftIntervalButton.setOnClickListener(view -> {

                if (intervalIndex > 0 && calendar.getTimeInMillis() >= 1_656_288_000_000L) {
                    intervalIndex--;
                }
                intervalTextView.setText(listOfIntervals.get(intervalIndex));
            });
            rightIntervalButton.setOnClickListener(view -> {
                if (intervalIndex < listOfIntervals.size() - 1) {
                    intervalIndex++;
                }
                intervalTextView.setText(listOfIntervals.get(intervalIndex));
            });

            downloadButton.setOnClickListener(view -> {
                initDownload();
            });

            savedRecordingsButton.setOnClickListener(view -> {
                Intent intent = new Intent(this, RecordingsActivity.class);
                startActivity(intent);
            });
        }
    }

    private void updateUI() {
        Calendar today = Calendar.getInstance();

        datePickerTextview.setText(dateFormat.format(calendar.getTime()));

        listOfIntervals.clear();
        if (calendar.get(Calendar.DATE) == today.get(Calendar.DATE)) {
            for (int i = 0; i < today.get(Calendar.HOUR_OF_DAY) + 1; i++) {
                listOfIntervals.add(String.format(Locale.ENGLISH, "%02d-%02d", i, i + 1));
            }
        } else {
            for (int i = 0; i < 24; i++) {
                listOfIntervals.add(String.format(Locale.ENGLISH, "%02d-%02d", i, i + 1));
            }
        }
        intervalIndex = listOfIntervals.size() - 1;
        intervalTextView.setText(listOfIntervals.get(intervalIndex));
    }


    private void initDownload() {

        String uri = String.format("http://archivum.mariaradio.ro/Archivum/MRE_%s_%s-00.mp3",
                dateFormat.format(calendar.getTime()).substring(0, 10),
                listOfIntervals.get(intervalIndex).substring(0, 2)
        );

        download(getApplicationContext(),
                dateFormat.format(calendar.getTime()) + listOfIntervals.get(intervalIndex),
                ".mp3",
                Environment.getExternalStorageDirectory().getPath() + "/Music/MariaRadio/",
                uri.trim());

    }

    private void download(Context context, String filename, String fileExtension, String DesignationDirectory, String url) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationUri(Uri.fromFile(new File(DesignationDirectory + filename + fileExtension)));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

        long downloadId = downloadManager.enqueue(request);



        // getting data from the state of the request
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.my_download_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        BroadcastReceiver onComplete=new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                dialog.dismiss();

                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                Cursor cursor = downloadManager.query(query);

                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (columnIndex >= 0) {
                        int status = cursor.getInt(columnIndex);
                        if (status == DownloadManager.STATUS_FAILED) {
                            Toast.makeText(MainActivity.this, "Ebben az időpontban nincs letölthető archív felvétel!", Toast.LENGTH_LONG).show();

                            return;
                        }
                    }
                }

                Intent recordingsActivityIntent = new Intent(MainActivity.this, RecordingsActivity.class);
                startActivity(recordingsActivityIntent);
            }
        };

        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    public void processDatePickerResult(int y, int m, int d) {
        Calendar newCalendar = Calendar.getInstance();
        newCalendar.set(Calendar.YEAR, y);
        newCalendar.set(Calendar.MONTH, m);
        newCalendar.set(Calendar.DAY_OF_MONTH, d);
        newCalendar.set(Calendar.HOUR_OF_DAY, 0);
        newCalendar.set(Calendar.MINUTE, 0);
        newCalendar.set(Calendar.SECOND, 0);
        newCalendar.set(Calendar.MILLISECOND, 0);


        calendar.set(Calendar.YEAR, y);
        calendar.set(Calendar.MONTH, m);
        calendar.set(Calendar.DAY_OF_MONTH, d);


        updateUI();
    }
}