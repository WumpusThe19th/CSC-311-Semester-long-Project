package viewmodel;

import dao.DbConnectivityClass;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.Client;

import java.util.HashMap;

public class SpalshScreenController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onButtonClick() {

        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void initialize(){
        //Set up files and classes
        DbConnectivityClass instance = DbConnectivityClass.getInstance();
        HashMap<String, Client> clients = instance.getClientData(true);
    }
}