import Data.StudentInformation;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class AdminMainWindow {
    private XEadmin dbmsAdminAccount;
    private String FontName;
    private ObservableList<StudentInformation> StudentsList;
    private ObservableList<String> TeacherList;
    private ObservableList<String> SectionList;
    private ObservableList<String> CourseList;


    @FXML private Button FX_B_Account;
    @FXML private Text FX_T_Date;
    @FXML private Text FX_T_TotalTeachers;
    @FXML private Text FX_T_TotalSections;
    @FXML private Text FX_T_TotalCourses;
    @FXML private Text FX_T_TotalStudents;
    @FXML private ComboBox<String> FX_CB_TeacherList;
    @FXML private ListView<String> FX_LV_CourseList;
    @FXML private ListView<String> FX_LV_SectionList;
    @FXML private ListView<StudentInformation> FX_LV_StudentList;
    @FXML private BorderPane FX_BorderPane_Admin;
    @FXML private Text FX_T_SName;
    @FXML private Text FX_T_SAddress;
    @FXML private Text FX_T_SContactNumber;


    /**
     * Sets text colors for information displaying.
     * @param TextColor the color to show.
     */
    private void setTextColor(Color TextColor){
        FX_T_SName.setFill(TextColor);
        FX_T_SAddress.setFill(TextColor);
        FX_T_SContactNumber.setFill(TextColor);
        FX_T_Date.setFill(TextColor);
        FX_T_TotalTeachers.setFill(TextColor);
        FX_T_TotalSections.setFill(TextColor);
        FX_T_TotalCourses.setFill(TextColor);
        FX_T_TotalStudents.setFill(TextColor);
    }


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
        setTextColor(Color.color(1,1,1,1));
        TeacherList = FXCollections.observableArrayList();
        StudentsList = FXCollections.observableArrayList();
        SectionList = FXCollections.observableArrayList();
        CourseList = FXCollections.observableArrayList();
        FontName = "Arial";
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        FX_T_Date.setText("Date : " + df.format(LocalDateTime.now()));
        Callback<ListView<String>, ListCell<String>> cellColor = DrawWindows.getInstance().cellColor;

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
                cell.backgroundProperty().setValue(DrawWindows.getInstance().ListViewBackground);

                return cell;
            }
        });

        // Setting background
        FX_CB_TeacherList.setBackground(DrawWindows.getInstance().ListViewBackground);
        FX_LV_CourseList.setBackground(DrawWindows.getInstance().ListViewBackground);
        FX_LV_SectionList.setBackground(DrawWindows.getInstance().ListViewBackground);

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

        boolean creation = DrawWindows.getInstance().DrawDialog(
                "InsertTeacherDialog.fxml",
                "Insert New Teacher",
                "Use this dialog to insert a new teacher",
                FX_BorderPane_Admin
        );

        if (creation == true){
            System.out.println("Dialog has been created!");

            InsertTeacherDialog insertTeacherDialog = DrawWindows.getInstance().getFxmlLoader().getController();
            Optional<ButtonType> result = DrawWindows.getInstance().getDialog().showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK){
                String Teacher = insertTeacherDialog.getTeacherName();
                String Password = insertTeacherDialog.getPassword();

                if (TeacherList.indexOf(Teacher) == -1) {
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

                            if (InsertionStatus == true) {
                                TeacherList.add(Teacher);
                                Integer Size = TeacherList.size();
                                FX_T_TotalTeachers.setText(Size.toString());

                                DrawWindows.getInstance().DrawAlert(
                                        "Success",
                                        "Teacher successfully created",
                                        Teacher + " successfully created",
                                        "INFORMATION"
                                );
                            } else {
                                DrawWindows.getInstance().DrawAlert(
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
            }
        }
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

        DrawWindows.getInstance().DrawAlert(
                "Confirm delete",
                "Confirm delete operation",
                "Do you want to delete " + FX_CB_TeacherList.getSelectionModel().getSelectedItem() + "?",
                "CONFIRMATION"
        );

        Optional<ButtonType> result = DrawWindows.getInstance().getAlertResult();
        if (result.isPresent() && result.get() == ButtonType.OK){
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

                        DrawWindows.getInstance().DrawAlert(
                                "Success",
                                "Teacher successfully deleted",
                                Teacher + " successfully deleted",
                                "INFORMATION"
                        );
                    }
                    else{
                        System.out.println("Delete failed!");

                        DrawWindows.getInstance().DrawAlert(
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
    }


    /**
     * Handles insertion of a new student.
     */
    @FXML private void handleInsertStudentDialog(){
        if (dbmsAdminAccount == null){
            System.out.println("Returned from handle insert section!");
            return;
        }

        boolean dialogCreation = DrawWindows.getInstance().DrawDialog(
                "InsertStudentDialog.fxml",
                "Student Insertion",
                "Insert a new student",
                FX_BorderPane_Admin
        );

        if (dialogCreation == true){
            InsertStudentDialog insertStudentDialog = DrawWindows.getInstance().getFxmlLoader().getController();
            insertStudentDialog.setSectionName(FX_LV_SectionList.getSelectionModel().getSelectedItem());
            insertStudentDialog.setTeacherName(FX_CB_TeacherList.getSelectionModel().getSelectedItem());

            Optional<ButtonType> result = DrawWindows.getInstance().getDialog().showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK){
                String SID = insertStudentDialog.getStudentID();
                String Name = insertStudentDialog.getStudentName();
                String Address = insertStudentDialog.getStudentAddress();
                String ContactNo = insertStudentDialog.getStudentContact();
                String Teacher = insertStudentDialog.getTeacherName();
                String Section = insertStudentDialog.getSectionName();

                Task <Boolean> Task_InsertStudent = new Task<Boolean>() {
                    boolean InsertionStatus;

                    @Override
                    protected Boolean call() throws Exception {
                        InsertionStatus = false;
                        InsertionStatus = dbmsAdminAccount.insertStudent(Teacher, Section, SID, Name, Address, ContactNo);
                        return InsertionStatus;
                    }

                    @Override
                    protected void succeeded() {
                        if (InsertionStatus == true){
                            DrawWindows.getInstance().DrawAlert(
                                    "Success",
                                    "Student successfully inserted",
                                    SID + " successfully inserted",
                                    "INFORMATION"
                            );
                        }
                        else{
                            DrawWindows.getInstance().DrawAlert(
                                    "Failed",
                                    "Student creation failed",
                                    SID + " couldn't be created",
                                    "ERROR"
                            );
                        }
                    }
                };
                new Thread(Task_InsertStudent).start();
            }
        }
    }


    /**
     * Handles deletion of a student
     */
    @FXML private void handleDeleteStudentDialog(){
        if (dbmsAdminAccount == null){
            System.out.println("Returned from delete teacher because account is null!");
            return;
        }

        String SID = FX_LV_StudentList.getSelectionModel().getSelectedItem().getSID();
        String Section = FX_LV_SectionList.getSelectionModel().getSelectedItem();
        String Teacher = FX_CB_TeacherList.getSelectionModel().getSelectedItem();

        DrawWindows.getInstance().DrawAlert(
                "Confirm delete",
                "Confirm delete operation",
                "Do you want to delete " + SID + " from " + Section  + "?",
                "CONFIRMATION"
        );

        Optional<ButtonType> result = DrawWindows.getInstance().getAlertResult();
        if (result.isPresent() && result.get() == ButtonType.OK){
            Task<Boolean> Task_DeleteStudent = new Task<>() {
                boolean DeleteStatus;

                @Override
                protected Boolean call() throws Exception {
                    DeleteStatus = false;
                    DeleteStatus = dbmsAdminAccount.deleteStudent(Teacher, Section, SID);
                    return DeleteStatus;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();

                    if (DeleteStatus == true){
                        DrawWindows.getInstance().DrawAlert(
                                "Success",
                                "Student successfully deleted",
                                SID + " successfully deleted",
                                "INFORMATION"
                        );
                        getStudentInformation();
                    }
                    else{
                        System.out.println("Delete failed!");
                        DrawWindows.getInstance().DrawAlert(
                                "Failed",
                                "Student deletion failed",
                                SID + " couldn't be deleted",
                                "ERROR"
                        );
                    }
                }
            };
            new Thread(Task_DeleteStudent).start();
        }
    }


    /**
     * Handles insert section task.
     */
    @FXML private void handleInsertSectionDialog(){
        if (dbmsAdminAccount == null){
            System.out.println("Returned from handle insert section!");
            return;
        }

        boolean dialogCreation = DrawWindows.getInstance().DrawDialog(
                "InsertSectionDialog.fxml",
                "Section Insertion",
                "Insert a section for a teacher",
                FX_BorderPane_Admin
        );

        if (dialogCreation == true){
            System.out.println("Dialog has been created!");

            InsertSectionDialog insertSectionDialog = DrawWindows.getInstance().getFxmlLoader().getController();
            insertSectionDialog.setTeacherName(FX_CB_TeacherList.getSelectionModel().getSelectedItem());
            Optional<ButtonType> result = DrawWindows.getInstance().getDialog().showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK){
                String Teacher = insertSectionDialog.getTeacherName();
                String Section = insertSectionDialog.getSectionName();

                if (TeacherList.indexOf(Teacher) == -1){
                    DrawWindows.getInstance().DrawAlert(
                            "Error inserting section",
                            "Teacher not found",
                            Teacher + " doesn't exist!",
                            "ERROR"
                    );
                    return;
                }
                else if (SectionList.indexOf(Section) == -1){
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

                                DrawWindows.getInstance().DrawAlert(
                                        "Success",
                                        "Section successfully created",
                                        Section + " successfully created",
                                        "INFORMATION"
                                );
                            }
                            else{
                                DrawWindows.getInstance().DrawAlert(
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
            }
        }
    }


    /**
     * Handles delete section task.
     */
    @FXML private void handleDeleteSectionDialog(){
        if (dbmsAdminAccount == null){
            System.out.println("Returned from delete teacher because account is null!");
            return;
        }

        DrawWindows.getInstance().DrawAlert(
                "Confirm delete",
                "Confirm delete operation",
                "Do you want to delete " + FX_LV_SectionList.getSelectionModel().getSelectedItem() + "?",
                "CONFIRMATION"
        );

        Optional<ButtonType> result = DrawWindows.getInstance().getAlertResult();
        if (result.isPresent() && result.get() == ButtonType.OK){
            Task<Boolean> Task_DeleteSection = new Task<>() {
                boolean DeleteStatus;
                String Teacher;
                String Section;

                @Override
                protected Boolean call() throws Exception {
                    DeleteStatus = false;
                    Teacher = FX_CB_TeacherList.getSelectionModel().getSelectedItem();
                    Section = FX_LV_SectionList.getSelectionModel().getSelectedItem();
                    DeleteStatus = dbmsAdminAccount.deleteSection(Teacher, Section);
                    return DeleteStatus;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();

                    if (DeleteStatus == true){
                        System.out.println("Delete successful!");
                        SectionList.remove(Section);

                        DrawWindows.getInstance().DrawAlert(
                                "Success",
                                "Section successfully deleted",
                                Section + " successfully deleted",
                                "INFORMATION"
                        );
                    }
                    else{
                        System.out.println("Delete failed!");

                        DrawWindows.getInstance().DrawAlert(
                                "Failed",
                                "Section deletion failed",
                                Section + " couldn't be deleted",
                                "ERROR"
                        );
                    }
                }
            };
            new Thread(Task_DeleteSection).start();
        }
    }


    /**
     * Handles insert course task.
     */
    @FXML private void handleInsertCourseDialog(){
        if (dbmsAdminAccount == null){
            System.out.println("Returned from handle insert section!");
            return;
        }

        boolean dialogCreation = DrawWindows.getInstance().DrawDialog(
                "InsertCourseDialog.fxml",
                "Course Insertion",
                "Insert a Course for a teacher and section",
                FX_BorderPane_Admin
        );

        if (dialogCreation == true){
            System.out.println("Dialog has been created!");

            InsertCourseDialog insertCourseDialog = DrawWindows.getInstance().getFxmlLoader().getController();
            insertCourseDialog.setTeacherName(FX_CB_TeacherList.getSelectionModel().getSelectedItem());
            insertCourseDialog.setSectionName(FX_LV_SectionList.getSelectionModel().getSelectedItem());

            Optional<ButtonType> result = DrawWindows.getInstance().getDialog().showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK){
                String Teacher = insertCourseDialog.getTeacherName();
                String Section = insertCourseDialog.getSectionName();
                String Course = insertCourseDialog.getCourseName();

                if (TeacherList.indexOf(Teacher) == -1){
                    DrawWindows.getInstance().DrawAlert(
                            "Error inserting course",
                            "Teacher not found",
                            Teacher + " doesn't exist!",
                            "ERROR"
                    );
                    return;
                }
                else if (SectionList.indexOf(Section) == -1){
                    DrawWindows.getInstance().DrawAlert(
                            "Error inserting course",
                            "Section not found",
                            Section + " doesn't exist!",
                            "ERROR"
                    );
                    return;
                }
                else if (CourseList.indexOf(Course) == -1) {
                    Task <Boolean> Task_InsertCourse = new Task<Boolean>() {
                        boolean InsertionStatus;

                        @Override
                        protected Boolean call() throws Exception {
                            InsertionStatus = false;
                            InsertionStatus = dbmsAdminAccount.createClass(Teacher, Section, Course);
                            return InsertionStatus;
                        }

                        @Override
                        protected void succeeded() {
                            if (InsertionStatus == true){
                                CourseList.add(Course);

                                DrawWindows.getInstance().DrawAlert(
                                        "Success",
                                        "Course successfully created",
                                        Course + " successfully created",
                                        "INFORMATION"
                                );
                            }
                            else{
                                DrawWindows.getInstance().DrawAlert(
                                        "Failed",
                                        "Course creation failed",
                                        Course + " couldn't be created",
                                        "ERROR"
                                );
                            }
                        }
                    };
                    new Thread(Task_InsertCourse).start();
                }
            }
        }
    }


    /**
     * Handles delete course task.
     */
    @FXML private void handleDeleteCourseDialog(){
        if (dbmsAdminAccount == null){
            System.out.println("Returned from delete teacher because account is null!");
            return;
        }

        DrawWindows.getInstance().DrawAlert(
                "Confirm delete",
                "Confirm delete operation",
                "Do you want to delete " + FX_LV_CourseList.getSelectionModel().getSelectedItem() + "?",
                "CONFIRMATION"
        );

        Optional<ButtonType> result = DrawWindows.getInstance().getAlertResult();
        if (result.isPresent() && result.get() == ButtonType.OK){
            Task<Boolean> Task_DeleteCourse = new Task<>() {
                boolean DeleteStatus;
                String Teacher;
                String Section;
                String Course;

                @Override
                protected Boolean call() throws Exception {
                    DeleteStatus = false;
                    Teacher = FX_CB_TeacherList.getSelectionModel().getSelectedItem();
                    Section = FX_LV_SectionList.getSelectionModel().getSelectedItem();
                    Course = FX_LV_CourseList.getSelectionModel().getSelectedItem();

                    if (Teacher != null && Section != null && Course != null)
                        DeleteStatus = dbmsAdminAccount.deleteClass(Teacher, Section, Course);

                    return DeleteStatus;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();

                    if (DeleteStatus == true){
                        System.out.println("Delete successful!");
                        CourseList.remove(Course);

                        DrawWindows.getInstance().DrawAlert(
                                "Success",
                                "Course successfully deleted",
                                Course + " successfully deleted",
                                "INFORMATION"
                        );
                    }
                    else{
                        System.out.println("Delete failed!");

                        DrawWindows.getInstance().DrawAlert(
                                "Failed",
                                "Course deletion failed",
                                Course + " couldn't be deleted",
                                "ERROR"
                        );
                    }
                }
            };
            new Thread(Task_DeleteCourse).start();
        }
    }


    /**
     * Handles the task of exiting the program.
     */
    @FXML private void handleExit(){
        DrawWindows.getInstance().DrawAlert(
                "Confirm Exit",
                "Do you want to exit?",
                "Please confirm if you want to exit the application",
                "CONFIRMATION"
        );

        Optional<ButtonType> result = DrawWindows.getInstance().getAlertResult();
        if (result.isPresent() && result.get() == ButtonType.OK){
            Platform.exit();
        }
    }

    /**
     * Handles log out process.
     */
    @FXML private void handleLogOut(){
        DrawWindows.getInstance().DrawAlert(
                "Confirm Log out",
                "Do you want to Log out?",
                "Please confirm if you want to Log out of " + dbmsAdminAccount.name,
                "CONFIRMATION"
        );

        Optional<ButtonType> result = DrawWindows.getInstance().getAlertResult();
        if (result.isPresent() && result.get() == ButtonType.OK){
            dbmsAdminAccount.close();
            DrawWindows.getInstance().CloseAdminMainStage();
        }
    }



    @FXML private void handleShowDevInfo(){
        boolean creation = DrawWindows.getInstance().DrawDialog(
                "DevInfoDialog.fxml",
                "Developer Information Dialog",
                "Team Information",
                FX_BorderPane_Admin
        );

        if (creation){
            Optional<ButtonType> result = DrawWindows.getInstance().getDialog().showAndWait();
        }
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

                TeacherList.setAll(Teachers);
                FX_T_TotalTeachers.setText(Size.toString());

                if (Size > 0) {
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

                FX_T_TotalStudents.setText(((Integer)StudentsList.size()).toString());

                if (StudentsList.size() > 0){
                    FX_LV_StudentList.getSelectionModel().selectFirst();
                }
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
            Boolean Retrieve = false;

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

                    FX_T_TotalCourses.setText(((Integer)CourseList.size()).toString());
                    FX_T_TotalSections.setText(((Integer)SectionList.size()).toString());
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
}
