import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LogInDialogueController {
    @FXML private TextField FXUserName;         // FXML User Name field
    @FXML private PasswordField FXPassword;     // FXML Password Field
    @FXML private ComboBox<String> FX_CB_AccountType;

    private String UserName;                    // Stores FXML text converted to string.
    private String PassWord;                    // Stores FXML text converted to string.

    public void initialize(){
        ObservableList<String> items = FXCollections.observableArrayList();
        items.add("Teacher");
        items.add("Department Head");
        FX_CB_AccountType.setItems(items);
        FX_CB_AccountType.getSelectionModel().selectFirst();
    }

    /**
     * A method that processes the text field information.
     * It converts the input in the text fields to string for opening a connection.
     * @return true if login info is properly filled in. False otherwise.
     */
    @FXML public Boolean processLogIn(){
        UserName = FXUserName.getText().trim();
        PassWord = FXPassword.getText();

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
