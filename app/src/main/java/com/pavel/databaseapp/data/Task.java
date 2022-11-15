package com.pavel.databaseapp.data;

import com.google.firebase.firestore.QueryDocumentSnapshot;

public class Task {
    public static final String DESCRIPTION = "description";
    public static final String TASK_NAME = "task_name";
    public static final String START_DATE = "start";
    public static final String END_DATE = "end";
    public static final String EMPLOYEE = "performer";
    public static final String CREATOR = "creator";

    public String description;
    public String taskName;
    public String startDate;
    public String endDate;
    public String employee;
    public String creator;

    public Task(String description, String taskName, String startDate, String endDate, String employee, String creator) {
        this.description = description;
        this.taskName = taskName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.employee = employee;
        this.creator = creator;
    }
    public static Task parse(QueryDocumentSnapshot doc){
        return new Task(doc.getString(DESCRIPTION),doc.getString(TASK_NAME),doc.getString(START_DATE)
                ,doc.getString(END_DATE),doc.getString(EMPLOYEE),doc.getString(CREATOR));
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
}
