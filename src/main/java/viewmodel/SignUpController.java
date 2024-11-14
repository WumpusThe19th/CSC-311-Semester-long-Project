package viewmodel;

import dao.DbConnectivityClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.UserSession;

public class SignUpController {

    @FXML
    private TextField registerUserName;

    @FXML
    private PasswordField registerPassword;

    @FXML
    private ComboBox registerPrivileges;

    public void createNewAccount(ActionEvent actionEvent) {
        DbConnectivityClass cnUtil = DbConnectivityClass.getInstance();
        String userName = registerUserName.getText();
        String passWord = registerPassword.getText();
        String privileges = registerPrivileges.getId();
        System.out.println(privileges);
        //System.out.println("Calling getInstance from SignUpController");
        //UserSession newClient = UserSession.getInstance(registerUserName.getText(), registerPassword.getText());
        //cnUtil.insertClient(newClient);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Info for the user. Message goes here");
        alert.showAndWait();

    }

    public void goBack(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
