import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SearchDialog {
    @FXML private TextField FX_TF_SID;
    @FXML private TextField FX_TF_Course;
    @FXML private Label FX_L_SID;
    @FXML private Label FX_L_Name;
    @FXML private Label FX_L_Address;
    @FXML private Label FX_L_ContactNumber;
    @FXML private Label FX_L_AttendanceCount;
    @FXML private Label FX_L_AttendancePercentage;

    private XEbase Account;
    private String SID;
    private String Class;

    public void initialize(){
        FX_TF_SID.onMouseClickedProperty().setValue(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FX_TF_SID.selectAll();
            }
        });
        FX_TF_Course.onMouseClickedProperty().setValue(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FX_TF_Course.selectAll();
            }
        });
    }

    public void process(XEbase account){
        Account = account;
        if (Account == null){
            System.out.println("ACCOUNT INPUTTED IS NULL");
        }
    }

    @FXML private void handleSearch(){
        SID = FX_TF_SID.getText().trim();
        Class = FX_TF_Course.getText().trim();

        System.out.println(Class);

        if (Account != null){
            if (SID != null && Class != null) {
                try {
                    Account.setClassname(Class);

                    ResultSet rs = Account.getExtendedAttendanceBySIDList(SID);

                    if (rs != null) {
                        while (rs.next()) {
                            FX_L_SID.setText(rs.getString(1));
                            FX_L_Name.setText(rs.getString(2));
                            FX_L_Address.setText(rs.getString(3));
                            FX_L_ContactNumber.setText(rs.getString(4));
                            FX_L_AttendanceCount.setText(rs.getString(5));
                            FX_L_AttendancePercentage.setText(rs.getString(6));
                        }
                    }
                    else {
                        System.out.println("Result set is null!");
                    }

                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            System.out.println("NULL Account!");
        }
    }
}
