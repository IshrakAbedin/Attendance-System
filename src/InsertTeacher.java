import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class InsertTeacher {
    @FXML private TextField FX_TF_TeacherName;
    @FXML private PasswordField FX_PF_Password;

    public String getTeacherName() {
        return FX_TF_TeacherName.getText().trim();
    }

    public String getPassword() {
        return FX_PF_Password.getText().trim();
    }
}
