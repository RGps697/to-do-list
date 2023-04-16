package com.example.todolist;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ActivityTaskEditor extends AppCompatActivity {

    TextView textViewName;
    EditText editTextName;
    TextView textViewDescription;
    EditText editTextDescription;
    TextView textViewDate;
    EditText editTextDate;
    Spinner spinnerPriority;
    Button buttonConfirm;
    Button buttonDelete;
    Intent intent;
    Bundle taskData;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        textViewName = (TextView) findViewById(R.id.textViewName);
        editTextName = (EditText) findViewById(R.id.editTextName);
        textViewDescription = (TextView) findViewById(R.id.textViewDescription);
        editTextDescription = (EditText) findViewById(R.id.editTextDescription);
        textViewDate = (TextView) findViewById(R.id.textViewDate);
        editTextDate = (EditText) findViewById(R.id.editTextDate);
        buttonConfirm = (Button) findViewById(R.id.buttonConfirm);
        buttonDelete = (Button) findViewById(R.id.buttonDelete);



        spinnerPriority = findViewById(R.id.spinnerPriority);
        String[] priorityTipes = new String[]{"Low", "Medium", "High"};
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, priorityTipes);
        spinnerPriority.setAdapter(priorityAdapter);

        intent = getIntent();
        taskData = intent.getExtras();
        String mode = taskData.getString("Mode");

        if(mode.equals("add")){
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, 24);
            date = calendar.getTime();
            editTextDate.setText(formatter.format(date));
        }
        else if(mode.equals("edit")) {
            editTextName.setText(taskData.getString("Name"));
            editTextDescription.setText(taskData.getString("Description"));
            editTextDate.setText(taskData.getString("Date"));
            spinnerPriority.setSelection(taskData.getInt("Priority"));
            buttonConfirm.setText("Edit");
        }

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if(mode.equals("add")){
                    String name = editTextName.getText().toString();
                    String description = editTextDescription.getText().toString();
                    taskData.putString("Name", name);
                    taskData.putString("Description", description);
                    taskData.putString("Priority", description);
                    intent.putExtras(taskData);
                    setResult(Activity.RESULT_OK, intent);
                }
                else if(mode.equals("edit")) {
                    String name = editTextName.getText().toString();
                    String description = editTextDescription.getText().toString();
                    taskData.putString("Name", name);
                    taskData.putString("Description", description);
                    intent.putExtras(taskData);
                    setResult(Activity.RESULT_OK, intent);
                }*/

                String name = editTextName.getText().toString();
                String description = editTextDescription.getText().toString();
                String date = editTextDate.getText().toString();
                int priority = spinnerPriority.getSelectedItemPosition();
                taskData.putString("Name", name);
                taskData.putString("Description", description);
                taskData.putString("Date", date);
                taskData.putInt("Priority", priority);
                intent.putExtras(taskData);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED, intent);

                finish();
            }
        });

    }
}