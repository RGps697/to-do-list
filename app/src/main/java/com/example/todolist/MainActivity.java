package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ArrayList<task> tasksUncompleted;
    private ArrayList<task> tasksCompleted;
    private ArrayAdapter<task> tasksAdapterUncompleted;
    private ArrayAdapter<task> tasksAdapterCompleted;

    String currentList;

    ListView listViewTasks;
    Button buttonExport;
    Button buttonImport;
    Button buttonAdd;
    Button buttonToDo;
    Button buttonDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.smallicon)
                .setContentTitle("To Do")
                .setContentText("You have at least one task to complete")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        listViewTasks = (ListView)findViewById(R.id.listViewTasks);
        buttonExport = (Button)findViewById(R.id.buttonExport);
        buttonImport = (Button)findViewById(R.id.buttonImport);
        buttonAdd = (Button)findViewById(R.id.buttonAdd);
        buttonToDo = (Button)findViewById(R.id.buttonToDo);
        buttonDone = (Button)findViewById(R.id.buttonDone);

        tasksUncompleted = new ArrayList<task>();
        tasksCompleted = new ArrayList<task>();
        tasksAdapterUncompleted = new ArrayAdapter<task>(this, android.R.layout.simple_list_item_1, tasksUncompleted);
        tasksAdapterCompleted = new ArrayAdapter<task>(this, android.R.layout.simple_list_item_1, tasksCompleted);
        listViewTasks.setAdapter(tasksAdapterUncompleted);
        currentList = "todo";

        Date currentDate = new Date();
        for(int i = 0; i < tasksUncompleted.size(); i++){
            try {
                Date taskDate = tasksUncompleted.get(i).getDate();
                if((currentDate.getYear()>taskDate.getYear()) || (currentDate.getMonth()>taskDate.getMonth()) || (currentDate.getDate()>=taskDate.getDate())){
                    notificationManager.notify(5555, builder.build());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        XMLio export = new XMLio(tasksUncompleted, tasksCompleted);
        File file = new File(getFilesDir() + "/data.xml");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        export.readXML(file);
        tasksAdapterUncompleted.notifyDataSetChanged();
        tasksAdapterCompleted.notifyDataSetChanged();


        listViewTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Date currentDate = new Date();
                try {
                    Date taskDate = tasksUncompleted.get(0).getDate();
                    if((currentDate.getYear()>taskDate.getYear()) || (currentDate.getMonth()>taskDate.getMonth()) || (currentDate.getDate()>=taskDate.getDate())){
                        notificationManager.notify(5555, builder.build());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Intent intentEdit = new Intent (MainActivity.this, ActivityTaskEditor.class);
                Bundle taskData = new Bundle();
                String name;
                String description;
                Boolean completed;
                String date;
                int priority;
                if(currentList.equals("todo")) {
                    name = tasksUncompleted.get(i).getName();
                    description = tasksUncompleted.get(i).getDescription();
                    completed = tasksUncompleted.get(i).getCompleted();
                    date = tasksUncompleted.get(i).getDateAsString();
                    priority = tasksUncompleted.get(i).getPriority();
                }
                else{ //else if done
                    name = tasksCompleted.get(i).getName();
                    description = tasksCompleted.get(i).getDescription();
                    completed = tasksCompleted.get(i).getCompleted();
                    date = tasksCompleted.get(i).getDateAsString();
                    priority = tasksCompleted.get(i).getPriority();
                }
                taskData.putInt("ID", i);
                taskData.putString("Name", name);
                taskData.putString("Description", description);
                taskData.putBoolean("Completed", completed);
                taskData.putString("Date", date);
                taskData.putInt("Priority", priority);
                taskData.putString("Mode", "edit");
                intentEdit.putExtras(taskData);
                startActivityForResult(intentEdit, 102);
            }
        });

        listViewTasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(currentList.equals("todo")) {
                    tasksUncompleted.get(i).setCompleted(true);
                    tasksCompleted.add(tasksUncompleted.get(i));
                    tasksUncompleted.remove(i);
                    tasksAdapterUncompleted.notifyDataSetChanged();
                    tasksAdapterCompleted.notifyDataSetChanged();
                }
                else if(currentList.equals("done")){
                    tasksCompleted.get(i).setCompleted(false);
                    tasksUncompleted.add(tasksCompleted.get(i));
                    tasksCompleted.remove(i);
                    tasksAdapterUncompleted.notifyDataSetChanged();
                    tasksAdapterCompleted.notifyDataSetChanged();
                }
                return false;
            }
        });

        buttonExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                XMLio xmlExport = new XMLio(tasksUncompleted, tasksCompleted);
                ContextWrapper ctxWrapper = new ContextWrapper(getApplicationContext());
                File file = new File(ctxWrapper.getExternalFilesDir(Environment.DIRECTORY_DCIM), "data.xml");
                xmlExport.saveToXML(file);
            }
        });

        buttonImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                XMLio xmlImport = new XMLio(tasksUncompleted, tasksCompleted);
                ContextWrapper ctxWrapper = new ContextWrapper(getApplicationContext());
                File file = new File(ctxWrapper.getExternalFilesDir(Environment.DIRECTORY_DCIM), "data.xml");
                xmlImport.readXML(file);
                tasksAdapterUncompleted.notifyDataSetChanged();
                tasksAdapterCompleted.notifyDataSetChanged();
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAdd = new Intent (MainActivity.this, ActivityTaskEditor.class);
                Bundle taskData = new Bundle();
                taskData.putString("Mode", "add");
                intentAdd.putExtras(taskData);
                startActivityForResult(intentAdd, 101);
            }
        });

        buttonToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listViewTasks.setAdapter(tasksAdapterUncompleted);
                currentList = "todo";
            }
        });

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listViewTasks.setAdapter(tasksAdapterCompleted);
                currentList = "done";
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Bundle taskData = data.getExtras();
            int taskId = taskData.getInt("ID");
            String taskName = taskData.getString("Name");
            String taskDescription = taskData.getString("Description");
            Boolean taskCompleted = taskData.getBoolean("Completed");
            String taskDate = taskData.getString("Date");
            int taskPriority = taskData.getInt("Priority");

            if ((requestCode == 101) && (resultCode == Activity.RESULT_OK)) {
                task newTask = new task(taskName, taskDescription, taskDate, taskPriority);
                tasksAdapterUncompleted.add(newTask);
            }
            else if ((requestCode == 102) && (resultCode == Activity.RESULT_OK)) {
                if(taskCompleted){
                    tasksCompleted.get(taskId).editData(taskName, taskDescription, taskDate, taskPriority);
                }
                else {
                    tasksUncompleted.get(taskId).editData(taskName, taskDescription, taskDate, taskPriority);
                }
                tasksAdapterUncompleted.notifyDataSetChanged();
            }
            else if((requestCode == 102) && (resultCode == Activity.RESULT_CANCELED)){
                if(taskCompleted){
                    tasksAdapterCompleted.notifyDataSetChanged();
                    tasksAdapterCompleted.remove(tasksCompleted.get(taskId));
                }
                else {
                    tasksAdapterUncompleted.notifyDataSetChanged();
                    tasksAdapterUncompleted.remove(tasksUncompleted.get(taskId));
                }
            }
        }
        catch(Exception e){
                Log.i("ERROR", "ERROR ON INTENT RESULT");
        }
    }

    protected void onStop() {
        super.onStop();
        XMLio export = new XMLio(tasksUncompleted, tasksCompleted);
        File file = new File(getFilesDir() + "/data.xml");
        System.out.println(getFilesDir());
        export.saveToXML(file);
    }

}
