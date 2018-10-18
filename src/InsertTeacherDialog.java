import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class InsertTeacherDialog {
    @FXML private TextField FX_TF_TeacherName;
    @FXML private PasswordField FX_PF_Password;

    public void initialize(){
        FX_PF_Password.onMouseClickedProperty().setValue(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FX_PF_Password.selectAll();
            }
        });
        FX_TF_TeacherName.onMouseClickedProperty().setValue(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FX_TF_TeacherName.selectAll();
            }
        });
    }

    public String getTeacherName() {
        return FX_TF_TeacherName.getText().trim();
    }

    public String getPassword() {
        return FX_PF_Password.getText().trim();
    }
}
