package com.mirra.bmp4;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class EventInfoActivity extends AppCompatActivity
        implements DialogInterface.OnClickListener, View.OnClickListener,
        DatePickerDialog.OnDateSetListener, CompoundButton.OnCheckedChangeListener {

    TextView tv_date, tv_id;
    EditText tv_name, tv_comment;
    Button btn_ok;
    ImageButton btn_date;
    CheckBox cb_solved;
    UserEvent event;
    //int day, month, year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        event = (UserEvent) getIntent().getSerializableExtra("event");
        setTitle(event.name);
        setContentView(R.layout.activity_event_info);
        tv_name = findViewById(R.id.tv_einfo_name);
        tv_comment = findViewById(R.id.tv_einfo_comment);
        tv_date = findViewById(R.id.tv_einfo_date);
        btn_ok = findViewById(R.id.btn_einfo_ok);
        btn_ok.setOnClickListener(this);
        cb_solved = findViewById(R.id.cb_einfo_solved);
        tv_name.setText(event.name);
        tv_comment.setText(event.Comment);
        tv_date.setText(event.day + "." + event.month + "." + event.year);
        cb_solved.setChecked(event.isFinished);
        cb_solved.setOnCheckedChangeListener(this);
        btn_date = findViewById(R.id.einfo_btn_date);
        btn_date.setOnClickListener(this);
        tv_id = findViewById(R.id.tv_einfo_id);
        tv_id.setText("id: " + event.id);
        ChangeEditables();
    }

    void ChangeEditables()
    {
        if (event.isFinished) {
            tv_name.setInputType(InputType.TYPE_NULL);
            tv_comment.setInputType(InputType.TYPE_NULL);
            btn_date.setEnabled(false);
        }
        else
        {
            tv_name.setInputType(InputType.TYPE_CLASS_TEXT);
            tv_comment.setInputType(InputType.TYPE_CLASS_TEXT);
            btn_date.setEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_event_info, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_del_event)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Точно удалить?");
            builder.setPositiveButton("Супер точно", this);
            builder.setNegativeButton("Не точно", this);
            builder.create().show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE)
        {
            Intent intent = new Intent();
            intent.putExtra("delete", true);
            intent.putExtra("id", event.id);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btn_ok)
        {
            String name = tv_name.getText().toString().trim();
            if (name.isEmpty())
            {
                Toast.makeText(this, "Заполните поля", Toast.LENGTH_SHORT).show();
                return;
            }
            event.isFinished = cb_solved.isChecked();
            event.name = name;
            event.Comment = tv_comment.getText().toString().trim();
            Intent intent = new Intent();
            intent.putExtra("event", event);
            setResult(RESULT_OK, intent);
            finish();
        }
        else if (v == btn_date)
        {
            DatePickerDialog dialog = new DatePickerDialog(this, this,
                    event.year, event.month - 1, event.day);
            dialog.show();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        event.year = year;
        event.month = month + 1;
        event.day = dayOfMonth;
        tv_date.setText(event.day + "." + event.month + "." + event.year);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        event.isFinished = isChecked;
        ChangeEditables();
    }
}
