import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;


public class InsertStudentDialog {
    @FXML private TextField FX_TF_StudentID;
    @FXML private TextField FX_TF_StudentName;
    @FXML private TextField FX_TF_StudentAddress;
    @FXML private TextField FX_TF_StudentContact;
    @FXML private TextField FX_TF_TeacherName;
    @FXML private TextField FX_TF_SectionName;

    public void initialize(){
        FX_TF_StudentID.onMouseClickedProperty().setValue(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FX_TF_StudentID.selectAll();
            }
        });
        FX_TF_StudentName.onMouseClickedProperty().setValue(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FX_TF_StudentName.selectAll();
            }
        });
        FX_TF_StudentAddress.onMouseClickedProperty().setValue(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FX_TF_StudentAddress.selectAll();
            }
        });
        FX_TF_StudentContact.onMouseClickedProperty().setValue(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FX_TF_StudentContact.selectAll();
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

    public String getStudentID() {
        return FX_TF_StudentID.getText().trim();
    }

    public String getStudentName() {
        return FX_TF_StudentName.getText().trim();
    }

    public String getTeacherName() {
        return FX_TF_TeacherName.getText().trim();
    }

    public String getSectionName() {
        return FX_TF_SectionName.getText().trim();
    }

    public String getStudentAddress() {
        return FX_TF_StudentAddress.getText().trim();
    }

    public String getStudentContact() {
        return FX_TF_StudentContact.getText().trim();
    }
}
