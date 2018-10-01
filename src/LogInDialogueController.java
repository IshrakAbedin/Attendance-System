import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LogInDialogueController {
    @FXML private TextField FXUserName;         // FXML User Name field
    @FXML private PasswordField FXPassword;     // FXML Password Field
    @FXML private TextField FXSectionName;      // FXML Section Name Field
    @FXML private TextField FXClassName;        // FXML Class Name Field

    private String UserName;                    // Stores FXML text converted to string.
    private String PassWord;                    // Stores FXML text converted to string.
    private String SectionName;                 // Stores FXML text converted to string.
    private String ClassName;                   // Stores FXML text converted to string.

    /**
     * A method that processes the text field information.
     * It converts the input in the text fields to string for opening a connection.
     * @return true if login info is properly filled in. False otherwise.
     */
    @FXML public Boolean processLogIn(){
        UserName = FXUserName.getText().trim();
        PassWord = FXPassword.getText();
        SectionName = FXSectionName.getText().trim();
        ClassName = FXClassName.getText().trim();

//        System.out.println("User Name = " + UserName);
//        System.out.println("Password = " + PassWord);
//        System.out.println("Section Name = " + SectionName);
//        System.out.println("Class Name = " + ClassName);

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

    /**
     * Gets the section name inputted.
     * @return Section Name.
     */
    public String getSectionName() {
        return SectionName;
    }

    /**
     * Gets the class name inputted.
     * @return Class Name.
     */
    public String getClassName() {
        return ClassName;
    }
}
