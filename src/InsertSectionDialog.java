import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class InsertSectionDialog {
    @FXML private TextField FX_TF_TeacherName;
    @FXML private TextField FX_TF_SectionName;

    public void initialize(){
        FX_TF_TeacherName.onMouseClickedProperty().setValue(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FX_TF_TeacherName.selectAll();
            }
        });
        FX_TF_SectionName.onMouseClickedProperty().setValue(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FX_TF_SectionName.selectAll();
            }
        });
    }

    public void setTeacherName(String TeacherName) {
        FX_TF_TeacherName.setText(TeacherName);
    }

    public String getTeacherName() {
        return FX_TF_TeacherName.getText().trim();
    }

    public String getSectionName() {
        return FX_TF_SectionName.getText().trim();
    }
}
