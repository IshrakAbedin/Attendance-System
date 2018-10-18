import Data.StudentInformation;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import javafx.collections.ObservableList;
import javafx.util.Callback;

public class TeacherMainWindow {
    private XEbase dbmsUserAccount;
    private ObservableList<StudentInformation> StudentInfoList;
    private ObservableList<String> SIDList;
    private ObservableList<String> ClassDayList;
    private ObservableList<String> CourseList;
    private ObservableList<String> SectionList;
    private ObservableList<String> PresentDayList;
    private Predicate<StudentInformation> DefaulterStudents;
    private Predicate<StudentInformation> AllStudents;
    private FilteredList<StudentInformation> filteredList;
    private String TeacherAccountName;
    private String TeacherAccountPassword;
    private String FontName;
    private ContextMenu FX_CM_list;
    private DateTimeFormatter dateTimeFormatter;

    /*
     * Necessary variables to maintain connections.
     * FX_BorderPane_Teacher        -       The main border pane for teacher.
     * FX_B_Account                 -       Account button for log in and out.
     * FX_B_Search                  -       Button for searching student information.
     * FX_T_Date                    -       To show the current date.
     * FX_LV_StudentInfo            -       List to show the SID list of students in a course.
     * FX_T_SName                   -       Text to show student name.
     * FX_T_SAddress                -       Text to show student Address.
     * FX_T_SContactNumber          -       Text to show student contact info.
     * FX_T_SAttendanceCount        -       Text to show student attendance count.
     * FX_T_SAttendancePercentage   -       Text to show student attendance percentage.
     * FX_T_SAttendanceRemarks      -       Text to show student attendance remarks.
     * FX_T_TotalStudents           -       Text to show total students in a course.
     * FX_T_TotalLectureCount       -       Text to show total lecture count of a course.
     * FX_T_TotalDefaulterStudents  -       Text to show total defaulters of a course.
     * FX_TB_FilterDefaulter        -       A toggle button for filtering defaulters.
     * FX_LV_PresentDays            -       Shows which days a student was present.
     * FX_LV_ClassDayList              -       Shows which days a lecture was held.
     * FX_CB_CourseList             -       Shows which courses the teacher takes.
     */
    @FXML private BorderPane FX_BorderPane_Teacher;
    @FXML private Button FX_B_Account;
    @FXML private ToggleButton FX_TB_FilterDefaulter;
    @FXML private ListView<StudentInformation> FX_LV_StudentInfo;
    @FXML private ListView<String> FX_LV_PresentDays;
    @FXML private ListView<String> FX_LV_ClassDayList;
    @FXML private Text FX_T_Date;
    @FXML private Text FX_T_SName;
    @FXML private Text FX_T_SAddress;
    @FXML private Text FX_T_SContactNumber;
    @FXML private Text FX_T_TotalStudents;
    @FXML private Text FX_T_SAttendanceCount;
    @FXML private Text FX_T_SAttendancePercentage;
    @FXML private Text FX_T_SAttendanceRemarks;
    @FXML private Text FX_T_TotalLectureCount;
    @FXML private Text FX_T_TotalDefaulterStudents;
    @FXML private ComboBox<String> FX_CB_CourseList;

    /**
     * Sets up the dbmsAccount
     * @param dbmsUserAccount
     */
    public void setDbmsUserAccount(XEbase dbmsUserAccount) {
        this.dbmsUserAccount = dbmsUserAccount;

        if (dbmsUserAccount != null) {
            getAccountInfo();
            setupAccountButton(TeacherAccountName, "Press to log out");
            FX_LV_StudentInfo.getSelectionModel().selectFirst();
        }
        else {
            setupAccountButton("Log In", "Press to log in");
        }
    }

