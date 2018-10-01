import Data.StudentInformation;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import javafx.collections.ObservableList;
import javafx.util.Callback;

public class Mainwindow {
    private XEbase dbmsUserAccount;
    private ObservableList<StudentInformation> StudentInfoList;
    private Dialog<ButtonType> Dialog;
    private FXMLLoader fxmlLoader;
    private Predicate<StudentInformation> DefaulterStudents;
    private Predicate<StudentInformation> AllStudents;
    private FilteredList<StudentInformation> filteredList;

    @FXML private BorderPane mainBorderPane;        // FXML Main Border Pane
    @FXML private Button AccountButton;             // Account button related to log in and log out.
    @FXML private Text DateText;                    // Text that display's the date in the app.
    @FXML private DatePicker FXDatePicker;          // Linking to date picker in FXML
    @FXML private ListView<StudentInformation> FXStudentInfoListView; // Linking the list view in FXML
    @FXML private Text FXStudentName;               // Linking student name
    @FXML private Text FXStudentAddress;            // Linking student Address
    @FXML private Text FXStudentContactNumber;      // Linking student contact info
    @FXML private Text FXTotalStudents;             // Linking the total number of students
    @FXML private Text FXStudentAttendanceCount;    // Linking the total attendance count of a student
    @FXML private Text FXStudentAttendancePercentage; // Linking the total attendance percentage of a student
    @FXML private Text FXStudentAttendanceRemarks;  // Linking the total attendance percentage of a student
    @FXML private Text FXTotalClassCount;
    @FXML private Text FXTotalDefaulterStudents;
    @FXML private ToggleButton FXFilterDefaulter;
    @FXML private ContextMenu listContextMenu;
    @FXML private ListView<String> FXPresentDaysListView;

    /**
     * Runs when first launching the application.
     */
    public void initialize(){
        /**
         * Create a context menu with 2 options.
         * Delete item or modify item.
         */
        listContextMenu = new ContextMenu();                    // Creating a context menu
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
        listContextMenu.getItems().add(edit);
        listContextMenu.getItems().add(delete);              // Attaching menu item in context menu

        /**
         * Sets up student and class information viewing base.
         */
        StudentInfoList = FXCollections.observableArrayList();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateText.setText("Date : " + df.format(LocalDateTime.now()));

        // Setting up Account button (log in and log out) button
        if (dbmsUserAccount == null){
            setupAccountButton("Log In", "Press to log in");
        } else {
            setupAccountButton(dbmsUserAccount.name, "Press to log out");
        }

        /**
         * Instantiating predicates for filtering on and off.
         * AllStudents predicate is used to view all students.
         * DefaulterStudents predicate is used to view only defaulter students.
         */
        AllStudents = new Predicate<StudentInformation>() {
            @Override
            public boolean test(StudentInformation stdInfo) {
                return true;
            }
        };
        DefaulterStudents = new Predicate<StudentInformation>() {
            @Override
            public boolean test(StudentInformation stdInfo) {
                return (stdInfo.getAttendancePercentage() <= 75);
            }
        };

        // Change listener
        FXStudentInfoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<StudentInformation>() {
            @Override
            public void changed(ObservableValue<? extends StudentInformation> observable, StudentInformation oldValue, StudentInformation newValue) {
                if (newValue != null){
                    StudentInformation sd = FXStudentInfoListView.getSelectionModel().getSelectedItem();
                    FXStudentName.setText(sd.getName());
                    FXStudentAddress.setText(sd.getAddress());
                    FXStudentContactNumber.setText(sd.getContactNumber());
                    FXStudentAttendanceCount.setText(sd.getAttendanceCount().toString());
                    FXStudentAttendancePercentage.setText(sd.getAttendancePercentage().toString());
                    if (sd.getAttendancePercentage() >= 75){
                        FXStudentAttendanceRemarks.setText("Regular");
                        FXStudentAttendanceRemarks.setFont(Font.font("Times New Roman", null, null, 20));
                    }
                    else{
                        FXStudentAttendanceRemarks.setText("Defaulter");
                        FXStudentAttendanceRemarks.setFont(Font.font("Times New Roman bold", null, null, 20));
                    }
                    FXPresentDaysListView.setItems(sd.getPresentDayList());
                }
            }
        });

