import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.Optional;

public class DrawMainStage {
    private static DrawMainStage instance = new DrawMainStage();
    public static Stage TeacherStage;
    public static FXMLLoader TeacherFXMLLoader;
    public static Parent TeacherRoot;
    public static Stage AdminStage;
    public static FXMLLoader AdminFXMLLoader;
    public static Parent AdminRoot;
    public static Callback<ListView<String>, ListCell<String>> cellColor;
    public static Background ListViewBackground;

    private XEbase dbmsUserAccount;
    private XEadmin dbmsAdminAccount;
    private TeacherMainWindow teacherMainWindow;
    private AdminMainWindow adminMainWindow;

    private DrawMainStage(){
        ListViewBackground = new Background(
                new BackgroundFill(
                        Color.color(0.25,0.25,0.25,1),
                        CornerRadii.EMPTY,
                        Insets.EMPTY
                )
        );
        cellColor = new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                ListCell <String> cell = new ListCell<String>(){
                    @Override
                    protected void updateItem(String std, boolean empty) {
                        super.updateItem(std, empty);
                        setFont(Font.font("Arial", 16));
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

        try {
            TeacherStage = new Stage();
            TeacherFXMLLoader = new FXMLLoader();
            TeacherFXMLLoader.setLocation(getClass().getResource("TeacherMainWindow.fxml"));
            TeacherRoot = TeacherFXMLLoader.load();
            TeacherStage.setTitle("Attendance System - Teacher");
            TeacherStage.setScene(new Scene(TeacherRoot, 1280, 720));

            AdminStage = new Stage();
            AdminFXMLLoader = new FXMLLoader();
            AdminFXMLLoader.setLocation(getClass().getResource("AdminMainWindow.fxml"));
            AdminRoot = AdminFXMLLoader.load();
            AdminStage.setTitle("Attendance System - Admin");
            AdminStage.setScene(new Scene(AdminRoot, 1280, 720));
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void DrawTeacherMainStage(){
        TeacherStage.show();
    }

    public void DrawAdminMainStage(){
        AdminStage.show();
    }

    public void CloseTeacherMainStage(){
        TeacherStage.close();
        ManageLogIn();
    }

    public void CloseAdminMainStage(){
        AdminStage.close();
        ManageLogIn();
    }

    public static DrawMainStage getInstance() {
        return instance;
    }

    public void ManageLogIn(){
        boolean failed = false;             // Variable keeps track of invalid user name or password.
        boolean dialogCreation = false;     // Variable keeps track of unsuccessful dialog creation.
        String AccountType = "";

        // Keep doing unless the user enters a legit username and password.
        do{
            DrawWindows drawWindows = new DrawWindows();

            if (failed){    // If log in failed then show this
                dialogCreation = drawWindows.DrawDialog(
                        "LogInDialogue.fxml",
                        "Log In Dialogue",
                        "Invalid Username and/or password.",
                        null
                );
            } else {        // If log in for first time then show this
                dialogCreation = drawWindows.DrawDialog(
                        "LogInDialogue.fxml",
                        "Log In Dialogue",
                        "Use this Dialog to Log In",
                        null
                );
            }

            // If dialog was successfully created then proceed.
            if (dialogCreation){
                Optional<ButtonType> result = drawWindows.getDialog().showAndWait();

                // If the user presses the OK button perform the following.
                if (result.isPresent() && result.get() == ButtonType.OK){
                    // Get controller of FXML.
                    LogInDialogueController controller = drawWindows.getFxmlLoader().getController();

                    if (controller.processLogIn()){
                        AccountType = controller.getAccountType();

                        if (AccountType.equals("Teacher")) {
                            dbmsUserAccount = new XEbase(
                                    controller.getUserName(),
                                    controller.getPassWord()
                            );

                            if (dbmsUserAccount.connected){
                                DrawMainStage.getInstance().DrawTeacherMainStage();
                                teacherMainWindow = DrawMainStage.getInstance().TeacherFXMLLoader.getController();
                                teacherMainWindow.setDbmsUserAccount(dbmsUserAccount);
                                failed = false;
                            }
                            else {
                                failed = true;
                            }
                        }
                        else if (AccountType.equals("Admin")){
                            dbmsAdminAccount = new XEadmin(
                                    controller.getUserName(),
                                    controller.getPassWord()
                            );

                            if (dbmsAdminAccount.connected == true){
                                DrawMainStage.getInstance().DrawAdminMainStage();
                                adminMainWindow = DrawMainStage.getInstance().AdminFXMLLoader.getController();
                                adminMainWindow.setDbmsAdminAccount(dbmsAdminAccount);
                                failed = false;
                            }
                            else {
                                failed = true;
                            }
                        }
                    }
                    else {
                        failed = true;
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
        } while (failed == true);
    }
}
