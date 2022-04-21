package fsd.assignment.assignment1;

import fsd.assignment.assignment1.datamodel.Student;
import fsd.assignment.assignment1.datamodel.StudentData;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Controller {

    // These variables correspond to the <top> of main-view.fxml
    @FXML
    private TextField studId;

    @FXML
    private TextField yearStudy;

    @FXML
    private ChoiceBox<String> mod1Choice;

    @FXML
    private ChoiceBox<String> mod2Choice;

    @FXML
    private ChoiceBox<String> mod3Choice;

    private String choice1, choice2, choice3;

    private String modChoices[] = {"OOP", "Data Algo", "DS", "Maths", "AI",
            "Adv Programming", "Project"};

    @FXML
    private Label validateStudent;


    // This variable corresponds to the <left> i.e. the studentListView
    @FXML
    private ListView<Student> studentListView;


    // These variables correspond to the <bottom> part of the border
    @FXML
    private Label yearStudyView;

    @FXML
    private Label mod1View;

    @FXML
    private Label mod2View;

    @FXML
    private Label mod3View;


    // Used for the right-click regarding Edit / Delete
    @FXML
    private ContextMenu listContextMenu;

    // Used when switching windows from add to edit
    @FXML
    private BorderPane mainWindow;

    // Used to add a student to the ArrayList for addStudentData()
    public Student studentToAdd;


    public void initialize()
    {
        // Listens for selected studID from ListView
        studentListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Student>() {
            @Override
            public void changed(ObservableValue<? extends Student> observable, Student oldValue, Student newValue) {

                Student studIdSelected = studentListView.getSelectionModel().getSelectedItem();

                yearStudyView.setText(studIdSelected.getYearOfStudy());
                mod1View.setText(studIdSelected.getModule1());
                mod2View.setText(studIdSelected.getModule2());
                mod3View.setText(studIdSelected.getModule3());
            }
        });
        // The setOnAction ensures that when a ChoiceBox is selected the getChoice() grabs the selected choice
        mod1Choice.setOnAction(this::getChoice);
        mod2Choice.setOnAction(this::getChoice);
        mod3Choice.setOnAction(this::getChoice);

        mod1Choice.getItems().addAll(modChoices);
        mod2Choice.getItems().addAll(modChoices);
        mod3Choice.getItems().addAll(modChoices);

        // Deleting a student
        listContextMenu = new ContextMenu();

        MenuItem deleteStudent = new MenuItem("Delete?");

        deleteStudent.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Student item = studentListView.getSelectionModel().getSelectedItem();
                deleteStudent(item);
            }
        });

        // Editing a student
        listContextMenu = new ContextMenu();

        MenuItem editStudent = new MenuItem("Edit?");

        editStudent.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Student item = studentListView.getSelectionModel().getSelectedItem();
                editStudent(item);
            }
        });

        // To ensure that the contextMenu appears as part of the above actions
        listContextMenu.getItems().add(deleteStudent);
        listContextMenu.getItems().add(editStudent);

        // To ensure access to a particular cell in the studentListView
        studentListView.setCellFactory(new Callback<ListView<Student>, ListCell<Student>>() {
            public ListCell<Student> call(ListView<Student> param) {
                ListCell<Student> cell = new ListCell<Student>() {

                    @Override
                    protected void updateItem(Student stu, boolean empty) {
                        super.updateItem(stu, empty);
                        if(empty){
                            setText(null);
                        }else{
                            setText(stu.getStudId());
                        }
                    }// End of update()
                };
                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if (isNowEmpty) {
                                cell.setContextMenu(null);
                            } else {
                                cell.setContextMenu(listContextMenu);
                            }
                        });
                return cell;
            }
        }); // End of setting the cell factory

        // Sorts studIDs according to year of study in ascending order

        SortedList<Student> sortedByYear = new SortedList<Student>(StudentData.getInstance().getStudents(), new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                return o1.getYearOfStudy().compareTo(o2.getYearOfStudy());
            }
        });
        studentListView.setItems(sortedByYear);
        studentListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        studentListView.getSelectionModel().selectFirst();
    } // End of initialise

    // Determines module choices
    public void getChoice(ActionEvent event) {

        if(event.getSource().equals(mod1Choice)){
            choice1 = mod1Choice.getSelectionModel().getSelectedItem();
        }if(event.getSource().equals(mod2Choice)){
            choice2 = mod2Choice.getSelectionModel().getSelectedItem();
        }if(event.getSource().equals(mod3Choice)){
            choice3 = mod3Choice.getSelectionModel().getSelectedItem();
        }
    }

    // To add studentData
    @FXML
    public void addStudentData() {

        String studIdS = studId.getText().trim();
        String yearStudyS = yearStudy.getText().trim();

        // Prevents adding of student details when essential boxes are empty

        if(studIdS.isEmpty() || yearStudyS.isEmpty())
        {
            validateStudent.setText("Error: cannot add student if studId or year of study not filled in");
        }if(studIdS != null && !studIdS.trim().isEmpty() && yearStudyS != null && !yearStudyS.trim().isEmpty()){
            studentToAdd = new Student(studIdS, yearStudyS, choice1, choice2, choice3);

            StudentData.getInstance().addStudentData(studentToAdd);
            validateStudent.setText("");

            studentListView.getSelectionModel().select(studentToAdd);
        }
    }

    // Deleting a student's details
    public void deleteStudent(Student stu) {

        Alert alert = new Alert((Alert.AlertType.CONFIRMATION));

        alert.setTitle("Delete a student from the list");
        alert.setHeaderText("Deleting student " + stu.getStudId());
        alert.setContentText("Are you sure you want to delete the student?");

        Optional<ButtonType> result = alert.showAndWait();

        if(result.isPresent() && result.get() == ButtonType.OK){
            StudentData.getInstance().deleteStudent(stu);
        }
    }

    // Editing a student
    public void editStudent(Student stu) {

        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.initOwner(mainWindow.getScene().getWindow());
        dialog.setTitle("Edit a student's details");
        dialog.setHeaderText("Editing student Id: " + stu.getStudId());

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("edit-students.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        }catch (IOException event) {
            System.out.println("Could not load the dialog");
            event.printStackTrace();
            return;
        }

        EditStudentController ec = fxmlLoader.getController();
        ec.setToEdit(stu);

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Student editStudent = ec.processEdit(stu);

            studentListView.getSelectionModel().select(editStudent);
        }
    }
}