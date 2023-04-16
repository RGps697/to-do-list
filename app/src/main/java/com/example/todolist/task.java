package com.example.todolist;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class task {

    private int taskId;
    private String name;
    private String description;
    private Boolean completed;
    private String date;
    private int priority;

    public task(String name, String description, String date, int priority) {
        this.name = name;
        this.description = description;
        this.completed = false;
        this.date = date;
        this.priority = priority;
    }

    public task(String name, String description, Boolean completed, String date, int priority) {
        this.name = name;
        this.description = description;
        this.completed = completed;
        this.date = date;
        this.priority = priority;
    }

    public void editData(String name, String description, String date, int priority) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.priority = priority;
    }

    @Override
    public String toString(){
        return name;
    }

    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }

    public void setCompleted(Boolean value){
        completed = value;
    }

    public Boolean getCompleted(){
        return completed;
    }

    public void setDate(String date){
        this.date = date;
    }

    public Date getDate() throws ParseException {
        Date toDateFormat = new SimpleDateFormat("dd/MM/yyyy").parse(date);
        return toDateFormat;
    }

    public String getDateAsString() {
        return date;
    }

    public void setPriority(int priority){
        this.priority = priority;
    }

    public int getPriority(){
        return priority;
    }

}

