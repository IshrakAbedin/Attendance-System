import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.Optional;

public class DrawWindows {
    private Dialog<ButtonType> Dialog;
    private FXMLLoader fxmlLoader;
    private DialogPane dialogPane;
    private Background DialogBackground;
    private String DialogStyleCSS;
    private String FontName;
    private Alert alert;
    private Optional<ButtonType> AlertResult;


    public DrawWindows(){
        FontName = "Arial";
        DialogStyleCSS = "dialog.css";
        DialogBackground = new Background(
                new BackgroundFill(
                        Color.GRAY,
                        CornerRadii.EMPTY,
                        Insets.EMPTY
                )
        );
    }

    /**
     * Create the log in dialog.
     * @param DialogName Name of the dialog.
     * @param TitleText Title of the dialog.
     * @param HeaderText Header text of the dialog.
     * @param FX_BorderPane The border pane where to get scene from
     * @return true if successfully created. False otherwise.
     */
    public boolean DrawDialog(String DialogName, String TitleText, String HeaderText, BorderPane FX_BorderPane){
        try{
            Dialog = new Dialog<>();
            fxmlLoader = new FXMLLoader();
            if (FX_BorderPane != null)
                Dialog.initOwner(FX_BorderPane.getScene().getWindow());
            Dialog.setTitle(TitleText);
            Dialog.setHeaderText(HeaderText);
            dialogPane = Dialog.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource(DialogStyleCSS).toExternalForm());
            fxmlLoader.setLocation(getClass().getResource(DialogName));
            Dialog.getDialogPane().setContent(fxmlLoader.load());
            Dialog.getDialogPane().setBackground(DialogBackground);
            Dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            Dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        } catch(IOException e){
            System.out.println("Couldn't load dialogue " + DialogName);
            e.printStackTrace();
            return false;
        }

        return true;
    }


    /**
     * Displays an error message when trying to use the software without logging in.
     * @param TitleText The title of the warning
     * @param HeaderText the header of the warning
     * @param ContentText the context of the warning
     * @param Type Type of issue generating the command.
     */
    public void DrawAlert(String TitleText, String HeaderText, String ContentText, String Type){
        String type = Type.toUpperCase();

        if (type.equals("ERROR"))                   alert = new Alert(Alert.AlertType.ERROR);
        else if (type.equals("CONFIRMATION"))       alert = new Alert(Alert.AlertType.CONFIRMATION);
        else if (type.equals("WARNING"))            alert = new Alert(Alert.AlertType.WARNING);
        else if (type.equals("INFORMATION"))        alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(TitleText);
        alert.setHeaderText(HeaderText);
        alert.setContentText(ContentText);
        dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource(DialogStyleCSS).toExternalForm());
        AlertResult = alert.showAndWait();
    }


    public javafx.scene.control.Dialog<ButtonType> getDialog() {
        return Dialog;
    }

    public FXMLLoader getFxmlLoader() {
        return fxmlLoader;
    }

    public DialogPane getDialogPane() {
        return dialogPane;
    }

    public Background getDialogBackground() {
        return DialogBackground;
    }

    public String getDialogStyleCSS() {
        return DialogStyleCSS;
    }

    public String getFontName() {
        return FontName;
    }

    public Alert getAlert() {
        return alert;
    }

    public Optional<ButtonType> getAlertResult() {
        return AlertResult;
    }
}
