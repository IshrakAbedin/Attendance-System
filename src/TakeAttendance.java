import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;

import java.util.Comparator;

public class TakeAttendance {
    @FXML private Button FX_B_Select;
    @FXML private Button FX_B_SelectAll;
    @FXML private Button FX_B_Deselect;
    @FXML private Button FX_B_DeselectAll;
    @FXML private ListView<String> FX_LV_PresentStudents;
    @FXML private ListView<String> FX_LV_AbsentStudents;

    private ObservableList<String> SelectedAbsentStudents;
    private ObservableList<String> SelectedPresentStudents;

    public TakeAttendance(){
        SelectedAbsentStudents = FXCollections.observableArrayList();
        SelectedPresentStudents = FXCollections.observableArrayList();
    }

    public void initialize(){
        FX_B_Select.setText(">");
        FX_B_Deselect.setText("<");
        FX_B_SelectAll.setText(">>");
        FX_B_DeselectAll.setText("<<");

        Background ListViewBackground = new Background(
                new BackgroundFill(
                        Color.color(0.25,0.25,0.25,1),
                        CornerRadii.EMPTY,
                        Insets.EMPTY
                )
        );

        Callback<ListView<String>, ListCell<String>> cellColor = new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                ListCell <String> cell = new ListCell<String>(){
                    @Override
                    protected void updateItem(String std, boolean empty) {
                        super.updateItem(std, empty);
                        setFont(Font.font("Arial", 16));
                        setTextFill(Color.color(0, 1, 0.86, 1));

                        if (empty){
                            setText("- - -");
                        } else {
                            setText(std);
                        }
                    }
                };

                // Setting cell background color
                cell.backgroundProperty().setValue(ListViewBackground);

                return cell;
            }
        };

        FX_LV_PresentStudents.setBackground(ListViewBackground);
        FX_LV_PresentStudents.setCellFactory(cellColor);
        FX_LV_PresentStudents.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        FX_LV_PresentStudents.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SelectedPresentStudents = FX_LV_PresentStudents.getSelectionModel().getSelectedItems();
            }
        });

        FX_LV_AbsentStudents.setBackground(ListViewBackground);
        FX_LV_AbsentStudents.setCellFactory(cellColor);
        FX_LV_AbsentStudents.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        FX_LV_AbsentStudents.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SelectedAbsentStudents = FX_LV_AbsentStudents.getSelectionModel().getSelectedItems();
            }
        });
    }

    public void setFX_LV_AbsentStudents(ObservableList<String> AbsentStudents){
        FX_LV_AbsentStudents.setItems(AbsentStudents);
    }

    public void handleGivePresent(){
        FX_LV_PresentStudents.getItems().addAll(SelectedAbsentStudents);
        FX_LV_AbsentStudents.getItems().removeAll(SelectedAbsentStudents);
    }

    public void handleGiveAbsent(){
        FX_LV_AbsentStudents.getItems().addAll(SelectedPresentStudents);
        FX_LV_PresentStudents.getItems().removeAll(SelectedPresentStudents);
    }

    public void handleGivePresentToAll(){
        FX_LV_PresentStudents.getItems().addAll(FX_LV_AbsentStudents.getItems());
        FX_LV_AbsentStudents.getItems().clear();
    }

    public void handleGiveAbsentToAll(){
        FX_LV_AbsentStudents.getItems().addAll(FX_LV_PresentStudents.getItems());
        FX_LV_PresentStudents.getItems().clear();
    }

    public ObservableList<String> getFX_LV_PresentStudents() {
        return FX_LV_PresentStudents.getItems();
    }
}
