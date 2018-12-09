package com.raz.todolist;

import com.raz.todolist.datamodel.ToDoItem;
import com.raz.todolist.datamodel.TodoData;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;


/**
 * Created by Razvan on 12/6/2018.
 */
public class DialogController {
    @FXML
    private TextField shortDescription;
    @FXML
    private TextArea detailsArea;
    @FXML
    private DatePicker deadLinePicker;

    public ToDoItem processResults(){
        String shortDesc = shortDescription.getText().trim();
        String detailsDesc = detailsArea.getText().trim();
        LocalDate datepicked = deadLinePicker.getValue();
        ToDoItem toDoItem = new ToDoItem(shortDesc,detailsDesc,datepicked);
        TodoData.getInstance().addToDoItem(toDoItem);
        return toDoItem;
    }
}
