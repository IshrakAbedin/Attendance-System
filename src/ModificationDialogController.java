import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;

import java.time.LocalDate;

public class ModificationDialogController {
    @FXML private DatePicker FXDatePicker;

    public void initialize(){
        FXDatePicker.setValue(LocalDate.now());
    }

    public LocalDate getDate(){
        return FXDatePicker.getValue();
    }
}
