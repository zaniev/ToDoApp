package com.raz.todolist;

import com.raz.todolist.datamodel.ToDoItem;
import com.raz.todolist.datamodel.TodoData;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import javafx.scene.input.KeyEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

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
    private ContextMenu listContextMenu;
    @FXML
    private ToggleButton FilterToggleButton;

    private FilteredList<ToDoItem> filteredList;

    private  Predicate<ToDoItem> showAllItems;

    private Predicate<ToDoItem> showTodayItems;
    public void initialize() {
        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ToDoItem item= ToDoListView.getSelectionModel().getSelectedItem();
                deleteItem(item);
            }
        });
        listContextMenu.getItems().addAll(deleteMenuItem);
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
        showAllItems = new Predicate<ToDoItem>() {
            @Override
            public boolean test(ToDoItem item) {
                return true;
            }
        };

        showTodayItems = new Predicate<ToDoItem>() {
            @Override
            public boolean test(ToDoItem item) {
                return item.getDueDate().equals(LocalDate.now());
            }
        };

        filteredList = new FilteredList<ToDoItem>(TodoData.getInstance().getTodoItems(), showAllItems);

        SortedList<ToDoItem> sortedList = new SortedList<ToDoItem>(filteredList, new Comparator<ToDoItem>() {
            @Override
            public int compare(ToDoItem o1, ToDoItem o2) {
                return o1.getDueDate().compareTo(o2.getDueDate());
            }
        });
        //ToDoListView.setItems(TodoData.getInstance().getTodoItems());
        ToDoListView.setItems(sortedList);
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
                cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                            if (isNowEmpty){
                                cell.setContextMenu(null);
                            }else{
                                cell.setContextMenu(listContextMenu);
                            }
                });

                return cell;
            }
        });

    }
    @FXML
    public void handleKeyPressed(KeyEvent keyEvent){
        ToDoItem selectedItem = ToDoListView.getSelectionModel().getSelectedItem();
        if(selectedItem != null){
            if(keyEvent.getCode().equals(KeyCode.DELETE)){
                deleteItem(selectedItem);
            }
        }
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

    public void deleteItem(ToDoItem item){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Todo Item");
        alert.setHeaderText("Delete Item: " + item.getShortDescription());
        alert.setContentText("Are you certain? Press Ok to confirm, or cancel to return");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK){
            TodoData.getInstance().deleteTodoItem(item);
        }
    }

    public void handleFilterButton(){
        ToDoItem selectedItem = ToDoListView.getSelectionModel().getSelectedItem();
        if(FilterToggleButton.isSelected()){
            filteredList.setPredicate(showTodayItems);
                if(filteredList.isEmpty()){
                    textarea.clear();
                    deadlineLabel.setText("");
                }else if(filteredList.contains(selectedItem)){
                    ToDoListView.getSelectionModel().select(selectedItem);
                }else{
                    ToDoListView.getSelectionModel().selectFirst();
                }

        }else{
            filteredList.setPredicate(showAllItems);
            ToDoListView.getSelectionModel().select(selectedItem);
        }
    }

    public void exitButtonHandler(){
        Platform.exit();
    }

//next 151
}
