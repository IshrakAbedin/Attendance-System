import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class InsertCourseDialog {
    @FXML private TextField FX_TF_TeacherName;
    @FXML private TextField FX_TF_SectionName;
    @FXML private TextField FX_TF_CourseName;

    public void initialize(){
        FX_TF_CourseName.onMouseClickedProperty().setValue(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FX_TF_CourseName.selectAll();
            }
        });
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

    public void setSectionName(String SectionName) {
        FX_TF_SectionName.setText(SectionName);
    }

    public String getTeacherName() {
        return FX_TF_TeacherName.getText().trim();
    }

    public String getSectionName() {
        return FX_TF_SectionName.getText().trim();
    }

    public String getCourseName() {
        return FX_TF_CourseName.getText().trim();
    }
}
