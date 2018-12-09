package com.raz.todolist.datamodel;

import java.time.LocalDate;

/**
 * Created by Razvan on 12/3/2018.
 */
public class ToDoItem {
    private String shortDescription;
    private String details;
    private LocalDate dueDate;

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public ToDoItem(String shortDescription, String details, LocalDate dueDate) {
        this.shortDescription = shortDescription;
        this.details = details;
        this.dueDate = dueDate;
    }

    @Override
    public String toString() {
        return  shortDescription;
    }
}
