package com.pavel.databaseapp.data;

import com.google.firebase.firestore.QueryDocumentSnapshot;

public class Task{
    public static final String DESCRIPTION = "description";
    public static final String TASK_NAME = "taskName";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String EMPLOYEE = "employee";
    public static final String CREATOR = "creator";
    public static final String EMPLOYEE_MAIL = "employeeMail";
    public static final String IMPORTANCE  = "importance";
    public static final String IS_COMPLETE = "isComplete";
    public static final String IS_CHECKED = "isChecked";

    public String id;
    public String description;
    public String taskName;
    public String startDate;
    public String endDate;
    public String employee;
    public String creator;
    public String employeeMail;
    public String importance;
    public boolean isComplete;
    public boolean isChecked;

    public Task(String description,
                String taskName,
                String startDate,
                String endDate,
                String employee,
                String creator,
                String employeeMail,
                String importance, boolean isComplete, boolean isChecked) {
        this.description = description;
        this.taskName = taskName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.employee = employee;
        this.creator = creator;
        this.employeeMail = employeeMail;
        this.importance = importance;
        this.isComplete = isComplete;
        this.isChecked = isChecked;
    }

    public Task(){}

    public static Task parse(QueryDocumentSnapshot doc){
        return new Task(doc.getString(DESCRIPTION),doc.getString(TASK_NAME),doc.getString(START_DATE)
                ,doc.getString(END_DATE),doc.getString(EMPLOYEE), doc.getString(CREATOR),
                doc.getString(EMPLOYEE_MAIL), doc.getString(IMPORTANCE),
                Boolean.TRUE.equals(doc.getBoolean(IS_COMPLETE)), Boolean.TRUE.equals(doc.getBoolean(IS_CHECKED)));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDateRange(){
        return String.format("%s => %s",startDate,endDate);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getImportance() {
        return importance;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isChecked() {
        return isChecked;
    }
}
