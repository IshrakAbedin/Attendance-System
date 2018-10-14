import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;

/**
 * A temporary driver to debug and demonstrate XEbase's different methods.
 * This is to be removed upon the completion or UI build of project.
 *
 * @author Mohammad Ishrak Abedin
 * @version 0.1.0
 * @since 2018-09-24
 */
public class Driver extends Application {
    private Dialog<ButtonType> Dialog;
    private FXMLLoader fxmlLoader;
    private DialogPane dialogPane;
    private XEbase dbmsUserAccount;
    private TeacherMainWindow teacherMainWindow;

    private boolean createDialog(String DialogName, String TitleText, String HeaderText){
        try{
            Dialog = new Dialog<>();
            fxmlLoader = new FXMLLoader();
            Dialog.setTitle(TitleText);
            Dialog.setHeaderText(HeaderText);
            dialogPane = Dialog.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("dialog.css").toExternalForm());
            fxmlLoader.setLocation(getClass().getResource(DialogName));
            Dialog.getDialogPane().setContent(fxmlLoader.load());
            Dialog.getDialogPane().setBackground(
                    new Background(
                        new BackgroundFill(
                                Color.GRAY,
                                CornerRadii.EMPTY,
                                Insets.EMPTY
                        )
                    )
            );
            Dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            Dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        } catch(IOException e){
            System.out.println("Couldn't load dialogue " + DialogName);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        boolean failed = false;             // Variable keeps track of invalid user name or password.
        boolean dialogCreation = false;     // Variable keeps track of unsuccessful dialog creation.
        String AccountType = "";

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
                            AccountType = controller.getAccountType();
                            FXMLLoader MainFXMLLoader = new FXMLLoader();

                            if (AccountType.equals("Teacher")) {
                                MainFXMLLoader.setLocation(getClass().getResource("TeacherMainWindow.fxml"));
                                Parent root = MainFXMLLoader.load();
                                primaryStage.setTitle("Attendance System - Teacher");
                                primaryStage.setScene(new Scene(root, 1280, 720));
                                primaryStage.show();
                                teacherMainWindow = MainFXMLLoader.getController();
                                teacherMainWindow.setDbmsUserAccount(dbmsUserAccount);
                            }
                            else if (AccountType.equals("Department Head")){

                            }
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

    @Override
    public void stop() throws Exception {
        if (teacherMainWindow != null)
            teacherMainWindow.clearInformation();
        super.stop();
    }

    /**
     * Temporary main method for the entry point of program.
     * @param args String[] not used
     */
    public static void main(String[] args){
//        System.out.println("Hell-o-World");
//
//        XEbase dbase = new XEbase("DEMOTEACHER", "DEMOPASSWORD"); // Constructor
//        dbase.setClassname("CSE9406");
//        dbase.setSectionname("CSE16");
//        ResultSet rs;
//        try {
//
//            // getTotalStudentCount() method use
//            System.out.println("Total Student Count: " + dbase.getTotalStudentCount());
//            // Separator for ease of viewing
//            System.out.println("-----------------------------------\n\n");
//
//            // getStudentList() method use
//            rs = dbase.getStudentList();
//            if (rs == null)
//                System.out.println("No Result Found");
//            else {
//                System.out.println("SID\tName\t\tAddress\t\tContact Number");
//                while (rs.next()) { // ResultSet must be exhausted by continuous use of next() method to avoid errors.
//                    // columnIndex of ResultSet is used here. Use of columnLabel is shown later.
//                    System.out.print(rs.getString(1) + "\t");
//                    System.out.print(rs.getString(2) + "\t");
//                    System.out.print(rs.getString(3) + "\t");
//                    System.out.print(rs.getString(4) + "\n");
//                }
//            }
//            System.out.println("-----------------------------------\n\n");
//
//            // getCourseList() method use
//            rs = dbase.getCourseList();
//            if (rs == null)
//                System.out.println("No Result Found");
//            else {
//                System.out.println("Section\tClass");
//                while (rs.next()) { // ResultSet must be exhausted by continuous use of next() method to avoid errors.
//                    // columnIndex of ResultSet is used here. Use of columnLabel is shown later.
//                    System.out.print(rs.getString("SECTION") + "\t");
//                    System.out.print(rs.getString("CLASS") + "\n");
//                }
//            }
//            System.out.println("-----------------------------------\n\n");
//
//            rs = dbase.getCompleteAttendanceList(); // getCompleteAttendanceList() method use
//            if (rs == null) System.out.println("No Result Found");
//            else {
//                System.out.println("SID\tDay");
//                while (rs.next()) {
//                    // columnLabel of ResultSet is used here instead of columnIndex
//                    System.out.print(rs.getString("SID") + "\t");
//                    System.out.print(rs.getString("DAY") + "\n");
//                }
//            }
//            System.out.println("-----------------------------------\n\n");
//
//            rs = dbase.getExtendedAttendanceList(); // getCompleteAttendanceList() method use
//            if (rs == null) System.out.println("No Result Found");
//            else {
//                System.out.println("SID\tName\t\tAddress\t\tNumber\tCount\tPercentage");
//                while (rs.next()) {
//                    // columnLabel of ResultSet is used here instead of columnIndex
//                    System.out.print(rs.getString("SID") + "\t");
//                    System.out.print(rs.getString("NAME") + "\t");
//                    System.out.print(rs.getString("ADDRESS") + "\t");
//                    System.out.print(rs.getString("CONTACT_NUMBER") + "\t");
//                    System.out.print(rs.getString("ATTENDANCE_COUNT") + "\t");
//                    System.out.print(rs.getFloat("ATTENDANCE_PERCENTAGE") + "\n");
//                }
//            }
//            System.out.println("-----------------------------------\n\n");
//
//            rs = dbase.getExtendedAttendanceBySIDList("102"); // getCompleteAttendanceList() method use
//            if (rs == null) System.out.println("No Result Found");
//            else {
//                System.out.println("102\tName\t\tAddress\t\tNumber\tCount\tPercentage");
//                while (rs.next()) {
//                    // columnLabel of ResultSet is used here instead of columnIndex
//                    System.out.print(rs.getString("SID") + "\t");
//                    System.out.print(rs.getString("NAME") + "\t");
//                    System.out.print(rs.getString("ADDRESS") + "\t");
//                    System.out.print(rs.getString("CONTACT_NUMBER") + "\t");
//                    System.out.print(rs.getString("ATTENDANCE_COUNT") + "\t");
//                    System.out.print(rs.getFloat("ATTENDANCE_PERCENTAGE") + "\n");
//                }
//            }
//            System.out.println("-----------------------------------\n\n");
//
//            System.out.println("Inserted Rows: " + dbase.insertAttendance("101", "SEP25")); // insertAttendance() method use
//            System.out.println("-----------------------------------\n\n");
//
//            rs = dbase.getAttendanceByDayList("SEP24"); // getAttendanceByDayList() method use
//            if (rs == null) System.out.println("No Result Found");
//            else {
//                System.out.println("SID\tDay");
//                while (rs.next()) {
//                    System.out.print(rs.getString("SID") + "\t");
//                    System.out.print(rs.getString("DAY") + "\n");
//                }
//            }
//            System.out.println("-----------------------------------\n\n");
//
//            System.out.println("Deleted Rows: " + dbase.deleteAttendance("101", "SEP25")); // deleteAttendance() method use
//            System.out.println("-----------------------------------\n\n");
//
//            rs = dbase.getAttendanceByDayList("SEP25"); // getAttendanceByDayList() method use
//            if (rs == null) System.out.println("No Result Found");
//            else {
//                System.out.println("SID\tDay");
//                while (rs.next()) {
//                    System.out.print(rs.getString("SID") + "\t");
//                    System.out.print(rs.getString("DAY") + "\n");
//                }
//            }
//            System.out.println("-----------------------------------\n\n");
//
//            rs = dbase.getAttendanceBySIDList("102"); // getAttendanceByDayList() method use
//            if (rs == null) System.out.println("No Result Found");
//            else {
//                System.out.println("SID\tDay");
//                while (rs.next()) {
//                    System.out.print(rs.getString("SID") + "\t");
//                    System.out.print(rs.getString("DAY") + "\n");
//                }
//            }
//            System.out.println("-----------------------------------\n\n");
//
//            rs = dbase.getAttendanceCountPerStudentList(); // getAttendanceCountPerStudentList() method use
//            if (rs == null) System.out.println("No Result Found");
//            else {
//                System.out.println("SID\tCount");
//                while (rs.next()) {
//                    System.out.print(rs.getString("SID") + "\t");
//                    System.out.print(rs.getInt("ATTENDANCE_COUNT") + "\n");
//                }
//            }
//            System.out.println("-----------------------------------\n\n");
//
//            System.out.println("Total class count: " + dbase.getTotalClassCount()); // getTotalClassCount() method use
//            System.out.println("-----------------------------------\n\n");
//
//            rs = dbase.getAttendancePercentageList(); // getAttendancePercentageList() method use
//            if (rs == null) System.out.println("No Result Found");
//            else {
//                System.out.println("SID\tPercentage");
//                while (rs.next()) {
//                    System.out.print(rs.getString("SID") + "\t");
//                    System.out.print(rs.getInt("ATTENDANCE_PERCENTAGE") + "\n");
//                }
//            }
//            System.out.println("-----------------------------------\n\n");
//
//            rs = dbase.getAttendancePercentageListWithName(); // getAttendancePercentageListWithName() method use
//            if (rs == null) System.out.println("No Result Found");
//            else {
//                System.out.println("SID\tName\tPercentage");
//                while (rs.next()) {
//                    System.out.print(rs.getString(1) + "\t");
//                    System.out.print(rs.getString(2) + "\t");
//                    System.out.print(rs.getInt(3) + "\n");
//                }
//            }
//            System.out.println("-----------------------------------\n\n");
//
//            rs = dbase.getAttendanceDefauterList(); // getAttendanceDefauterList() method use
//            if (rs == null) System.out.println("No Result Found");
//            else {
//                System.out.println("SID\tDef_Percentage");
//                while (rs.next()) {
//                    System.out.print(rs.getString("SID") + "\t");
//                    System.out.print(rs.getInt("ATTENDANCE_PERCENTAGE") + "\n");
//                }
//            }
//            System.out.println("-----------------------------------\n\n");
//
//            rs = dbase.getAttendanceDefauterListWithName(); // getAttendanceDefauterListWithName()
//            if (rs == null) System.out.println("No Result Found");
//            else {
//                System.out.println("SID\tName\tDef_Percentage");
//                while (rs.next()) {
//                    System.out.print(rs.getString(1) + "\t");
//                    System.out.print(rs.getString(2) + "\t");
//                    System.out.print(rs.getInt(3) + "\n");
//                }
//            }
//            System.out.println("-----------------------------------\n\n");
//
//            System.out.println("Def count: "+dbase.getDefaulterCount()); // getDefaulterCount()
//            System.out.println("-----------------------------------\n\n");
//
//            rs = dbase.getTakenDayList(); // getAttendanceDefauterListWithName()
//            if (rs == null) System.out.println("No Result Found");
//            else {
//                System.out.println("Day\n");
//                while (rs.next()) {
//                    System.out.print(rs.getString("DAY") + "\n");
//                }
//            }
//            System.out.println("-----------------------------------\n\n");
//
//            System.out.println(dbase.wasTakenOnDay("SEP24"));
//            System.out.println(dbase.wasTakenOnDay("SEP25"));
//            System.out.println(dbase.wasTakenOnDay("SEP23"));
//            System.out.println(dbase.wasTakenOnDay("SEP26"));
//            System.out.println("-----------------------------------\n\n");
//
//        }
//        catch (SQLException ex){
//            ex.printStackTrace(); // Exception handled and printed
//        }
//        finally {
//            dbase.close(); // finally statement makes sure database is always closed at end regardless of the occurrence of exception
//        }

        launch(args);
    }
}