    /**
     * Runs when first launching the application.
     */
    public void initialize(){
        /*
         * Setup necessary variables for software.
         *
         * DialogStyleCSS       - the dialog file name
         * FontName             - The default font for everything that will be viewed in the app.
         * AccountData          - Teacher information is kept in this temporary variable.
         * df                   - Time Date formatter.
         * FX_T_Date            - the current day is viewed here
         *
         * <Predicates are for filtering on and off>
         * AllStudents          - is used to view all students.
         * DefaulterStudents    - is used to view only defaulter students.
         *
         * ListViewBackground   - Used for background color of list views.
         * DialogBackground     - Used for background color of dialog boxes.
         * CellColor            - used for setting cell color.
         */
        FontName = "Arial";
        StudentInfoList = FXCollections.observableArrayList();
        ClassDayList = FXCollections.observableArrayList();
        CourseList = FXCollections.observableArrayList();
        SectionList = FXCollections.observableArrayList();
        SIDList = FXCollections.observableArrayList();
        PresentDayList = FXCollections.observableArrayList();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        FX_T_Date.setText("Date : " + df.format(LocalDateTime.now()));
        AllStudents = new Predicate<StudentInformation>() {
            @Override
            public boolean test(StudentInformation stdInfo) {
                return true;
            }
        };
        DefaulterStudents = new Predicate<StudentInformation>() {
            @Override
            public boolean test(StudentInformation stdInfo) {
                return (stdInfo.getAttendancePercentage() < 75);
            }
        };
        Callback<ListView<String>, ListCell<String>> cellColor = DrawMainStage.getInstance().cellColor;
        dateTimeFormatter = DateTimeFormatter.ofPattern("MMMdd");

        /*
         * Calling necessary functions for running software.
         * Does initial setup of structure.
         *
         * SetTextColor - Sets the default color of the information viewing texts.
         * SetupContextMenu - Creates and attaches the context menu.
         */
        setTextColor(Color.color(1,1,1,1));
        setupContextMenu();

        FX_LV_StudentInfo.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        FX_LV_ClassDayList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        FX_LV_PresentDays.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Change listener
        FX_LV_StudentInfo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<StudentInformation>() {
            @Override
            public void changed(ObservableValue<? extends StudentInformation> observable, StudentInformation oldValue, StudentInformation newValue) {
                if (newValue != null){
                    StudentInformation sd = FX_LV_StudentInfo.getSelectionModel().getSelectedItem();
                    FX_T_SName.setText(sd.getName());
                    FX_T_SAddress.setText(sd.getAddress());
                    FX_T_SContactNumber.setText(sd.getContactNumber());
                    FX_T_SAttendanceCount.setText(sd.getAttendanceCount().toString());
                    FX_T_SAttendancePercentage.setText(sd.getAttendancePercentage().toString());
                    if (sd.getAttendancePercentage() >= 75){
                        FX_T_SAttendanceRemarks.setText("Regular");
                        FX_T_SAttendanceRemarks.setFill(Color.color(0, 0.86, 0.86, 1));
                        FX_T_SAttendanceRemarks.setFont(Font.font(FontName, null, null, 20));
                    }
                    else{
                        FX_T_SAttendanceRemarks.setText("Defaulter");
                        FX_T_SAttendanceRemarks.setFill(Color.color(0.86, 0, 0, 1));
                        FX_T_SAttendanceRemarks.setFont(Font.font(FontName, null, null, 20));
                    }
                    FX_LV_PresentDays.setItems(sd.getPresentDayList());
                }
            }
        });
        FX_LV_ClassDayList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                getStudentInformationList();
            }
        });
        FX_CB_CourseList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // Get selected class and section info
                String CourseName = FX_CB_CourseList.getSelectionModel().getSelectedItem();
                int index = CourseList.indexOf(CourseName);

                if (index != -1) {
                    String SectionName = SectionList.get(index);

                    // Set new class and section name in dbms.
                    dbmsUserAccount.setClassname(CourseName);
                    dbmsUserAccount.setSectionname(SectionName);

                    // Retrieve new class and section information
                    getClassInformation();
                }
            }
        });

        // Setting cell color
        FX_LV_ClassDayList.setCellFactory(cellColor);
        FX_LV_PresentDays.setCellFactory(cellColor);
        FX_CB_CourseList.setCellFactory(cellColor);
        FX_LV_StudentInfo.setCellFactory(new Callback<ListView<StudentInformation>, ListCell<StudentInformation>>() {
            @Override
            public ListCell<StudentInformation> call(ListView<StudentInformation> param) {
                ListCell <StudentInformation> cell = new ListCell<StudentInformation>(){
                    @Override
                    protected void updateItem(StudentInformation std, boolean empty) {
                        super.updateItem(std, empty);
                        if (empty){
                            setText(null);
                        } else {
                            setText(std.getSID());
                            setFont(Font.font(FontName, 16));

                            if (std.getAttendancePercentage() >= 75){
                                setTextFill(Color.color(0, 1, 0.86, 1));
                            } else if (std.getAttendancePercentage() < 75){
                                setTextFill(Color.RED);
                            }

                        }
                    }
                };

                // Setting cell background color
                cell.backgroundProperty().setValue(DrawMainStage.getInstance().ListViewBackground);

                // Attaching context menu to respond to only cells
                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if (isNowEmpty)
                                cell.setContextMenu(null);
                            else
                                cell.setContextMenu(FX_CM_list);
                        }
                );
                return cell;
            }
        });

        // Binding values to FX variables
        FX_LV_ClassDayList.itemsProperty().setValue(ClassDayList);
        FX_CB_CourseList.itemsProperty().setValue(CourseList);
        FX_LV_PresentDays.itemsProperty().setValue(PresentDayList);
    }


    /**
     * Creates a dialogue through which you log in to your account.
     */
    @FXML private void handleLogOut(){
        DrawWindows drawWindows = new DrawWindows();
        drawWindows.DrawAlert(
                "Confirm Log out",
                "Do you want to Log out?",
                "Please confirm if you want to Log out of " + dbmsUserAccount.name,
                "CONFIRMATION"
        );

        Optional<ButtonType> result = drawWindows.getAlertResult();
        if (result.isPresent() && result.get() == ButtonType.OK){
            dbmsUserAccount.close();
            DrawMainStage.getInstance().CloseTeacherMainStage();
        }
    }


    /**
     * Handles key press events
     * @param k is the event when a keyboard button is pressed
     */
    @FXML private void handleKeyPressed(KeyEvent k){
        if (dbmsUserAccount == null){
            return;
        }

        if (k.getCode().equals(KeyCode.DELETE)){
            handleDelete();
        } else if (k.getCode().equals(KeyCode.INSERT)){
            handleInsertDialog();
        } else if (k.getCode().equals(KeyCode.ENTER)){
            handleInsertDialog();
        } else if (k.getCode().equals(KeyCode.ESCAPE)){
            handleExit();
        }
    }


    /**
     * Closes connection and exits application.
     */
    @FXML private void handleExit(){
        DrawWindows drawWindows = new DrawWindows();
        drawWindows.DrawAlert(
                "Exit warning",
                "Are you sure you want to exit?",
                "Press OK to exit, otherwise press cancel",
                "CONFIRMATION"
        );

        Optional<ButtonType> result = drawWindows.getAlertResult();
        if (result.isPresent() && result.equals(ButtonType.OK)){
            Platform.exit();
        }
    }


    /**
     * Handles the code for editing task details.
     * Creates a Dialog to display old information
     * and runs the code to save the new information from the Dialog.
     */
    @FXML private void handleInsertDialog(){
        if (dbmsUserAccount == null){
            System.out.println("Showing from insert");
            return;
        }

        modify("Insert");
    }

    /**
     * handles the code for deleting a task.
     * Creates a confirmation Dialog and then proceeds to delete with consent.
     */
    @FXML private void handleDelete(){
        if (dbmsUserAccount == null){
            System.out.println("Showing from delete");
            return;
        }

        modify("Delete");
    }


    /**
     * handles the filtering mechanism.
     */
    @FXML private void handleFilter(){
        if (dbmsUserAccount == null){
            System.out.println("Showing from filter");
            return;
        }

        StudentInformation std = FX_LV_StudentInfo.getSelectionModel().getSelectedItem();

        if (FX_TB_FilterDefaulter.isSelected()){
            System.out.println("FILTER ON!");
            filteredList.setPredicate(DefaulterStudents);

            if (filteredList.isEmpty()){
                FX_T_SName.setText("");
                FX_T_SAddress.setText("");
                FX_T_SContactNumber.setText("");
                FX_T_SAttendanceCount.setText("");
                FX_T_SAttendancePercentage.setText("");
            }
            else if (filteredList.contains(std)){
                FX_LV_StudentInfo.getSelectionModel().select(std);
            }
            else {
                FX_LV_StudentInfo.getSelectionModel().selectFirst();
            }
        } else {
            System.out.println("FILTER OFF!");
            filteredList.setPredicate(AllStudents);
            FX_LV_StudentInfo.getSelectionModel().selectFirst();
        }
    }


    /**
     * Handles searching for student info
     */
    @FXML private void handleSearch(){
        if (dbmsUserAccount == null){
            System.out.println("Showing from search");
            return;
        }

        String DialogName = "SearchDialog.fxml";
        String TitleText = "Search for student";
        String HeaderText = "Enter student ID and section to begin searching";
        DrawWindows drawWindows = new DrawWindows();

        if (drawWindows.DrawDialog(DialogName, TitleText, HeaderText, FX_BorderPane_Teacher)){
            System.out.println("Created!");
            SearchDialog controller = drawWindows.getFxmlLoader().getController();
            String CourseName = FX_CB_CourseList.getSelectionModel().getSelectedItem();
            int CourseIndex = CourseList.indexOf(CourseName);
            String SectionName = SectionList.get(CourseIndex);
            controller.process(dbmsUserAccount);

            Optional<ButtonType> result = drawWindows.getDialog().showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                drawWindows.getDialog().close();
                dbmsUserAccount.setUsername(SectionName);
            }
        } else {
            System.out.println("NOT CREATED!");
        }
    }

    /**
     * Handles attendance taking.
     * Temporary method.
     */
    @FXML void handleTakeAttendance(){
        if (dbmsUserAccount == null){
            System.out.println("FROM Take attendance");
            return;
        }

        DrawWindows drawWindows = new DrawWindows();
        boolean dialogCreation = drawWindows.DrawDialog(
                "TakeAttendance.fxml",
                "Take attendance",
                "Move the present students to the present list to give them attendance",
                FX_BorderPane_Teacher
        );

        if (dialogCreation){
            TakeAttendance takeAttendance = drawWindows.getFxmlLoader().getController();
            takeAttendance.setFX_LV_AbsentStudents(SIDList);
            Optional<ButtonType> result = drawWindows.getDialog().showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                TakeAttendance(takeAttendance.getFX_LV_PresentStudents());
            }
        }
    }


    /**
     * Inserts attendance for all the students in PresentStudents list.
     * @param PresentStudents A list of students who were present.
     */
    private void TakeAttendance(ObservableList<String> PresentStudents){
        Task<Boolean> Task_TakeAttendance = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                int state = 0;

                for(int i = 0; i < PresentStudents.size(); ++i){
                    String date = dateTimeFormatter.format(LocalDate.now());
                    date = date.toUpperCase();
                    state = dbmsUserAccount.insertAttendance(PresentStudents.get(i), date);

                    if (state == -1){
                        System.out.println("Failed for " + PresentStudents.get(i));
                    }
                }

                return null;
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                getClassInformation();
            }
        };
        new Thread(Task_TakeAttendance).start();
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
     * Method copies the student information retrieved from the Database for displaying.
     * @return true if information successfully copied without error. False otherwise.
     */
    private boolean getStudentInformationList(){
        // Check if the dbmsUserAccount is valid. Meaning if a log in was successful
        if (dbmsUserAccount == null){
            System.out.println("Showing from getStudentInformation");
            return false;
        }

        /*
         * Get the student information (SID, Name, Address, Contact no) from the dbms.
         * If Base information isn't accessible then none of the other portions will work.
         */
        Task<Boolean> Task_GetStudentInformation = new Task<Boolean>() {
            ObservableList<StudentInformation> Students = FXCollections.observableArrayList();
            Boolean Succeeded = false;

            @Override
            protected Boolean call() throws Exception {
                try{
                    String day = FX_LV_ClassDayList.getSelectionModel().getSelectedItem();
                    ResultSet rs = dbmsUserAccount.getAttendanceByDayList(day);

                    if (rs != null){
                        while(rs.next())
                            Students.add(new StudentInformation(rs.getString(1)));
                        rs.close();
                    }

                    for(int i = 0; i < Students.size(); ++i){
                        rs = dbmsUserAccount.getExtendedAttendanceBySIDList(Students.get(i).getSID());

                        if (rs != null){
                            while(rs.next()) {
                                Students.get(i).setName(rs.getString(2));
                                Students.get(i).setAddress(rs.getString(3));
                                Students.get(i).setContactNumber(rs.getString(4));
                                Students.get(i).setAttendanceCount(rs.getInt(5));
                                Students.get(i).setAttendancePercentage(rs.getInt(6));
                            }
                            rs.close();
                        }

                        rs = dbmsUserAccount.getAttendanceBySIDList(Students.get(i).getSID());
                        if (rs != null){
                            while(rs.next())
                                Students.get(i).addPresentDayList(rs.getString(2));
                        }
                    }

                    Succeeded = true;
                }
                catch(SQLException e){
                    e.printStackTrace();
                }

                return Succeeded;
            }

            @Override
            protected void succeeded() {
                super.succeeded();

                if (Succeeded == true){
                    StudentInfoList.setAll(Students);

                    //Setting up filtered list.
                    filteredList = new FilteredList<>(StudentInfoList);
                    setCellFactory(null);
                }
            }
        };
        new Thread(Task_GetStudentInformation).start();

        return true;
    }


    /**
     * Gets class information for a section.
     * @return true if information successfully copied without error. False otherwise.
     */
    private boolean getClassInformation(){
        if (dbmsUserAccount == null){
            System.out.println("Showing from getClassInformation");
            return false;
        }

        Task<Boolean> Task_GetClassInformation = new Task<Boolean>() {
            Boolean Succeeded = false;
            Integer TotalStudents = 0;
            Integer TotalClasses = 0;
            Integer TotalDefaulters = 0;
            ObservableList<String> ClassDays = FXCollections.observableArrayList();
            ObservableList<String> SIDs = FXCollections.observableArrayList();

            @Override
            protected Boolean call() throws Exception {
                // Getting total student count
                TotalStudents = max(dbmsUserAccount.getTotalStudentCount(), 0);

                // Getting total classes taken
                TotalClasses = max(dbmsUserAccount.getTotalClassCount(), 0);

                // Getting total defaulter student count
                TotalDefaulters = max(dbmsUserAccount.getDefaulterCount(), 0);

                // Getting days the class was conducted
                try{
                    ResultSet rs = dbmsUserAccount.getTakenDayList();

                    if (rs != null){
                        while(rs.next())
                            ClassDays.add(rs.getString("DAY"));
                        rs.close();
                    }

                    rs = dbmsUserAccount.getStudentList();
                    if (rs != null){
                        while(rs.next())
                            SIDs.add(rs.getString(1));
                        rs.close();
                    }

                    Succeeded = true;
                } catch(SQLException e){
                    e.printStackTrace();
                }

                return Succeeded;
            }

            @Override
            protected void succeeded() {
                super.succeeded();

                if (Succeeded){
                    FX_T_TotalStudents.setText(TotalStudents.toString());
                    FX_T_TotalLectureCount.setText(TotalClasses.toString());
                    FX_T_TotalDefaulterStudents.setText(TotalDefaulters.toString());

                    ClassDayList.setAll(ClassDays);
                    SIDList.setAll(SIDs);

                    if (ClassDayList.size() > 0){
                        FX_LV_ClassDayList.getSelectionModel().selectFirst();
                    }
                }
            }
        };
        new Thread(Task_GetClassInformation).start();

        return true;
    }


    /**
     * Gets the account information of the teacher.
     * @return true if successful. False otherwise.
     */
    private boolean getAccountInfo(){
        if (dbmsUserAccount == null){
            System.out.println("Showing from getAccountInformation");
            return false;
        }

        TeacherAccountName = dbmsUserAccount.getUsername();
        TeacherAccountPassword = dbmsUserAccount.getPassword();

        Task<Boolean> Task_GetAccountInfo = new Task<Boolean>() {
            final ObservableList<String> Courses = FXCollections.observableArrayList();
            final ObservableList<String> Sections = FXCollections.observableArrayList();
            boolean Succeeded = false;

            @Override
            protected Boolean call() throws Exception {
                try{
                    ResultSet rs = dbmsUserAccount.getCourseList();

                    if (rs != null){
                        while(rs.next()){
                            Courses.add(rs.getString("CLASS"));
                            Sections.add(rs.getString("SECTION"));
                        }
                        rs.close();
                        Succeeded = true;
                    }
                } catch(SQLException e){
                    e.printStackTrace();
                }

                return Succeeded;
            }

            @Override
            protected void succeeded() {
                super.succeeded();

                if (Succeeded == true){
                    CourseList.setAll(Courses);
                    SectionList.setAll(Sections);

                    if (CourseList.size() > 0){
                        FX_CB_CourseList.getSelectionModel().selectFirst();
                    }
                }
            }
        };
        new Thread(Task_GetAccountInfo).start();

        return true;
    }


    /**
     * Setup cell factory.
     */
    private void setCellFactory(StudentInformation std){
        if (dbmsUserAccount == null){
            return;
        }

        if (StudentInfoList.size() > 0){
            if (filteredList.size() > 0){
                FX_LV_StudentInfo.setItems(filteredList);

                if (std == null)
                    FX_LV_StudentInfo.getSelectionModel().selectFirst();
                else
                    FX_LV_StudentInfo.getSelectionModel().select(std);
            }
        }
    }


    /**
     * Modifies (inserts or deletes) the attendance information of a student.
     * @param Operation which operation to perform. (Options : Insert, Delete)
     */
    private void modify(String Operation){
        StudentInformation std = FX_LV_StudentInfo.getSelectionModel().getSelectedItem();// Get the selected student
        int index = StudentInfoList.indexOf(std);   // Get the index of this student in StudentInfoList Array

        /*
         * Dialog necessary variables.
         * Dialog Name is the name of the dialog to open.
         * Title Text is the text to be shown in the title.
         * HeadText is the text of the Header.
         */
        String DialogName = "ModificationDialog.fxml";
        String TitleText = Operation + " Attendance Dialog";
        String HeaderText = Operation + " attendance for ID " + std.getSID() + "?";
        DrawWindows drawWindows = new DrawWindows();

        // Create the delete dialog.
        if (drawWindows.DrawDialog(DialogName, TitleText, HeaderText, FX_BorderPane_Teacher)){
            Optional<ButtonType> result = drawWindows.getDialog().showAndWait();

            // If the user presses the OK button perform the following.
            if (result.isPresent() && result.get() == ButtonType.OK){
                ModificationDialogController controller = drawWindows.getFxmlLoader().getController();// Get controller of FXML.
                String date = dateTimeFormatter.format(controller.getDate()).toUpperCase();

                if(dbmsUserAccount != null){
                    /*
                     * Step 0 : Check if date inputted is valid.
                     * Step 1 : Execute insert or delete operation based on checking.
                     * Step 2 : Check the returned value. If it's 1 then we proceed.
                     * Step 3 : Execute Attendance count and percentage command for student std.getSID()
                     * Step 4 : If the values are non zero update both their view and their temp storage.\
                     * Step 5 : Update the attendance list for that student.
                     */
                    int returned = 0;   // Stores the number of columns modified by the SQL command

                    // Step 0
                    if (dbmsUserAccount.wasTakenOnDay(date)){
                        String operation = Operation.toLowerCase();
                        // Step 1
                        if (operation.equals("insert"))
                            returned = dbmsUserAccount.insertAttendance(std.getSID(), date);
                        else if (Operation.toLowerCase().equals("delete"))
                            returned = dbmsUserAccount.deleteAttendance(std.getSID(), date);

                        // Step 2
                        if (returned == 1){
                            // Step 3
                            Integer count = dbmsUserAccount.getAttendanceCountBySID(std.getSID());
                            Integer percent = dbmsUserAccount.getAttendancePercentageBySID(std.getSID());

                            // Step 4
                            if (count != -1){
                                StudentInfoList.get(index).setAttendanceCount(count);
                                FX_T_SAttendanceCount.setText(count.toString());
                            }
                            if (percent != -1){
                                StudentInfoList.get(index).setAttendancePercentage(percent);
                                FX_T_SAttendancePercentage.setText(percent.toString());
                            }

                            // Step 5
                            if (Operation.toLowerCase().equals("insert"))
                                StudentInfoList.get(index).addPresentDayList(date);
                            else if (Operation.toLowerCase().equals("delete"))
                                StudentInfoList.get(index).removePresentDayList(date);

                            PresentDayList.setAll(StudentInfoList.get(index).getPresentDayList());
                            setCellFactory(std);
                        }
                    }
                    else{
                        // Show warning that invalid date was inputted
                        DrawWindows drawWindows1 = new DrawWindows();
                        drawWindows1.DrawAlert(
                                "Error",
                                "Invalid date inputted",
                                "Classes were not held in " + date,
                                "ERROR"
                        );

                        Optional<ButtonType> result1 = drawWindows1.getAlertResult();
                        if (result1.isPresent() && result1.get() == ButtonType.OK){
                            modify(Operation);
                        }
                    }
                }
            }
        }
    }


    /**
     * Create a context menu with 2 options.
     * Delete item or modify item.
     */
    private void setupContextMenu(){
        FX_CM_list = new ContextMenu();                    // Creating a context menu
        MenuItem delete = new MenuItem("Delete Attendance");      // Creating a menu item
        delete.setOnAction(new EventHandler<ActionEvent>() {    // Binding method on action
            @Override
            public void handle(ActionEvent event) {
                handleDelete();
            }
        });
        MenuItem edit = new MenuItem("Insert Attendance");
        edit.setOnAction(new EventHandler<ActionEvent>() {    // Binding method on action
            @Override
            public void handle(ActionEvent event) {
                handleInsertDialog();
            }
        });
        FX_CM_list.getItems().add(edit);
        FX_CM_list.getItems().add(delete);              // Attaching menu item in context menu
    }

    /**
     * Sets text colors for information displaying.
     * @param TextColor the color to show.
     */
    private void setTextColor(Color TextColor){
        FX_T_SName.setFill(TextColor);
        FX_T_SAddress.setFill(TextColor);
        FX_T_SContactNumber.setFill(TextColor);
        FX_T_SAttendanceCount.setFill(TextColor);
        FX_T_SAttendancePercentage.setFill(TextColor);
        FX_T_TotalStudents.setFill(TextColor);
        FX_T_TotalLectureCount.setFill(TextColor);
        FX_T_TotalDefaulterStudents.setFill(TextColor);
    }

    /**
     * Returns the maximum of a and b.
     * @param a number 1
     * @param b number 2
     * @return the maximum of a and b
     */
    private Integer max(Integer a, Integer b){
        if (a > b)  return a;
        return b;
    }
}
