package viewmodel;

import dao.DbConnectivityClass;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Client;
import service.UserSession;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class MainApplication extends Application {

    private static Scene scene;
    private static DbConnectivityClass cnUtil;
    private Stage primaryStage;

    //Debug variable that skips the login phase
    private Boolean skipFade = false;
    public static void main(String[] args) {
        cnUtil = new DbConnectivityClass();
        launch(args);

    }

    public void start(Stage primaryStage) {
        Image icon = new Image(getClass().getResourceAsStream("/images/DollarClouddatabase.png"));
        this.primaryStage = primaryStage;
        this.primaryStage.setResizable(false);
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("FSC CSC311 _ Database Project");
        DbConnectivityClass.getInstance();
        showScene1();
    }

    //If fade, boot to the log in. Otherwise, boot to the interface.
    private boolean showScene1() {
        try {
            if (skipFade) {
                UserSession curUser = UserSession.getInstance("debugGuest", "longtermorganfailure20092020", "all",true);
                Parent root = FXMLLoader.load(getClass().getResource("/view/db_interface_gui.fxml"));
                Scene scene = new Scene(root, 900, 600);
                //scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
                primaryStage.setScene(scene);
                primaryStage.show();
                 //changeScene();
            } else{
                Parent root = FXMLLoader.load(getClass().getResource("/view/splashscreen.fxml"));
                Scene scene = new Scene(root, 900, 600);
                //scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
                primaryStage.setScene(scene);
                primaryStage.show();
                DbConnectivityClass instance = DbConnectivityClass.getInstance();
                HashMap<String, Client> clients = instance.getClientData(true);
                for (String key : clients.keySet()) {
                    System.out.println("Key: " + key + ", Value: " + clients.get(key) + " isCurrentUser: " + clients.get(key).isItCurrentUser() );

                    if (clients.get(key).isItCurrentUser()){
                        System.out.println("We have a previous user, and should boot straight to the application screen");
                        UserSession inst = UserSession.getInstance(clients.get(key));
                        changeScene("db_interface_gui");
                        return true;
                    }
                }
                changeScene("login");

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //Change to login. Finish the fade.
    public void changeScene(String page) {
        try {
            Parent newRoot = FXMLLoader.load(getClass().getResource("/view/"+page+".fxml").toURI().toURL());
            Scene currentScene = primaryStage.getScene();
            Parent currentRoot = currentScene.getRoot();
            //currentScene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(3), currentRoot);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> {
                Scene newScene = new Scene(newRoot, 900, 600);
                primaryStage.setScene(newScene);
                primaryStage.show();
            });
            fadeOut.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}