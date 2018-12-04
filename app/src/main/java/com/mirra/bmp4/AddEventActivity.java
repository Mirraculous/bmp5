package com.mirra.bmp4;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;

public class AddEventActivity extends AppCompatActivity implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener {

    ImageButton btn_date;
    Button btn_finishAdd;
    boolean dateIsSet;
    int day, month, year;
    TextView tv_date;
    EditText editName, editComment;
    CheckBox cb_isFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        btn_date = findViewById(R.id.btn_date);
        btn_date.setOnClickListener(this);
        btn_finishAdd = findViewById(R.id.btn_finishAdd);
        btn_finishAdd.setOnClickListener(this);
        tv_date = findViewById(R.id.tv_date);
        editName = findViewById(R.id.editName);
        editComment = findViewById(R.id.editComment);
        cb_isFinished = findViewById(R.id.cb_isFinished);
        dateIsSet = false;
        Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH) + 1;
        year = calendar.get(Calendar.YEAR);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_date:
                DatePickerDialog dpd = new DatePickerDialog(this, this,
                        year, month - 1, day);
                dpd.show();

                break;
            case R.id.btn_finishAdd:
                String name = editName.getText().toString();
                String Comment = editComment.getText().toString();
                boolean isFinished = cb_isFinished.isChecked();
                if (name.isEmpty() || !dateIsSet)
                {
                    Toast.makeText(this, "Заполните поля", Toast.LENGTH_SHORT).show();
                    return;
                }
                UserEvent event = new UserEvent(name, day, month, year, isFinished, Comment);
                Intent intent = new Intent();
                intent.putExtra("event", event);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        this.year = year;
        this.month = month + 1;
        this.day = dayOfMonth;
        tv_date.setText(dayOfMonth + "." + this.month + "." + year);
        dateIsSet = true;
    }
}
