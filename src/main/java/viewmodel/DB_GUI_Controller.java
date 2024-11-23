package viewmodel;

import com.azure.storage.blob.BlobClient;
import dao.DbConnectivityClass;
import dao.StorageUploader;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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

import java.io.*;
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
    private Tooltip imageURLPopUp;
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
    Button addBtn;
    @FXML
    AnchorPane anchorPane;
    private Node stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            tv_id.setCellValueFactory(new PropertyValueFactory<>("id"));
            tv_fn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            tv_ln.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            tv_department.setCellValueFactory(new PropertyValueFactory<>("department"));
            tv_major.setCellValueFactory(new PropertyValueFactory<>("major"));
            tv_email.setCellValueFactory(new PropertyValueFactory<>("email"));
            tv.setItems(data); //registerPrivileges.setItems(FXCollections.observableArrayList(SignUpController.Privileges.values()));
            major.getItems().addAll(FXCollections.observableArrayList(DB_GUI_Controller.Major.values()));
            department.getItems().addAll(FXCollections.observableArrayList(DB_GUI_Controller.Department.values()));
            department.setValue("Department");
            major.setValue("Major");
            setPriviledgedActions();
            first_name.textProperty().addListener((observable, oldValue, newValue) -> {
                onFirstNameUpdated(newValue);
            });
            last_name.textProperty().addListener((observable, oldValue, newValue) -> {
                onLastNameUpdated(newValue);
            });
            email.textProperty().addListener((observable, oldValue, newValue) -> {
                onEmailUpdated(newValue);
            });
            imageURL.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
                imageURLPopUp.show(stage, e.getScreenX(), e.getScreenY());
            });
            major.valueProperty().addListener(new ChangeListener<String>() {
              @Override
              public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                  if (newValue.equals("Department")){
                      addBtn.setDisable(true);
                      //majorGood = false;
                  }
                  else{
                      //majorGood = true;
                      shouldEnable();
                  }
              }
          });
            department.valueProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if (newValue.equals("Department")){
                        addBtn.setDisable(true);
                        //departmentGood = false;
                    }
                    else{
                        //departmentGood = true;
                        shouldEnable();
                    }
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    boolean firstNameGood = false;
    boolean lastNameGood = false;
    boolean emailGood = false;
    boolean imageURLGood = false;
    //boolean majorGood = false;
    //boolean departmentGood = false;

    private void shouldEnable() {
        if (firstNameGood && lastNameGood && emailGood) {
            addBtn.setDisable(false);
        }
    }

    //Methods to listen for when the field is updated.
    public void onFirstNameUpdated(String newText) {
        Pattern p = Pattern.compile("\\w{2,25}");
        boolean tOne = newText.matches("\\w{2,25}");
        //System.out.println(newText + " is valid:" + tOne);
        if (!tOne) {
            //shoutBox.getChildren().add(new Label("Name must be 2-25 characters long"));
            if (!this.addBtn.isDisabled()) {
                addBtn.setDisable(true);
                firstNameGood = false;
            }
        } else {
            firstNameGood = true;
            shouldEnable();
            System.out.println("FirstNameGood");
        }
    }

    public void onLastNameUpdated(String newText) {
        Pattern p = Pattern.compile("\\w{2,25}");
        boolean tOne = newText.matches("\\w{2,25}");
        //System.out.println(newText + " is valid:" + tOne);
        if (!tOne) {
            //shoutBox.getChildren().add(new Label("Name must be 2-25 characters long"));
            if (!this.addBtn.isDisabled()) {
                addBtn.setDisable(true);
                lastNameGood = false;
            }
        } else {
            lastNameGood = true;
            shouldEnable();
            System.out.println("LastNameGood");
        }
    }

    public void onEmailUpdated(String newText) {
        Pattern p = Pattern.compile("\\w{2,25}");
        boolean tOne = newText.matches("\\w{2,25}");
        //System.out.println(newText + " is valid:" + tOne);
        if (!tOne) {
            //shoutBox.getChildren().add(new Label("Name must be 2-25 characters long"));
            if (!this.addBtn.isDisabled()) {
                addBtn.setDisable(true);
                emailGood = false;
            }
        } else {
            emailGood = true;
            shouldEnable();
            System.out.println("EmailGood");
        }
    }

    public void onImageURLUpdated(String newText) {
        Pattern p = Pattern.compile("\\w{2,25}");
        boolean tOne = newText.matches("\\w{2,25}");
        //System.out.println(newText + " is valid:" + tOne);
        if (!tOne) {
            //shoutBox.getChildren().add(new Label("Name must be 2-25 characters long"));
            if (!this.addBtn.isDisabled()) {
                addBtn.setDisable(true);
                imageURLGood = false;
            }
        } else {
            imageURLGood = true;
            shouldEnable();
            System.out.println("ImageURLGood");
        }
    }

    private void setPriviledgedActions() {
        System.out.println(curUser.getPassword());
        String privies = curUser.getPrivileges();
        System.out.println(privies);
        Pattern p = Pattern.compile("all");
        boolean tAll = privies.equals("all");
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
        } else {
            System.out.println("Warning! No Edit or Delete privileges");
            shoutBox.getChildren().add(new Label("Warning! No Edit or Delete privileges"));
        }
        if (curUser.getTheme()) {
            lightTheme();
        } else {
            darkTheme();
        }
    }

    @FXML
    protected boolean addNewRecord() {

        boolean failAdd = false;
        Pattern pat = Pattern.compile("\\w{2,25}");
        if (!first_name.getText().matches("\\w{2,25}")) {
            Label lbl = new Label("Name must be 2-25 characters long");
            lbl.setWrapText(true);
            shoutBox.getChildren().add(lbl);
            failAdd = true;
        }
        if (!last_name.getText().matches("\\w{2,25}")) {
            Label lbl = new Label("Name must be 2-25 characters long");
            lbl.setWrapText(true);
            shoutBox.getChildren().add(lbl);
            failAdd = true;
        }
        if (!email.getText().matches("[a-zA-Z]{3,15}@[a-z][.][.com|.edu]")) {
            Label lbl = new Label("Email must be a valid email address");
            lbl.setWrapText(true);
            shoutBox.getChildren().add(lbl);
            failAdd = true;
        }
        if (major.getValue().equals("Major")) {
            Label lbl = new Label("Must have a department");
            lbl.setWrapText(true);
            shoutBox.getChildren().add(lbl);
            failAdd = true;
        }
        if (department.getValue().equals("Department")) {
            Label lbl = new Label("Must have a department");
            lbl.setWrapText(true);
            shoutBox.getChildren().add(lbl);
            failAdd = true;
        }

        if (failAdd) {
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

    public void showImage() {
        File file = (new FileChooser()).showOpenDialog(menuBar.getScene().getWindow());
        if (file != null) {
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
        department.setValue("Department");
        major.setValue("Major");
        email.setText("");
        imageURL.setText("");
    }

    //Clears the usersession, as you are no longer logged in, and clears the form to prevent.
    @FXML
    protected void logOut(ActionEvent actionEvent) {
        try {
            UserSession.getInstance("n/a", "n/a").cleanUserSession();
            clearWholeTable();
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

    //Writes the data in the table to a csv file and saves it.
    @FXML
    protected void exportAsCSV() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File f = fileChooser.showSaveDialog(menuBar.getScene().getWindow());
        if (f == null) {
            System.out.println("File save canceled.");
            return; // Exit if no file is chosen
        }
        System.out.println(tv.getItems());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(f))) {
            for (Person p : tv.getItems()) {
                String line = p.getId() + "," + p.getFirstName() + "," + p.getLastName() + ",";
                System.out.println(line);
                writer.write(line);
                writer.newLine();

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Create an observable list with person data type out of a CSV file.
    @FXML
    protected void importCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open CSV File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File f = fileChooser.showOpenDialog(menuBar.getScene().getWindow());

        if (f == null) {
            System.out.println("No file selected.");
            return;
        }
        data.clear();
        tv.setItems(data);
        // Read and parse the file
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(","); // Split line into parts based on commas
                if (parts.length == 7) { // Ensure there are exactly 6 columns
                    int id = Integer.parseInt(parts[0].trim());
                    String firstName = parts[1].trim();
                    String lastName = parts[2].trim();
                    String department = parts[3].trim();
                    String major = parts[4].trim();
                    String email = parts[5].trim();
                    String imageURL = parts[6].trim();
                    // Create a new Person object and add it to the TableView
                    Person person = new Person(id, firstName, lastName, department, major, email, imageURL);
                    tv.getItems().add(person);
                } else {
                    System.err.println("Skipping malformed line: " + line);
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }

    //Edits a record by changing the record provided.
    @FXML
    protected void editRecord() {
        Person p = tv.getSelectionModel().getSelectedItem();
        int index = data.indexOf(p);
        Person p2 = new Person(index + 1, first_name.getText(), last_name.getText(), (String) department.getValue(),
                (String) major.getValue(), email.getText(), imageURL.getText());
        cnUtil.editUser(p.getId(), p2);
        data.remove(p);
        data.add(index, p2);
        tv.getSelectionModel().select(index);
        shoutBox.getChildren().add(new Label("Record edited successfully"));
    }

    //Removes a record from the table.
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
    protected void addRecord() {
        showSomeone();
    }

    @FXML
    protected void selectedItemTV(MouseEvent mouseEvent) {
        Person p = tv.getSelectionModel().getSelectedItem();
        first_name.setText(p.getFirstName());
        last_name.setText(p.getLastName());
        department.setValue(p.getDepartment());
        major.setValue(p.getMajor());
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
        dialogPane.setContent(new VBox(8, textField1, textField2, textField3, comboBox));
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

    //Mostly for fxml function call. Will clear the form and data.
    public void clearWholeTable() {
        tv.getItems().clear();
        data.clear();
    }

    //Creates an upload task and tries to upload to the database, an image.
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
                    imageURL.setText(file.toURI().toString());
                }

                return null;
            }
        };
    }

    //Enums with major and departments defined.
    private static enum Major {
        Business("Business"),
        CSC("CSC"),
        CPIS("CPIS"),
        EGL("English");

        private final String major;
        Major(String privilege) {
            this.major = privilege;
        }

        @Override
        public String toString() {
            return major; // Controls how the enum appears in the ChoiceBox
        }
    }

    private static enum Department {
        Business("Business"),
        CSC("CSC"),
        CPIS("CPIS"),
        EGL("English");

        private final String department;
        Department(String privilege) {
            this.department = privilege;
        }

        @Override
        public String toString() {
            return department; // Controls how the enum appears in the ChoiceBox
        }
    }

    public static class Results {

        String fname;
        String lname;
        Major major;

        public Results(String name, String date, Major major) {
            this.fname = name;
            this.lname = date;
            this.major = major;
        }
    }

}