package com.example.mariaradiovisszatekinto;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class RecordingListAdapter extends RecyclerView.Adapter<RecordingListAdapter.ViewHolder> {

    ArrayList<AudioModel> recordingList;
    Context context;

    public RecordingListAdapter(ArrayList<AudioModel> recordingList, Context context) {
        this.recordingList = recordingList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AudioModel recordingData = recordingList.get(position);
        holder.titleTextView.setText(recordingData.getTitle());

        if (MyMediaPlayer.currentIndex == position && MyMediaPlayer.instance.isPlaying()) {
            holder.titleTextView.setTextColor(Color.parseColor("#FF0000"));
        } else {
            holder.titleTextView.setTextColor(Color.parseColor("#000000"));
        }

        holder.titleTextView.setOnClickListener(view -> {

            MyMediaPlayer.currentIndex = holder.getAdapterPosition();

            Intent intent = new Intent(context, MusicPlayerActivity.class);
            intent.putExtra("LIST", recordingList);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        holder.deleteButton.setOnClickListener(view -> {
            deleteRecording(recordingList, holder.getAdapterPosition());
        });

    }

    private void deleteRecording(ArrayList<AudioModel> recordingList, int adapterPosition) {

        File file = new File(recordingList.get(adapterPosition).path);
        if (file.delete()) {
            recordingList.remove(adapterPosition);

            notifyItemRemoved(adapterPosition);
            MyMediaPlayer.getInstance().pause();
        }
        
    }

    @Override
    public int getItemCount() {
        return recordingList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        ImageView iconImageView;
        ImageView deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.musicTitleText);
            iconImageView = itemView.findViewById(R.id.icon_view);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }


    }
}
