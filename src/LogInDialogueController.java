import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class LogInDialogueController {
    @FXML private TextField FX_TF_UserName;         // FXML User Name field
    @FXML private PasswordField FX_TF_Password;     // FXML Password Field
    @FXML private ComboBox<String> FX_CB_AccountType;

    private String UserName;                    // Stores FXML text converted to string.
    private String PassWord;                    // Stores FXML text converted to string.

    public void initialize(){
        ObservableList<String> items = FXCollections.observableArrayList();
        items.add("Admin");
        items.add("Teacher");
        FX_TF_UserName.onMouseClickedProperty().setValue(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FX_TF_UserName.selectAll();
            }
        });
        FX_TF_Password.onMouseClickedProperty().setValue(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FX_TF_Password.selectAll();
            }
        });
        FX_CB_AccountType.setItems(items);
        FX_CB_AccountType.getSelectionModel().selectFirst();
    }

    /**
     * A method that processes the text field information.
     * It converts the input in the text fields to string for opening a connection.
     * @return true if login info is properly filled in. False otherwise.
     */
    @FXML public Boolean processLogIn(){
        UserName = FX_TF_UserName.getText().trim();
        PassWord = FX_TF_Password.getText().trim();

        // User Name and password fields can not be empty.
        if (UserName.length() > 0 && PassWord.length() > 0){
            return true;
        }
        else {
            System.out.println("User Name and/or Password field is empty!");
            return false;
        }
    }

    /**
     * Gets the user name inputted.
     * @return User Name.
     */
    public String getUserName() {
        return UserName;
    }

    /**
     * Gets the password inputted.
     * @return Password.
     */
    public String getPassWord() {
        return PassWord;
    }

    public String getAccountType(){
        return FX_CB_AccountType.getSelectionModel().getSelectedItem();
    }
}
