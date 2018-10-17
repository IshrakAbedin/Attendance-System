import Data.StudentInformation;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class AdminMainWindow {
    private XEadmin dbmsAdminAccount;
    private String FontName;
    private Background ListViewBackground;
    private ObservableList<StudentInformation> StudentsList;
    private ObservableList<String> TeacherList;
    private ObservableList<String> SectionList;
    private ObservableList<String> CourseList;
    private Dialog<ButtonType> Dialog;
    private FXMLLoader fxmlLoader;
    private DialogPane dialogPane;
    private Background DialogBackground;
    private String DialogStyleCSS;


    @FXML private Button FX_B_Account;
    @FXML private Text FX_T_Date;
    @FXML private Text FX_T_TotalTeachers;
    @FXML private ComboBox<String> FX_CB_TeacherList;
    @FXML private ListView<String> FX_LV_CourseList;
    @FXML private ListView<String> FX_LV_SectionList;
    @FXML private ListView<StudentInformation> FX_LV_StudentList;
    @FXML private BorderPane FX_BorderPane_Admin;
    @FXML private Text FX_T_SName;
    @FXML private Text FX_T_SAddress;
    @FXML private Text FX_T_SContactNumber;


    public void setDbmsAdminAccount(XEadmin dbmsAdminAccount) {
        this.dbmsAdminAccount = dbmsAdminAccount;

        if (dbmsAdminAccount != null){
            setupAccountButton(dbmsAdminAccount.name, "Log Out");
            getTeacherInformation();
        }
    }

    /**
     * Initializes necessary variables and functions.
     */
    public void initialize(){
        TeacherList = FXCollections.observableArrayList();
        StudentsList = FXCollections.observableArrayList();
        SectionList = FXCollections.observableArrayList();
        CourseList = FXCollections.observableArrayList();
        FontName = "Arial";
        DialogStyleCSS = "dialog.css";
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        FX_T_Date.setText("Date : " + df.format(LocalDateTime.now()));
        ListViewBackground = new Background(
                new BackgroundFill(
                        Color.color(0.25,0.25,0.25,1),
                        CornerRadii.EMPTY,
                        Insets.EMPTY
                )
        );
        DialogBackground = new Background(
                new BackgroundFill(
                        Color.GRAY,
                        CornerRadii.EMPTY,
                        Insets.EMPTY
                )
        );
        Callback<ListView<String>, ListCell<String>> cellColor = new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                ListCell <String> cell = new ListCell<String>(){
                    @Override
                    protected void updateItem(String std, boolean empty) {
                        super.updateItem(std, empty);
                        setFont(Font.font(FontName, 16));
                        setTextFill(Color.color(0, 1, 0.86, 1));

                        if (empty){
                            setText("- - -");
                        } else {
                            setText(std);
                        }
                    }
                };

                // Setting cell background color
                cell.backgroundProperty().setValue(ListViewBackground);

                return cell;
            }
        };

        // Selection modes
        FX_LV_SectionList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        FX_LV_StudentList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        FX_LV_CourseList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Adding listeners
        FX_CB_TeacherList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                getCourseAndSectionInformation();
            }
        });
        FX_LV_StudentList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<StudentInformation>() {
            @Override
            public void changed(ObservableValue<? extends StudentInformation> observable, StudentInformation oldValue, StudentInformation newValue) {
                if (newValue != null){
                    StudentInformation sd = FX_LV_StudentList.getSelectionModel().getSelectedItem();
                    FX_T_SName.setText(sd.getName());
                    FX_T_SAddress.setText(sd.getAddress());
                    FX_T_SContactNumber.setText(sd.getContactNumber());
                }
            }
        });
        FX_LV_SectionList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println("New section is " + FX_LV_SectionList.getSelectionModel().getSelectedItem());
                getStudentInformation();
            }
        });

        // Setting cell factory
        FX_CB_TeacherList.setCellFactory(cellColor);
        FX_LV_CourseList.setCellFactory(cellColor);
        FX_LV_SectionList.setCellFactory(cellColor);
        FX_LV_StudentList.setCellFactory(new Callback<ListView<StudentInformation>, ListCell<StudentInformation>>() {
            @Override
            public ListCell<StudentInformation> call(ListView<StudentInformation> param) {
                ListCell <StudentInformation> cell = new ListCell<StudentInformation>(){
                    @Override
                    protected void updateItem(StudentInformation std, boolean empty) {
                        super.updateItem(std, empty);
                        if (empty)
                            setText(null);
                        else {
                            setText(std.getSID());
                            setFont(Font.font(FontName, 16));
                            setTextFill(Color.color(0, 1, 0.86, 1));
                        }
                    }
                };

                // Setting cell background color
                cell.backgroundProperty().setValue(ListViewBackground);

                return cell;
            }
        });

        // Setting background
        FX_CB_TeacherList.setBackground(ListViewBackground);
        FX_LV_CourseList.setBackground(ListViewBackground);
        FX_LV_SectionList.setBackground(ListViewBackground);

        // Binding values
        FX_CB_TeacherList.itemsProperty().setValue(TeacherList);
        FX_LV_CourseList.itemsProperty().setValue(CourseList);
        FX_LV_SectionList.itemsProperty().setValue(SectionList);
        FX_LV_StudentList.itemsProperty().setValue(StudentsList);
    }

    /**
     * Handles inserting a new teacher account.
     * Creates a new dialog to take necessary information.
     */
    @FXML private void handleInsertTeacherDialog(){
        if (dbmsAdminAccount == null){
            System.out.println("Returned from insert teacher because account is null!");
            return;
        }

        boolean creation = createDialog(
                "InsertTeacherDialog.fxml",
                "Insert New Teacher",
                "Use this dialog to insert a new teacher"
        );

        if (creation == true){
            System.out.println("Dialog has been created!");

            InsertTeacher insertTeacher = fxmlLoader.getController();
            Optional<ButtonType> result = Dialog.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK){
                InsertTeacher(
                        insertTeacher.getTeacherName(),
                        insertTeacher.getPassword()
                );
            }
        }
    }

    /**
     * Inserts teacher to dbms.
     * @param Teacher The name of the teacher account.
     * @param Password The password of the teacher account.
     */
    private void InsertTeacher(String Teacher, String Password){
        Task<Boolean> Task_InsertTeacher = new Task<Boolean>() {
            boolean InsertionStatus;

            @Override
            protected Boolean call() throws Exception {
                InsertionStatus = false;
                System.out.println(Teacher + " " + Password);
                InsertionStatus = dbmsAdminAccount.createTeacher(Teacher, Password);
                return InsertionStatus;
            }

            @Override
            protected void succeeded() {
                super.succeeded();

                if (InsertionStatus == true){
                    TeacherList.add(Teacher);
                    Integer Size = TeacherList.size();
                    FX_T_TotalTeachers.setText(Size.toString());

                    showWarning(
                            "Success",
                            "Teacher successfully created",
                            Teacher + " successfully created",
                            "INFORMATION"
                    );
                }
                else{
                    showWarning(
                            "Failed",
                            "Teacher creation failed",
                            Teacher + " couldn't be created",
                            "ERROR"
                    );
                }
            }
        };
        new Thread(Task_InsertTeacher).start();
    }


    /**
     * Handles deletion of a teacher account.
     * Deletes the teacher that is selected in the FX_CB_TeacherList
     */
    @FXML private void handleDeleteTeacherDialog(){
        if (dbmsAdminAccount == null){
            System.out.println("Returned from delete teacher because account is null!");
            return;
        }

        showWarning(
                "Confirm delete",
                "Confirm delete operation",
                "Do you want to delete " + FX_CB_TeacherList.getSelectionModel().getSelectedItem() + "?",
                "CONFIRMATION"
        );
    }


    /**
     * Deletes the selected teacher from dbms.
     */
    private void deleteTeacher(){
        Task<Boolean> Task_DeleteTeacher = new Task<>() {
            boolean DeleteStatus;
            String Teacher;

            @Override
            protected Boolean call() throws Exception {
                DeleteStatus = false;
                Teacher = FX_CB_TeacherList.getSelectionModel().getSelectedItem();
                DeleteStatus = dbmsAdminAccount.deleteTeacher(Teacher);
                return DeleteStatus;
            }

            @Override
            protected void succeeded() {
                super.succeeded();

                if (DeleteStatus == true){
                    System.out.println("Delete successful!");
                    TeacherList.remove(Teacher);
                    Integer Size = TeacherList.size();
                    FX_T_TotalTeachers.setText(Size.toString());

                    showWarning(
                            "Success",
                            "Teacher successfully deleted",
                            Teacher + " successfully deleted",
                            "INFORMATION"
                    );
                }
                else{
                    System.out.println("Delete failed!");

                    showWarning(
                            "Failed",
                            "Teacher deletion failed",
                            Teacher + " couldn't be deleted",
                            "ERROR"
                    );
                }
            }
        };
        new Thread(Task_DeleteTeacher).start();
    }


    @FXML private void handleSearchTeacher(){}


    @FXML private void handleInsertStudentDialog(){}


    @FXML private void handleDeleteStudentDialog(){}


    @FXML private void handleInsertSectionDialog(){
        if (dbmsAdminAccount == null){
            System.out.println("Returned from handle insert section!");
            return;
        }

        boolean dialogCreation = createDialog(
                "InsertSectionDialog.fxml",
                "Section Insertion",
                "Insert a section for a teacher"
        );

        if (dialogCreation == true){
            System.out.println("Dialog has been created!");

            InsertSection insertSection = fxmlLoader.getController();
            Optional<ButtonType> result = Dialog.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK){
                insertSection(insertSection.getTeacherName(), insertSection.getSectionName());
            }
        }
    }


    private void insertSection(String Teacher, String Section){
        Task <Boolean> Task_InsertSection = new Task<Boolean>() {
            boolean InsertionStatus;

            @Override
            protected Boolean call() throws Exception {
                InsertionStatus = false;
                InsertionStatus = dbmsAdminAccount.createSection(Teacher, Section);
                return InsertionStatus;
            }

            @Override
            protected void succeeded() {
                if (InsertionStatus == true){
                    SectionList.add(Section);

                    showWarning(
                            "Success",
                            "Section successfully created",
                            Section + " successfully created",
                            "INFORMATION"
                    );
                }
                else{
                    showWarning(
                            "Failed",
                            "Section creation failed",
                            Section + " couldn't be created",
                            "ERROR"
                    );
                }
            }
        };
        new Thread(Task_InsertSection).start();
    }


    @FXML private void handleDeleteSectionDialog(){}


    @FXML private void handleInsertCourseDialog(){}


    @FXML private void handleDeleteCourseDialog(){}


    @FXML private void handleExit(){
        Platform.exit();
    }


    @FXML private void handleLogOut(){
        dbmsAdminAccount.close();
        handleExit();
    }


    /**
     * Gets teacher information via another thread.
     * Saves information to TeacherList.
     */
    private void getTeacherInformation(){
        Task<ObservableList<String>> Task_Teachers = new Task<>() {
            final ObservableList<String> Teachers = FXCollections.observableArrayList();

            @Override
            protected ObservableList<String> call() throws Exception {
                try{
                    Teachers.clear();

                    ResultSet rs = dbmsAdminAccount.getTeacherList();
                    if (rs != null){
                        while(rs.next()){
                            Teachers.add(rs.getString("TNAME"));
                        }
                        rs.close();
                    }
                }
                catch (SQLException e){
                    e.printStackTrace();
                }

                return Teachers;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                System.out.println("Task_Teacher has completed!");
                Integer Size = Teachers.size();

                if (Size > 0) {
                    TeacherList.setAll(Teachers);
                    FX_T_TotalTeachers.setText(Size.toString());
                    FX_CB_TeacherList.getSelectionModel().selectFirst();
                }
            }
        };
        new Thread(Task_Teachers).start();
    }


    /**
     * Gets Student information via another thread.
     * Saves information to StudentList.
     */
    private void getStudentInformation(){
        Task<ObservableList<StudentInformation>> Task_StudentInfo = new Task<>() {
            final ObservableList<StudentInformation> studentList = FXCollections.observableArrayList();

            @Override
            protected ObservableList<StudentInformation> call() throws Exception {
                try{
                    String Teacher = FX_CB_TeacherList.getSelectionModel().getSelectedItem();
                    String Section = FX_LV_SectionList.getSelectionModel().getSelectedItem();
                    ResultSet rs = dbmsAdminAccount.getStudentListByTeacher(Teacher, Section);

                    if (rs != null){
                        while(rs.next()){
                            StudentInformation std = new StudentInformation(
                                    rs.getString("SID"),
                                    rs.getString("NAME"),
                                    rs.getString("ADDRESS"),
                                    rs.getString("CONTACT_NUMBER")
                            );

                            studentList.add(std);
                        }

                        rs.close();
                    }
                }
                catch(SQLException e){
                    e.printStackTrace();
                }

                return studentList;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                StudentsList.setAll(studentList);
            }
        };
        new Thread(Task_StudentInfo).start();
    }



    /**
     * Gets Course information via another thread.
     * Saves information to CourseLIst.
     */
    private void getCourseAndSectionInformation(){
        Task<Boolean> Task_CoursesAndSections = new Task<Boolean>() {
            final ObservableList<String> Courses = FXCollections.observableArrayList();
            final ObservableList<String> Sections = FXCollections.observableArrayList();
            Boolean Retrieve;

            @Override
            protected Boolean call() throws Exception {
                String SelectedTeacher = FX_CB_TeacherList.getSelectionModel().getSelectedItem();

                try {
                    ResultSet rs = dbmsAdminAccount.getSectionCourseByTeacherList(SelectedTeacher);
                    if (rs != null) {
                        while (rs.next()) {
                            Courses.add(rs.getString("CLASS"));
                            Sections.add(rs.getString("SECTION"));
                        }
                        rs.close();

                        Retrieve = true;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return Retrieve;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                System.out.println("Task_Courses has finished running!");

                if (Retrieve == true) {
                    CourseList.setAll(Courses);
                    SectionList.setAll(Sections);

                    if (CourseList.size() > 0)
                        FX_LV_CourseList.getSelectionModel().selectFirst();

                    if (SectionList.size() > 0)
                        FX_LV_SectionList.getSelectionModel().selectFirst();
                }
            }
        };
        new Thread(Task_CoursesAndSections).start();
    }


    /**
     * Setups the log in and out button text and tooltip text.
     * @param buttonText what the button text should be
     * @param buttonTooltip what the tooltip should say
     */
    private void setupAccountButton(String buttonText, String buttonTooltip){
        if (buttonText.length() > 0 && buttonTooltip.length() > 0) {
            FX_B_Account.setText(buttonText);
            FX_B_Account.getTooltip().setText(buttonTooltip);
        }
    }


    /**
     * Create the log in dialog.
     * @param DialogName Name of the dialog.
     * @param TitleText Title of the dialog.
     * @param HeaderText Header text of the dialog.
     * @return true if successfully created. False otherwise.
     */
    private boolean createDialog(String DialogName, String TitleText, String HeaderText){
        try{
            Dialog = new Dialog<>();
            fxmlLoader = new FXMLLoader();
            Dialog.initOwner(FX_BorderPane_Admin.getScene().getWindow());
            Dialog.setTitle(TitleText);
            Dialog.setHeaderText(HeaderText);
            dialogPane = Dialog.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource(DialogStyleCSS).toExternalForm());
            fxmlLoader.setLocation(getClass().getResource(DialogName));
            Dialog.getDialogPane().setContent(fxmlLoader.load());
            Dialog.getDialogPane().setBackground(DialogBackground);
            Dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            Dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        } catch(IOException e){
            System.out.println("Couldn't load dialogue " + DialogName);
            e.printStackTrace();
            return false;
        }

        return true;
    }


    /**
     * Displays an error message when trying to use the software without logging in.
     * @param TitleText The title of the warning
     * @param HeaderText the header of the warning
     * @param ContentText the context of the warning
     * @param Type Type of issue generating the command.
     */
    private void showWarning(String TitleText, String HeaderText, String ContentText, String Type){
        String type = Type.toUpperCase();

        Alert alert = null;
        if (type.equals("ERROR")){
            alert = new Alert(Alert.AlertType.ERROR);
        } else if (type.equals("CONFIRMATION")){
            alert = new Alert(Alert.AlertType.CONFIRMATION);
        } else if (type.equals("WARNING")){
            alert = new Alert(Alert.AlertType.WARNING);
        } else if (type.equals("INFORMATION")){
            alert = new Alert(Alert.AlertType.INFORMATION);
        }

        alert.setTitle(TitleText);
        alert.setHeaderText(HeaderText);
        alert.setContentText(ContentText);
        dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource(DialogStyleCSS).toExternalForm());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            if (TitleText.equals("Confirm delete")){
                deleteTeacher();
            }
        }
    }
}
