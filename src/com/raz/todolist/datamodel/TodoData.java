package com.raz.todolist.datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

/**
 * Created by Razvan on 12/5/2018.
 */
public class TodoData {
    private static TodoData instance = new TodoData();
    private static String filename = "TodoList.txt";
    private ObservableList<ToDoItem> todoItems;
    private DateTimeFormatter formatter;

    public static TodoData getInstance(){
        return instance;
    }
    private TodoData(){
        formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
    }

    public ObservableList<ToDoItem> getTodoItems() {
        return todoItems;
    }

    public void addToDoItem(ToDoItem item){
        todoItems.add(item);
    }
//    public void setTodoItems(List<ToDoItem> todoItems) {
//        this.todoItems = todoItems;
//    }
    public void loadToDoUtems() throws IOException{
        todoItems = FXCollections.observableArrayList();
        Path path = Paths.get(filename);
        BufferedReader br = Files.newBufferedReader(path);
        String input;
        try{
            while ((input=br.readLine()) != null){
                String[] itemPieces  = input.split("\t");
                String shortDescription = itemPieces[0];
                String details = itemPieces[1];
                String date = itemPieces[2];

                LocalDate date1 = LocalDate.parse(date, formatter);
                ToDoItem toDoItem = new ToDoItem(shortDescription,details,date1);
                todoItems.add(toDoItem);
            }
        }finally {
            if(br!= null){
                br.close();
            }
        }

    }
    public void StoreToDoItems() throws IOException{
        Path path = Paths.get(filename);
        BufferedWriter bw = Files.newBufferedWriter(path);

        try {
            Iterator<ToDoItem> iterator = todoItems.iterator();
            while (iterator.hasNext()){
                ToDoItem item = iterator.next();
                bw.write(String.format("%s\t%s\t%s",
                        item.getShortDescription(),
                        item.getDetails(),
                        item.getDueDate().format(formatter)
                        ));
                bw.newLine();
            }
        }finally {
            if(bw!=null){
                bw.close();
            }
        }
    }



}
