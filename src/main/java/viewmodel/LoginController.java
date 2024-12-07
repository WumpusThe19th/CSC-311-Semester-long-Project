package viewmodel;

import dao.DbConnectivityClass;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Client;
import service.UserSession;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;


public class LoginController {

    public boolean validateLogin = false;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label userNotFound;

    @FXML
    private Label passNotFound;

    @FXML
    private GridPane rootpane;

    private final ReentrantLock lock = new ReentrantLock();
    public void initialize() {
        rootpane.setBackground(new Background(
                        createImage("https://edencoding.com/wp-content/uploads/2021/03/layer_06_1920x1080.png"),
                        null,
                        null,
                        null,
                        null,
                        null
                )
        );


        rootpane.setOpacity(0);
        FadeTransition fadeOut2 = new FadeTransition(Duration.seconds(10), rootpane);
        fadeOut2.setFromValue(0);
        fadeOut2.setToValue(1);
        fadeOut2.play();
    }
    private static BackgroundImage createImage(String url) {
        return new BackgroundImage(
                new Image(url),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT,
                new BackgroundPosition(Side.LEFT, 0, true, Side.BOTTOM, 0, true),
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true));
    }
    @FXML
    public boolean login(ActionEvent actionEvent) {
        DbConnectivityClass instance = DbConnectivityClass.getInstance();
        HashMap<String, Client> clients = instance.getClientData(false);
        if (!clients.containsKey(usernameTextField.getText())) {
            userNotFound.setVisible(true);
            return false;
        }
        if (!clients.get(usernameTextField.getText()).getPassword().equals(passwordField.getText())){
            //System.out.println("Pass matches this record");
            passNotFound.setVisible(true);
            return false;
        }
        try {
            lock.lock();
            clients.get(usernameTextField.getText()).setCurrentUser(true);
            instance.editClient(usernameTextField.getText(), clients.get(usernameTextField.getText()));
            System.out.println(clients.get(usernameTextField.getText()).isItCurrentUser());
            UserSession inst = UserSession.getInstance(clients.get(usernameTextField.getText()));
            Parent root = FXMLLoader.load(getClass().getResource("/view/db_interface_gui.fxml"));
            Scene scene = new Scene(root, 900, 600);
            //Tweak this
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
        return false;
    }

    public void signUp(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/signUp.fxml"));
            Scene scene = new Scene(root, 900, 600);
            //Tweak this.
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
