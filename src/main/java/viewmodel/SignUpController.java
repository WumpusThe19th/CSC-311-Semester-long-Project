package viewmodel;

import dao.DbConnectivityClass;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Client;
import service.UserSession;

import java.net.URL;
import java.util.ResourceBundle;

public class SignUpController {

    @FXML
    private TextField registerUserName;

    @FXML
    private PasswordField registerPassword;
    public enum Privileges {
        NONE("None"),
        READWRITE("Read/Write"),
        EXPORTIMPORT("Export/Import"),
        ALL("All");

        private final String privilegeLevel;
        Privileges(String privilege) {
            this.privilegeLevel = privilege;
        }

        @Override
        public String toString() {
            return privilegeLevel; // Controls how the enum appears in the ChoiceBox
        }
    }
    @FXML
    private ChoiceBox registerPrivileges;

    @FXML
    private TextField adminCode;

    private String adminCodeString = "X#324PLMTN";

    public void initialize(URL url, ResourceBundle resourceBundle) {
        registerPrivileges.setItems(FXCollections.observableArrayList(Privileges.values()));
    }

    //Creates a new client and adds it to the database in the clients table
    public void createNewAccount(ActionEvent actionEvent) {
        DbConnectivityClass cnUtil = DbConnectivityClass.getInstance();
        String userName = registerUserName.getText();
        String passWord = registerPassword.getText();
        String privileges;
        if (adminCode.equals(adminCodeString)){
            privileges = "all";
        }
        else {
            privileges = (String) registerPrivileges.getValue();
        }
        //String privileges = "all";
        System.out.println(privileges);
        Client curClient = new Client(userName, passWord, privileges, true, false);
        //System.out.println("Calling getInstance from SignUpController");
        //UserSession newClient = UserSession.getInstance(registerUserName.getText(), registerPassword.getText());
        boolean insertSuccessful = cnUtil.insertClient(curClient);
        if (insertSuccessful){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("user added successfully. please log in.");
            alert.showAndWait();}

    }

    public void goBack(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            //scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
