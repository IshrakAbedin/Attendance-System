import Data.StudentInformation;
import Data.TeacherAccountData;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
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
import java.io.IOException;
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
    private Dialog<ButtonType> Dialog;
    private FXMLLoader fxmlLoader;
    private Predicate<StudentInformation> DefaulterStudents;
    private Predicate<StudentInformation> AllStudents;
    private FilteredList<StudentInformation> filteredList;
    private TeacherAccountData AccountData;
    private String FontName;
    private Background ListViewBackground;
    private Background DialogBackground;
    private DialogPane dialogPane;
    private String DialogStyleCSS;
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
            setupAccountButton(dbmsUserAccount.name, "Press to log out");
            getAccountInfo();
            getClassInformation();
            FX_LV_ClassDayList.getSelectionModel().selectFirst();
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
        DialogStyleCSS = "dialog.css";
        FontName = "Arial";
        AccountData = null;
        StudentInfoList = FXCollections.observableArrayList();
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
        FX_LV_ClassDayList.setCellFactory(cellColor);
        FX_LV_PresentDays.setCellFactory(cellColor);
        FX_CB_CourseList.setCellFactory(cellColor);
        setCellFactory(null);
    }


    /**
     * Creates a dialogue through which you log in to your account.
     */
    @FXML private void handleLogOut(){
        handleExit();
    }


    /**
     * Handles key press events
     * @param k is the event when a keyboard button is pressed
     */
    @FXML private void handleKeyPressed(KeyEvent k){
        if (dbmsUserAccount == null){
            System.out.println("Showing from keyPressed");
            showWarning(
                    "Error",
                    "Please log in first",
                    "To use the application logging in is required beforehand.",
                    "account"
            );
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
        showWarning(
                "Exit warning",
                "Are you sure you want to exit?",
                "Press OK to exit, otherwise press cancel",
                "exit"
        );
    }


    /**
     * Handles the code for editing task details.
     * Creates a Dialog to display old information
     * and runs the code to save the new information from the Dialog.
     */
    @FXML private void handleInsertDialog(){
        if (dbmsUserAccount == null){
            System.out.println("Showing from insert");

            showWarning(
                    "Error",
                    "Please log in first",
                    "To use the application logging in is required beforehand.",
                    "account"
            );
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

            showWarning(
                    "Error",
                    "Please log in first",
                    "To use the application logging in is required beforehand.",
                    "account"
            );
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

            FX_TB_FilterDefaulter.setSelected(false);
            showWarning(
                    "Error",
                    "Please log in first",
                    "To use the application logging in is required beforehand.",
                    "account"
            );
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
     * Handles information viewing specific to a course of a specific section.
     */
    @FXML private void handleCourseChange(){
        if (dbmsUserAccount == null){
            System.out.println("Showing from course change");
            return;
        }

        // Get selected class and section info
        String CourseName = FX_CB_CourseList.getSelectionModel().getSelectedItem();
        String SectionName = AccountData.getSectionForCourse(CourseName);

        // Set new class and section name in dbms.
        dbmsUserAccount.setClassname(CourseName);
        dbmsUserAccount.setSectionname(SectionName);

        // Retrieve new class and section information
        clearClassInformation();
        getClassInformation();
        clearStudentInformation();
        FX_LV_ClassDayList.getSelectionModel().selectFirst();
        FX_LV_StudentInfo.getSelectionModel().selectFirst();
    }


    /**
     * Handles searching for student info
     */
    @FXML private void handleSearch(){
        if (dbmsUserAccount == null){
            System.out.println("Showing from search");

            FX_TB_FilterDefaulter.setSelected(false);
            showWarning(
                    "Error",
                    "Please log in first",
                    "To use the application logging in is required beforehand.",
                    "account"
            );
            return;
        }

        String DialogName = "SearchDialog.fxml";
        String TitleText = "Search for student";
        String HeaderText = "Enter student ID and section to begin searching";
        if (createDialog(DialogName, TitleText, HeaderText)){
            System.out.println("Created!");
            SearchDialog controller = fxmlLoader.getController();
            String CourseName = FX_CB_CourseList.getSelectionModel().getSelectedItem();
            String SectionName = AccountData.getSectionForCourse(CourseName);
            controller.process(dbmsUserAccount);

            Optional<ButtonType> result = Dialog.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                Dialog.close();
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

        boolean dialogCreation = createDialog(
                "TakeAttendance.fxml",
                "Take attendance",
                "Move the present students to the present list to give them attendance"
        );

        if (dialogCreation){
            TakeAttendance takeAttendance = fxmlLoader.getController();
            takeAttendance.setFX_LV_AbsentStudents(SIDList);
            Optional<ButtonType> result = Dialog.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                ObservableList<String> PresentStudents = takeAttendance.getFX_LV_PresentStudents();
                int state = 0;

                for(int i = 0; i < PresentStudents.size(); ++i){
                    String date = dateTimeFormatter.format(LocalDate.now());
                    date = date.toUpperCase();
                    state = dbmsUserAccount.insertAttendance(PresentStudents.get(i), date);

                    if (state == -1){
                        System.out.println("Failed for " + PresentStudents.get(i));
                    }
                }

                getClassInformation();
            }
        }
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

        clearStudentInformation();

        /*
         * Get the student information (SID, Name, Address, Contact no) from the dbms.
         * If Base information isn't accessible then none of the other portions will work.
         */
        try{
            clearStudentInformation();

            String day = FX_LV_ClassDayList.getSelectionModel().getSelectedItem();
            ResultSet rs = dbmsUserAccount.getAttendanceByDayList(day);

            if (rs != null){
                while(rs.next()){
                    StudentInformation std = new StudentInformation(rs.getString(1));
                    StudentInfoList.add(std);
                }
                rs.close();
            }

            for(int i = 0; i < StudentInfoList.size(); ++i){
                rs = dbmsUserAccount.getExtendedAttendanceBySIDList(StudentInfoList.get(i).getSID());

                if (rs != null){
                    while(rs.next()) {
                        StudentInfoList.get(i).setName(rs.getString(2));
                        StudentInfoList.get(i).setAddress(rs.getString(3));
                        StudentInfoList.get(i).setContactNumber(rs.getString(4));
                        StudentInfoList.get(i).setAttendanceCount(rs.getInt(5));
                        StudentInfoList.get(i).setAttendancePercentage(rs.getInt(6));
                    }
                }

                rs.close();

                rs = dbmsUserAccount.getAttendanceBySIDList(StudentInfoList.get(i).getSID());

                if (rs != null){
                    while(rs.next()) {
                        StudentInfoList.get(i).addPresentDayList(rs.getString(2));
                    }
                }
            }

            //Setting up filtered list.
            filteredList = new FilteredList<>(StudentInfoList);
            setCellFactory(null);

            return true;
        }
        catch(SQLException e){
            e.printStackTrace();
        }

        return false;
    }


    /**
     * Gets class information for a section.
     * @return true if information successfully copied without error. False otherwise.
     */
    private boolean getClassInformation(){
        if (dbmsUserAccount == null){
            System.out.println("Showing from getClassInformation");

            showWarning(
                    "Error",
                    "Please log in first",
                    "To use the application logging in is required beforehand.",
                    "account"
            );
            return false;
        }

        clearClassInformation();

        // Getting total student count
        Integer TotalStudents = dbmsUserAccount.getTotalStudentCount();
        if (TotalStudents != -1){
            FX_T_TotalStudents.setText(TotalStudents.toString());
        } else{
            FX_T_TotalStudents.setText("0");
        }

        // Getting total classes taken
        Integer TotalClasses = dbmsUserAccount.getTotalClassCount();
        if (TotalClasses != -1){
            FX_T_TotalLectureCount.setText(TotalClasses.toString());
        }else{
            FX_T_TotalLectureCount.setText("0");
        }

        // Getting total defaulter student count
        Integer TotalDefaulters = dbmsUserAccount.getDefaulterCount();
        if (TotalDefaulters != -1){
            FX_T_TotalDefaulterStudents.setText(TotalDefaulters.toString());
        } else {
            FX_T_TotalDefaulterStudents.setText("0");
        }

        // Getting days the class was conducted
        try{
            AccountData.clearClassDays();
            ResultSet rs = dbmsUserAccount.getTakenDayList();

            if (rs != null){
                while(rs.next()){
                    AccountData.addToClassDays(rs.getString("DAY"));
                }

                FX_LV_ClassDayList.setItems(AccountData.getClassDaysList());
                rs.close();
            }

            rs = dbmsUserAccount.getStudentList();
            if (SIDList != null){
                SIDList.clear();
            } else {
                SIDList = FXCollections.observableArrayList();
            }

            if (rs != null){
                while(rs.next()){
                    SIDList.add(rs.getString(1));
                }
                rs.close();
            }
        } catch(SQLException e){
            e.printStackTrace();

            return false;
        }

        return true;
    }


    /**
     * Gets the account information of the teacher.
     * @return true if successful. False otherwise.
     */
    private boolean getAccountInfo(){
        if (dbmsUserAccount == null){
            System.out.println("Showing from getAccountInformation");

            showWarning(
                    "Error",
                    "Please log in first",
                    "To use the application logging in is required beforehand.",
                    "account"
            );
            return false;
        }

        if (AccountData != null){
            return false;
        }

        // Get teacher acount information
        AccountData = new TeacherAccountData(
                dbmsUserAccount.getUsername(),
                dbmsUserAccount.getPassword()
        );

        try{
            ResultSet rs = dbmsUserAccount.getCourseList();

            if (rs != null){
                while(rs.next()){
                    AccountData.addToCourseList(
                            rs.getString("CLASS"),
                            rs.getString("SECTION")
                    );
                }

                FX_CB_CourseList.setItems(AccountData.getCourseList());
                FX_CB_CourseList.getSelectionModel().selectFirst();

                rs.close();
            }
        } catch(SQLException e){
            e.printStackTrace();

            return false;
        }

        return true;
    }


    /**
     * Setup cell factory.
     */
    private void setCellFactory(StudentInformation std){
        if (dbmsUserAccount == null){
            return;
        }

        if (StudentInfoList != null){
            if (filteredList != null){
                FX_LV_StudentInfo.setItems(filteredList);
                System.out.println(StudentInfoList.size() + filteredList.size());
                FX_LV_StudentInfo.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

                if (std == null) {
                    FX_LV_StudentInfo.getSelectionModel().selectFirst();
                }
                else{
                    FX_LV_StudentInfo.getSelectionModel().select(std);
                }

                // Setting up cell factory
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
                        cell.backgroundProperty().setValue(ListViewBackground);

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

        // Create the delete dialog.
        if (createDialog(DialogName, TitleText, HeaderText)){
            Optional<ButtonType> result = Dialog.showAndWait();

            // If the user presses the OK button perform the following.
            if (result.isPresent() && result.get() == ButtonType.OK){
                ModificationDialogController controller = fxmlLoader.getController();// Get controller of FXML.
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

                            FX_LV_PresentDays.setItems(StudentInfoList.get(index).getPresentDayList());
                            setCellFactory(std);
                        }
                    }
                    else{
                        // Show warning that invalid date was inputted
                        showWarning(
                                "Error",
                                "Invalid date inputted",
                                "Classes were not held in " + date,
                                Operation
                        );
                    }
                }
            }
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
            Dialog.initOwner(FX_BorderPane_Teacher.getScene().getWindow());
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
     * Clears everything temporarily stored and viewed on UI.
     * Also logs out from dbms account.
     */
    public void clearInformation(){
        // Clearing account information
        clearAccountInformation();

        // Clearing class information
        clearClassInformation();

        // Clearing student information
        clearStudentInformation();
    }


    /**
     * Clears temporary student information.
     */
    private void clearStudentInformation(){
        StudentInfoList.clear();
        FX_T_SName.setText("");
        FX_T_SAddress.setText("");
        FX_T_SContactNumber.setText("");
        FX_T_SAttendanceCount.setText("");
        FX_T_SAttendancePercentage.setText("");
        FX_T_SAttendanceRemarks.setText("");
        FX_LV_StudentInfo.setItems(null);
        FX_LV_PresentDays.setItems(null);
    }


    /**
     * Clears temporary Class information.
     */
    private void clearClassInformation(){
        FX_T_TotalLectureCount.setText("");
        FX_T_TotalStudents.setText("");
        FX_T_TotalDefaulterStudents.setText("");
        FX_LV_ClassDayList.setItems(null);
    }


    /**
     * Clears temporary Account information.
     */
    private void clearAccountInformation(){
        dbmsUserAccount.close();
        dbmsUserAccount = null;
        AccountData = null;
        FX_CB_CourseList.setItems(null);
    }


    /**
     * Displays an error message when trying to use the software without logging in.
     * @param TitleText The title of the warning
     * @param HeaderText the header of the warning
     * @param ContentText the context of the warning
     * @param Type Type of issue generating the command.
     */
    private void showWarning(String TitleText, String HeaderText, String ContentText, String Type){
        String type = Type.toLowerCase();

        Alert alert = null;
        if (type.equals("account") || type.equals("insert") || type.equals("delete")){
            alert = new Alert(Alert.AlertType.ERROR);
        }
        else if (type.equals("exit")){
            alert = new Alert(Alert.AlertType.CONFIRMATION);
        }

        alert.setTitle(TitleText);
        alert.setHeaderText(HeaderText);
        alert.setContentText(ContentText);
        dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource(DialogStyleCSS).toExternalForm());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            if (type.equals("account")) {
                handleLogOut();
            }
            else if (type.equals("insert") || type.equals("delete")) {
                modify(Type);
            }
            else if (type.equals("exit")){
                if (dbmsUserAccount != null){
                    dbmsUserAccount.close();
                }

                Platform.exit();
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
}
