package com.mirra.bmp4;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


interface ItemClickListener {
    void OnItemClick(View v, int position);
}

interface CheckedChangedItemListener {
    void OnCheckedChanged(boolean checked, int position);
}

interface Saver {
    void SaveToFile();
}

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder>
        implements CheckedChangedItemListener{

    @Override
    public void OnCheckedChanged(boolean checked, int position) {
        events.get(position).isFinished = checked;
        if (!binding) {
            notifyItemChanged(position);
            if (saver != null)
                saver.SaveToFile();
        }
    }

    class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

        TextView tvName, tvComment, tvDate;
        CheckBox cbSolved;
        LinearLayout llHor;
        ItemClickListener listener;
        CheckedChangedItemListener cbListener;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.event_tv_name);
            tvComment = itemView.findViewById(R.id.event_tv_comment);
            tvDate = itemView.findViewById(R.id.event_tv_date);
            cbSolved = itemView.findViewById(R.id.event_cb_solved);
            llHor = itemView.findViewById(R.id.event_hor);
            llHor.setOnClickListener(this);
            cbSolved.setOnCheckedChangeListener(this);
        }

        void bind(UserEvent event, ItemClickListener listener)
        {
            tvName.setText(event.name);
            tvComment.setText(event.Comment.isEmpty() ? "*нет комментария*" : event.Comment);
            Calendar c = Calendar.getInstance();
            c.set(event.year, event.month - 1, event.day);
            DateFormat sdf = SimpleDateFormat.getDateInstance();
            tvDate.setText(sdf.format(c.getTime()));
            cbSolved.setChecked(event.isFinished);
            if (event.isFinished)
                tvName.setTextColor(tvName.getResources()
                        .getColor(R.color.colorSolved));
            else
                tvName.setTextColor(Color.BLACK);
            if (getAdapterPosition() % 2 == 0)
                llHor.setBackgroundColor(Color.WHITE);
            else
                llHor.setBackgroundColor(llHor.getResources()
                        .getColor(R.color.colorEvenEvent));
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            if (listener != null)
                listener.OnItemClick(v, getAdapterPosition());
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (cbListener != null)
                cbListener.OnCheckedChanged(isChecked, getAdapterPosition());
        }
    }

    ArrayList<UserEvent> events;
    ItemClickListener listener;
    boolean binding = false;
    Saver saver;

    public EventAdapter(ArrayList<UserEvent> events)
    {
        this.events = events;
    }

    public void setOnItemClickListener(ItemClickListener listener)
    {
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LinearLayout ll = (LinearLayout) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.event_view, viewGroup, false);
        EventViewHolder evh = new EventViewHolder(ll);
        evh.listener = listener;
        evh.cbListener = this;
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.EventViewHolder eventViewHolder, int i) {
        binding = true;
        eventViewHolder.bind(events.get(i), listener);
        binding = false;
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}