        setCellFactory(null);
    }

    /**
     * Creates a dialogue through which you log in to your account.
     */
    @FXML public void showLogInDialogue(){
        // If the dbmsUserAccount is null that means no one is logged in. So log in possible.
        if (dbmsUserAccount == null){
            boolean failed = false;             // Variable keeps track of invalid user name or password.
            boolean dialogCreation = false;     // Variable keeps track of unsuccessful dialog creation.

            // Keep doing unless the user enters a legit username and password.
            do{
                if (failed){    // If log in failed then show this
                    dialogCreation = createDialog(
                            "LogInDialogue.fxml",
                            "Log In Dialogue",
                            "Invalid Username and/or password."
                    );
                } else {        // If log in for first time then show this
                    dialogCreation = createDialog(
                            "LogInDialogue.fxml",
                            "Log In Dialogue",
                            "Use this Dialog to Log In"
                    );
                }

                // If dialog was successfully created then proceed.
                if (dialogCreation){
                    Optional<ButtonType> result = Dialog.showAndWait();

                    // If the user presses the OK button perform the following.
                    if (result.isPresent() && result.get() == ButtonType.OK){
                        // Get controller of FXML.
                        LogInDialogueController controller = fxmlLoader.getController();

                        if (controller.processLogIn()){
                            dbmsUserAccount = new XEbase(
                                    controller.getUserName(),
                                    controller.getPassWord()
                            );

                            if (dbmsUserAccount.connected == true){
                                getClassInformation();
                                getStudentInformationList();
                                setupAccountButton(dbmsUserAccount.name, "Press to log out");
                                setCellFactory(null);
                            }
                            else {
                                failed = true;
                            }
                        }
                    } else{
                        System.out.println("CANCEL");
                        break;
                    }
                }
                else {
                    System.out.println("Dialog couldn't be created!");
                    break;
                }
            } while (dbmsUserAccount.connected == false);
        }
        // Not null so log out
        else {
            clearInformation();
            setupAccountButton("Log In", "Press to log in");
        }
    }

    /**
     * Handles key press events
     * @param k is the event when a keyboard button is pressed
     */
    @FXML public void handleKeyPressed(KeyEvent k){
        if (dbmsUserAccount == null){
            showWarning(
                    "Error",
                    "Please log in first",
                    "To use the application logging in is required beforehand."
            );
            return;
        }

        StudentInformation item = FXStudentInfoListView.getSelectionModel().getSelectedItem();

        if (k.getCode().equals(KeyCode.ESCAPE)){
            handleExit();
        } else if (item != null){
            if (k.getCode().equals(KeyCode.DELETE)){
                handleDelete();
            } else if (k.getCode().equals(KeyCode.INSERT)){
                handleInsertDialog();
            } else if (k.getCode().equals(KeyCode.ENTER)){
                handleInsertDialog();
            }
        }
    }

    /**
     * Closes connection and exits application.
     */
    @FXML public void handleExit(){
        if (dbmsUserAccount != null){
            dbmsUserAccount.close();
        }
        Platform.exit();
    }

    /**
     * Handles the code for editing task details.
     * Creates a Dialog to display old information
     * and runs the code to save the new information from the Dialog.
     */
    @FXML public void handleInsertDialog(){
        if (dbmsUserAccount == null){
            showWarning(
                    "Error",
                    "Please log in first",
                    "To use the application logging in is required beforehand."
            );
            return;
        }

        modify("Insert");
    }

    /**
     * handles the code for deleting a task.
     * Creates a confirmation Dialog and then proceeds to delete with consent.
     */
    @FXML public void handleDelete(){
        if (dbmsUserAccount == null){
            showWarning(
                    "Error",
                    "Please log in first",
                    "To use the application logging in is required beforehand."
            );
            return;
        }

        modify("Delete");
    }

    /**
     * handles the filtering mechanism.
     */
    @FXML public void handleFilter(){
        if (dbmsUserAccount == null){
            FXFilterDefaulter.setSelected(false);
            showWarning(
                    "Error",
                    "Please log in first",
                    "To use the application logging in is required beforehand."
            );
            return;
        }

        StudentInformation std = FXStudentInfoListView.getSelectionModel().getSelectedItem();

        if (FXFilterDefaulter.isSelected()){
            System.out.println("FILTER ON!");
            filteredList.setPredicate(DefaulterStudents);

            if (filteredList.isEmpty()){
                FXStudentName.setText("");
                FXStudentAddress.setText("");
                FXStudentContactNumber.setText("");
                FXStudentAttendanceCount.setText("");
                FXStudentAttendancePercentage.setText("");
            }
            else if (filteredList.contains(std)){
                FXStudentInfoListView.getSelectionModel().select(std);
            }
            else {
                FXStudentInfoListView.getSelectionModel().selectFirst();
            }
        } else {
            System.out.println("FILTER OFF!");
            filteredList.setPredicate(AllStudents);
            FXStudentInfoListView.getSelectionModel().selectFirst();
        }
    }

    /**
     * Setups the log in and out button text and tooltip text.
     * @param buttonText what the button text should be
     * @param buttonTooltip what the tooltip should say
     */
    private void setupAccountButton(String buttonText, String buttonTooltip){
        if (buttonText.length() > 0 && buttonTooltip.length() > 0) {
            AccountButton.setText(buttonText);
            AccountButton.getTooltip().setText(buttonTooltip);
        }
    }

    /**
     * Method copies the student information retrieved from the Database for displaying.
     * @return true if information successfully copied without error. False otherwise.
     */
    private boolean getStudentInformationList(){
        // Check if the dbmsUserAccount is valid. Meaning if a log in was successful
        if (dbmsUserAccount == null){
            showWarning(
                    "Error",
                    "Please log in first",
                    "To use the application logging in is required beforehand."
            );
            return false;
        }

        /**
         * Get the student information (SID, Name, Address, Contact no) from the dbms.
         * If Base information isn't accessible then none of the other portions will work.
         */
        try{
            ResultSet rs = dbmsUserAccount.getStudentList();
            if (rs != null) {       // Proceed if resultSet is not null
                while(rs.next()){   // Create the student info list with base information
                    StudentInformation std = new StudentInformation(
                            rs.getString(1),    // SID
                            rs.getString(2),    // NAME
                            rs.getString(3),    // ADDRESS
                            rs.getString(4)     // CONTACT INFORMATION
                    );
                    StudentInfoList.add(std);
                }
                rs.close();

                /**
                 * Setting the present days for each and every student of a class.
                 */
                try{
                    for(int i = 0; i < StudentInfoList.size(); ++i){
                        rs = dbmsUserAccount.getAttendanceBySIDList(StudentInfoList.get(i).getSID());

                        if (rs != null){
                            while(rs.next()) {
                                StudentInfoList.get(i).addPresentDayList(rs.getString(2));
                            }
                        }
                    }
                }
                catch (SQLException e){
                    System.out.println("Error occurred");
                    e.printStackTrace();
                }


                /**
                 * Get the student attendance count information from the dbms
                 */
                try{            // Try to Copy the info into a list (StudentInfoList).
                    rs = dbmsUserAccount.getAttendanceCountPerStudentList();
                    if (rs != null){    // Proceed if resultSet is not null and list created
                        int i = 0;
                        while (rs.next()){
                            StudentInformation std = StudentInfoList.get(i);
                            std.setAttendanceCount(rs.getInt("ATTENDANCE_COUNT"));
                            ++i;
                        }
                        rs.close();
                    }
                    else{
                        System.out.println("getAttendanceCountPerStudentList returned null!");
                    }
                }
                catch (SQLException e){
                    System.out.println("Error occurred");
                    e.printStackTrace();
                }


                /**
                 * Get the student attendance percentage information from the dbms
                 */
                try{            // Try to Copy the info into a list (StudentInfoList).
                    rs = dbmsUserAccount.getAttendancePercentageList();
                    if (rs != null){    // Proceed if resultSet is not null
                        int i = 0;
                        while (rs.next()){
                            StudentInformation std = StudentInfoList.get(i);
                            std.setAttendancePercentage(rs.getInt("ATTENDANCE_PERCENTAGE"));
                            ++i;
                        }
                        rs.close();
                    }
                    else{
                        System.out.println("getAttendancePercentageList returned null!");
                    }
                }
                catch (SQLException e){
                    System.out.println("Error occurred");
                    e.printStackTrace();
                }

                /**
                 * Setting up filtered list.
                 */
                filteredList = new FilteredList<>(StudentInfoList);

                return true;
            }
            else {
                System.out.println("getStudentList returned null!");
            }
        }
        catch (SQLException e){
            System.out.println("Error occurred");
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
            showWarning(
                    "Error",
                    "Please log in first",
                    "To use the application logging in is required beforehand."
            );
            return false;
        }

        // Getting total student count
        Integer TotalStudents = dbmsUserAccount.getTotalStudentCount();
        FXTotalStudents.setText(TotalStudents.toString());

        // Getting total classes taken
        Integer TotalClasses = dbmsUserAccount.getTotalClassCount();
        FXTotalClassCount.setText(TotalClasses.toString());

        // Getting total defaulter student count
        Integer TotalDefaulters = dbmsUserAccount.getDefaulterCount();
        FXTotalDefaulterStudents.setText(TotalDefaulters.toString());

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
                FXStudentInfoListView.setItems(filteredList);
                FXStudentInfoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

                if (std == null) {
                    FXStudentInfoListView.getSelectionModel().selectFirst();
                }
                else{
                    FXStudentInfoListView.getSelectionModel().select(std);
                }

                // Setting up cell factory
                FXStudentInfoListView.setCellFactory(new Callback<ListView<StudentInformation>, ListCell<StudentInformation>>() {
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

                                    if (std.getAttendancePercentage() >= 85){
                                        setTextFill(Color.GREEN);
                                    } else if (std.getAttendancePercentage() >= 75){
                                        setTextFill(Color.BLUE);
                                    } else if (std.getAttendancePercentage() < 75){
                                        setFont(Font.font("Times New Roman bold", null, null, 16));
                                        setTextFill(Color.RED);
                                    }

                                }
                            }
                        };

                        // Attaching context menu to respond to only cells
                        cell.emptyProperty().addListener(
                                (obs, wasEmpty, isNowEmpty) -> {
                                    if (isNowEmpty)cell.setContextMenu(null);
                                    else           cell.setContextMenu(listContextMenu);
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
        StudentInformation std = FXStudentInfoListView.getSelectionModel().getSelectedItem();// Get the selected student
        int index = StudentInfoList.indexOf(std);   // Get the index of this student in StudentInfoList Array

        /**
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

                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMMdd");
                String date = dateTimeFormatter.format(controller.getDate()).toUpperCase();

                if(dbmsUserAccount != null){
                    /**
                     * Step 1 : Execute insert or delete operation based on checking.
                     * Step 2 : Check the returned value. If it's 1 then we proceed.
                     * Step 3 : Execute Attendance count and percentage command for student std.getSID()
                     * Step 4 : If the values are non zero update both their view and their temp storage.\
                     * Step 5 : Update the attendance list for that student.
                     */
                    int returned = 0;   // Stores the number of columns modified by the SQL command

                    // Step 1
                    if (Operation.toLowerCase().equals("insert")){
                        returned = dbmsUserAccount.insertAttendance(std.getSID(), date);
                    }
                    else if (Operation.toLowerCase().equals("delete")){
                        returned = dbmsUserAccount.deleteAttendance(std.getSID(), date);
                    }

                    // Step 2
                    if (returned == 1){
                        // Step 3
                        Integer count = dbmsUserAccount.getAttendanceCountBySID(std.getSID());
                        Integer percent = dbmsUserAccount.getAttendancePercentageBySID(std.getSID());

                        // Step 4
                        if (count != -1){
                            StudentInfoList.get(index).setAttendanceCount(count);
                            FXStudentAttendanceCount.setText(count.toString());
                        }
                        if (percent != -1){
                            StudentInfoList.get(index).setAttendancePercentage(percent);
                            FXStudentAttendancePercentage.setText(percent.toString());
                        }

                        // Step 5
                        if (Operation.toLowerCase().equals("insert")){
                            StudentInfoList.get(index).addPresentDayList(date);
                        }
                        else if (Operation.toLowerCase().equals("delete")){
                            StudentInfoList.get(index).removePresentDayList(date);
                        }

                        FXPresentDaysListView.setItems(StudentInfoList.get(index).getPresentDayList());
                        setCellFactory(std);
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
        Dialog = new Dialog<>();
        fxmlLoader = new FXMLLoader();
        Dialog.initOwner(mainBorderPane.getScene().getWindow());
        Dialog.setTitle(TitleText);
        Dialog.setHeaderText(HeaderText);
        fxmlLoader.setLocation(getClass().getResource(DialogName));

        try{
            Dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch(IOException e){
            System.out.println("Couldn't load dialogue " + DialogName);
            e.printStackTrace();
            return false;
        }

        Dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        Dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        return true;
    }

    /**
     * Clears everything temporarily stored and viewed on UI.
     * Also logs out from dbms account.
     */
    private void clearInformation(){
        // Clearing dbms account
        dbmsUserAccount.close();
        dbmsUserAccount = null;

        // Clearing class information
        FXTotalClassCount.setText("");
        FXTotalStudents.setText("");
        FXTotalDefaulterStudents.setText("");

        // Clearing student information
        StudentInfoList.clear();
        FXStudentName.setText("");
        FXStudentAddress.setText("");
        FXStudentContactNumber.setText("");
        FXStudentAttendanceCount.setText("");
        FXStudentAttendancePercentage.setText("");
        FXStudentAttendanceRemarks.setText("");
        FXStudentInfoListView.setItems(null);
        FXPresentDaysListView.setItems(null);
    }

    /**
     * Displays an error message when trying to use the software without logging in.
     * @param TitleText
     * @param HeaderText
     * @param ContentText
     */
    private void showWarning(String TitleText, String HeaderText, String ContentText){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(TitleText);
        alert.setHeaderText(HeaderText);
        alert.setContentText(ContentText);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            showLogInDialogue();
        }
    }
}
