package com.raz.todolist;

import com.raz.todolist.datamodel.ToDoItem;
import com.raz.todolist.datamodel.TodoData;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class Controller {
    @FXML
    private List<ToDoItem> tdi;
    @FXML
    private ListView<ToDoItem> ToDoListView;
    @FXML
    private TextArea textarea;
    @FXML
    private Label deadlineLabel;
    @FXML
    private BorderPane mainborderpane;
    @FXML
    public void initialize() {
//        ToDoItem item1 = new ToDoItem("Do Shopping", "Get all necessary groceries for the next 2 weeks, ", LocalDate.of(2018, Month.DECEMBER, 13));
//        ToDoItem item2 = new ToDoItem("Christmas List", "Buy presents for the whole family, also for Girlfriend and her Parents", LocalDate.of(2018, Month.DECEMBER, 24));
//        ToDoItem item3 = new ToDoItem("Get a job in Programming", "Work hard, do a few private projects and land the dreamjob in programming", LocalDate.of(2019, Month.MARCH, 1));
//        tdi = new ArrayList<ToDoItem>();
//        tdi.add(item1);
//        tdi.add(item2);
//        tdi.add(item3);
//        TodoData.getInstance().setTodoItems(tdi);
        ToDoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoItem>() {
            @Override
            public void changed(ObservableValue<? extends ToDoItem> observable, ToDoItem oldValue, ToDoItem newValue) {
                if(newValue != null){
                    ToDoItem item = ToDoListView.getSelectionModel().getSelectedItem();
                    textarea.setText(item.getDetails());
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                    deadlineLabel.setText(dateTimeFormatter.format(item.getDueDate()));
                }
            }
        });

        ToDoListView.setItems(TodoData.getInstance().getTodoItems());
        ToDoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        ToDoListView.getSelectionModel().selectFirst();
        ToDoListView.setCellFactory(new Callback<ListView<ToDoItem>, ListCell<ToDoItem>>() {
            @Override
            public ListCell<ToDoItem> call(ListView<ToDoItem> param) {

                ListCell<ToDoItem> cell = new ListCell<ToDoItem>(){
                    @Override
                    protected void updateItem(ToDoItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty){
                            setText(null);
                        }else{
                            setText(item.getShortDescription());
                            if(item.getDueDate().isBefore(LocalDate.now().plusDays(1))){
                                setTextFill(Color.RED);
                            }else if(item.getDueDate().equals(LocalDate.now().plusDays(1))){
                                setTextFill(Color.BROWN);
                            }
                        }
                    }
                };
                return cell;
            }
        });

    }
    @FXML
    public void handleClickListView(){
        ToDoItem item = ToDoListView.getSelectionModel().getSelectedItem();
        textarea.setText(item.getDetails());
        deadlineLabel.setText(item.getDueDate().toString());
//        StringBuilder sb = new StringBuilder(item.getDetails());
//        sb.append("\n\n\n\n");
//        sb.append("Due Date: ");
//        sb.append(item.getDueDate().toString());
//        textarea.setText(sb.toString());

    }
    @FXML
    public void showNewItemDialog(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainborderpane.getScene().getWindow());
        dialog.setTitle("Add new ToDo Item");
        dialog.setHeaderText("Use this option to create a new ToDo Item");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("toDoItemDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        }catch (IOException e){
            System.out.println("Could not load the DialogMenu");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK){

            DialogController controller = fxmlLoader.getController();
            ToDoItem toDoItem  = controller.processResults();
//            ToDoListView.getItems().setAll(TodoData.getInstance().getTodoItems());
            ToDoListView.getSelectionModel().select(toDoItem);
        }
    }

//148 next
}
