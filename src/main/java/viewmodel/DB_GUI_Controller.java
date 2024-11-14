package viewmodel;

import com.azure.storage.blob.BlobClient;
import dao.DbConnectivityClass;
import dao.StorageUploader;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Person;
import service.MyLogger;
import service.UserSession;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.*;

public class DB_GUI_Controller implements Initializable {

    @FXML
    ProgressBar progressBar;

    StorageUploader store = new StorageUploader();
    @FXML
    TextField first_name, last_name, email, imageURL;
    @FXML
    ImageView img_view;

    @FXML
    ChoiceBox department, major;
    @FXML
    MenuBar menuBar;
    @FXML
    private TableView<Person> tv;
    @FXML
    private TableColumn<Person, Integer> tv_id;
    @FXML
    private TableColumn<Person, String> tv_fn, tv_ln, tv_department, tv_major, tv_email;
    @FXML
    private VBox shoutBox;
    private final DbConnectivityClass cnUtil = DbConnectivityClass.getInstance();
    private final ObservableList<Person> data = cnUtil.getUserData();
    UserSession curUser = UserSession.getInstance("bartsimpson", "bartsimpson");
    @FXML
    Button edtBtn;
    @FXML
    Button dtBtn;
    @FXML
    AnchorPane anchorPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            tv_id.setCellValueFactory(new PropertyValueFactory<>("id"));
            tv_fn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            tv_ln.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            tv_department.setCellValueFactory(new PropertyValueFactory<>("department"));
            tv_major.setCellValueFactory(new PropertyValueFactory<>("major"));
            tv_email.setCellValueFactory(new PropertyValueFactory<>("email"));
            tv.setItems(data);
            setPriviledgedActions();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setPriviledgedActions() {
        System.out.println(curUser.getPassword());
        String privies = curUser.getPrivileges();
        System.out.println(privies);
        Pattern p = Pattern.compile("all");
        boolean tAll = privies.matches("all");
        System.out.println(tAll);

        boolean tOne = curUser.getPrivileges().matches("rw");
        System.out.println(tOne);
        if (tOne || tAll) {
            System.out.println("We did a thing");
            anchorPane.addEventFilter(MouseEvent.MOUSE_CLICKED,
                    new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            System.out.println("We are doing it");
                            Person p = tv.getSelectionModel().getSelectedItem();
                            if (p != null) {
                                edtBtn.setDisable(false);
                                dtBtn.setDisable(false);
                            } else {
                                edtBtn.setDisable(true);
                                dtBtn.setDisable(true);
                            }
                        }
                    });
        }
        else{
            System.out.println("Warning! No Edit or Delete privileges");
            shoutBox.getChildren().add(new Label("Warning! No Edit or Delete privileges"));
        }
        if (curUser.getTheme()){lightTheme();} else {darkTheme();}
    }

    @FXML
    protected boolean addNewRecord() {

            boolean failAdd = false;
            Pattern pat = Pattern.compile("\\w{2,25}");
            if (!first_name.getText().matches("\\w{2,25}")){
                shoutBox.getChildren().add(new Label("Name must be 2-25 characters long"));
                failAdd = true;
            }
            if (!last_name.getText().matches("\\w{2,25}")){
                shoutBox.getChildren().add(new Label("Name must be 2-25 characters long"));
                failAdd = true;
            }
            if (!email.getText().matches("[a-zA-Z]{3,15}@[a-z][.][.com|.edu]")){
                shoutBox.getChildren().add(new Label("Email must be a valid email address"));
                failAdd = true;
            }

            if (failAdd){
                return failAdd;
            }
            Person p = new Person(first_name.getText(), last_name.getText(), (String) department.getValue(),
                    (String) major.getValue(), email.getText(), imageURL.getText());
            cnUtil.insertUser(p);
            cnUtil.retrieveId(p);
            p.setId(cnUtil.retrieveId(p));
            data.add(p);

            clearForm();
            shoutBox.getChildren().add(new Label("Record added successfully"));
            return true;
    }

    public void showImage(){
        File file = (new FileChooser()).showOpenDialog(menuBar.getScene().getWindow());
                if (file != null){
                    img_view.setImage(new Image(file.toURI().toString()));
                    Task<Void> uploadTask = createUploadTask(file, progressBar);
                    progressBar.progressProperty().bind(uploadTask.progressProperty());
                    new Thread(uploadTask).start();
                }
    }

    @FXML
    protected void clearForm() {
        first_name.setText("");
        last_name.setText("");
        department.setValue("department");
        major.setValue("major");
        email.setText("");
        imageURL.setText("");
    }

    @FXML
    protected void logOut(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").getFile());
            Stage window = (Stage) menuBar.getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void closeApplication() {
        System.exit(0);
    }

    @FXML
    protected void displayAbout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/about.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root, 600, 500);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Utilize a stream to .joining each element together and write the result into a csv file
    @FXML
    protected void exportAsCSV(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File f = fileChooser.showSaveDialog(menuBar.getScene().getWindow());

        System.out.println(tv.getItems());
        for (Person p: tv.getItems()){
            String line = p.getId() + "," + p.getFirstName() + "," + p.getLastName() + ",";

        }
    }
    @FXML
    protected void editRecord() {
        Person p = tv.getSelectionModel().getSelectedItem();
        int index = data.indexOf(p);
        Person p2 = new Person(index + 1, first_name.getText(), last_name.getText(), (String) department.getSelectedItem(),
                (String) major.getSelectedItem(), email.getText(),  imageURL.getText());
        cnUtil.editUser(p.getId(), p2);
        data.remove(p);
        data.add(index, p2);
        tv.getSelectionModel().select(index);
        shoutBox.getChildren().add(new Label("Record edited successfully"));
    }

    @FXML
    protected void deleteRecord() {
        Person p = tv.getSelectionModel().getSelectedItem();
        int index = data.indexOf(p);
        cnUtil.deleteRecord(p);
        data.remove(index);
        tv.getSelectionModel().select(index);
        shoutBox.getChildren().add(new Label("Record deleted successfully"));
    }

    @FXML
    protected void showImage() {
        File file = (new FileChooser()).showOpenDialog(img_view.getScene().getWindow());
        if (file != null) {
            img_view.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    protected void addRecord() {
        showSomeone();
    }

    @FXML
    protected void selectedItemTV(MouseEvent mouseEvent) {
        Person p = tv.getSelectionModel().getSelectedItem();
        first_name.setText(p.getFirstName());
        last_name.setText(p.getLastName());
        department.setSelectedItem(p.getDepartment());
        major.setSelectedItem(p.getMajor());
        email.setText(p.getEmail());
        imageURL.setText(p.getImageURL());
    }

    public void lightTheme() {
        System.out.println("We changed to light theme");
        try {
            anchorPane.getStylesheets().remove("darkTheme.css");
            anchorPane.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());

            System.out.println("light " + anchorPane.getStylesheets());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void darkTheme() {
        System.out.println("We changed to dark theme");
        try {
            anchorPane.getStylesheets().remove("lightTheme.css");
            anchorPane.getStylesheets().add(getClass().getResource("/css/darkTheme.css").toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSomeone() {
        Dialog<Results> dialog = new Dialog<>();
        dialog.setTitle("New User");
        dialog.setHeaderText("Please specify…");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField textField1 = new TextField("Name");
        TextField textField2 = new TextField("Last Name");
        TextField textField3 = new TextField("Email ");
        ObservableList<Major> options =
                FXCollections.observableArrayList(Major.values());
        ComboBox<Major> comboBox = new ComboBox<>(options);
        comboBox.getSelectionModel().selectFirst();
        dialogPane.setContent(new VBox(8, textField1, textField2,textField3, comboBox));
        Platform.runLater(textField1::requestFocus);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new Results(textField1.getText(),
                        textField2.getText(), comboBox.getValue());
            }
            return null;
        });
        Optional<Results> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((Results results) -> {
            MyLogger.makeLog(
                    results.fname + " " + results.lname + " " + results.major);
        });
    }

    private Task<Void> createUploadTask(File file, ProgressBar progressBar) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                BlobClient blobClient = store.getContainerClient().getBlobClient(file.getName());
                long fileSize = Files.size(file.toPath());
                long uploadedBytes = 0;

                try (FileInputStream fileInputStream = new FileInputStream(file);
                     OutputStream blobOutputStream = blobClient.getBlockBlobClient().getBlobOutputStream()) {

                    byte[] buffer = new byte[1024 * 1024]; // 1 MB buffer size
                    int bytesRead;

                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        blobOutputStream.write(buffer, 0, bytesRead);
                        uploadedBytes += bytesRead;

                        // Calculate and update progress as a percentage
                        int progress = (int) ((double) uploadedBytes / fileSize * 100);
                        updateProgress(progress, 100);
                    }
                }

                return null;
            }
        };
    }

    private static enum Major {Business, CSC, CPIS}

    private static class Results {

        String fname;
        String lname;
        Major major;

        public Results(String name, String date, Major venue) {
            this.fname = name;
            this.lname = date;
            this.major = venue;
        }
    }

}