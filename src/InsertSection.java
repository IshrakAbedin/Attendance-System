import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class InsertSection {
    @FXML private TextField FX_TF_TeacherName;
    @FXML private TextField FX_TF_SectionName;

    public String getTeacherName() {
        return FX_TF_TeacherName.getText().trim();
    }

    public String getSectionName() {
        return FX_TF_SectionName.getText().trim();
    }
}
