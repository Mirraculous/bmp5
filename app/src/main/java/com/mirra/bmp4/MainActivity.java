package com.mirra.bmp4;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity implements ItemClickListener,
        Saver, AdapterView.OnItemSelectedListener {

    ArrayList<UserEvent> events, eventsFiltered;
    RecyclerView rv;
    EventAdapter rva;
    RecyclerView.LayoutManager rvlm;
    int lastId = 0;
    String filename = "events.json";
    Gson gson;
    Spinner spinner;
    int sortMode = 0;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Задачи");
        events = new ArrayList<>();
        gson = new Gson();
        CreateFile();
        if (events.size() > 0)
            lastId = events.get(events.size() - 1).id;
        setContentView(R.layout.activity_main);
        rv = findViewById(R.id.rv_main);

        rvlm = new LinearLayoutManager(this);
        ((LinearLayoutManager) rvlm).setReverseLayout(true);
        ((LinearLayoutManager) rvlm).setStackFromEnd(true);
        rv.setLayoutManager(rvlm);

        rva = new EventAdapter(events);
        rva.setOnItemClickListener(this);
        rva.saver = this;
        rv.setAdapter(rva);
    }

    void CreateFile() //создание файла или получение инфы из имеющегося
    {
        File storage = new File(getFilesDir(), filename);
        if (!storage.exists())
        {
            Log.d("log1", "creating file " + storage.getAbsolutePath());
            try {
                storage.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                FileInputStream fis = new FileInputStream(storage);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null)
                {
                    content.append(line);
                }
                br.close();
                isr.close();
                fis.close();
                Log.d("log1", content.toString());
                if (content.length() == 0)
                    events = new ArrayList<>();
                else
                    events = gson.fromJson(content.toString(),
                        new TypeToken<ArrayList<UserEvent>>(){}.getType());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void SaveToFile()
    {
        File storage = new File(getFilesDir(), filename);
        if (!storage.exists())
        {
            Log.d("log1", storage.getPath() + "doesnt exist");
            try {
                storage.createNewFile();
                Log.d("log1", "creating file " + storage.getPath());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else {
            Log.d("log1", "saving");
            String json = gson.toJson(events);
            try {
                FileWriter writer = new FileWriter(storage);
                writer.write(json);
                writer.flush();
                Log.d("log1", "saved");
                writer.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void PerformSort()
    {
        MenuItem btn = menu.findItem(R.id.menu_btn_sort);
        if (sortMode == 0)
        {
            Collections.sort(eventsFiltered, new UserEvent.IdComparator());
            rva.notifyDataSetChanged();
            btn.setIcon(android.R.drawable.checkbox_off_background);
            btn.setTitle("Сортировка ✘");
        }
        else
        {
            Collections.sort(eventsFiltered, new UserEvent.DateComparator());
            rva.notifyDataSetChanged();
            btn.setIcon(android.R.drawable.checkbox_on_background);
            btn.setTitle("Сортировка ✓");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        spinner = (Spinner) menu.findItem(R.id.main_spinner).getActionView();
        if (spinner == null)
            Log.d("log1", "spinner proeban");
        ArrayAdapter<CharSequence> ada = ArrayAdapter.createFromResource(this,
                R.array.spinner_choices, R.layout.spinner_elem);
        ada.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(ada);
        spinner.setOnItemSelectedListener(this);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.itemAddEvent)
        {
            Intent intent = new Intent(this, AddEventActivity.class);
            startActivityForResult(intent, 1);
            return true;
        }
        else if (item.getItemId() == R.id.menu_btn_sort)
        {
            sortMode = (sortMode + 1) % 2;
            PerformSort();
        }
        else if (item.getItemId() == R.id.menu_btn_del_solved)
        {
            ListIterator<UserEvent> iter = events.listIterator();
            while (iter.hasNext())
            {
                if (iter.next().isFinished)
                    iter.remove();
            }
            SaveToFile();
            onItemSelected(null, null, spinner.getSelectedItemPosition(),
                    spinner.getSelectedItemId());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data == null || resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case 1:
                AddEvent(data);
                break;
            case 2:
                onEInfoResult(data);
                break;
        }
        SaveToFile();
        if (spinner.getSelectedItemPosition() != 0 || sortMode != 0) //resort & refilter
            onItemSelected(null, null,
                    spinner.getSelectedItemPosition(), spinner.getSelectedItemId());
        super.onActivityResult(requestCode, resultCode, data);
    }

    void onEInfoResult(Intent data)
    {
        Boolean del = data.getBooleanExtra("delete", false);
        if (del)
        {
            int id = data.getIntExtra("id", -1);
            if (id == -1)
                return;
            int i;
            for (i = 0; i < events.size(); i++)
                if (events.get(i).id == id)
                    break;
            if (i >= events.size())
                return;
            events.remove(i);
            rva.notifyItemRemoved(i);
            rva.notifyItemRangeChanged(i, events.size() - i);
        }
        else {
            UserEvent event = (UserEvent) data.getSerializableExtra("event");
            int id = event.id;
            int i;
            for (i = 0; i < events.size(); i++)
                if (events.get(i).id == id)
                    break;
            if (i >= events.size())
                return;
            events.set(i, event);
            rva.notifyItemChanged(i);
        }
    }

    void AddEvent(Intent data)
    {
        UserEvent event = (UserEvent) data.getSerializableExtra("event");
        event.id = lastId++ + 1;
        events.add(event);
        rva.notifyItemInserted(events.size() - 1);
    }

    @Override
    public void OnItemClick(View v, int position) {
        Intent intent = new Intent(this, EventInfoActivity.class);
        intent.putExtra("event", events.get(position));
        startActivityForResult(intent, 2);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d("log1", "selected spinner pos " + position);
        switch (position)
        {
            case 0:
                eventsFiltered = events;
                break;
            case 1:
                eventsFiltered = new ArrayList<>();
                for (UserEvent event: events)
                    if (event.isFinished)
                        eventsFiltered.add(event);
                break;
            case 2:
                eventsFiltered = new ArrayList<>();
                for (UserEvent event: events)
                    if (!event.isFinished)
                        eventsFiltered.add(event);
                break;
        }
        PerformSort(); //resort after filtering
        rva.events = eventsFiltered;
        rva.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
