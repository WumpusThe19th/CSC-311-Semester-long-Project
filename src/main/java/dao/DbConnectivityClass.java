package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Client;
import model.Person;
import service.MyLogger;
import service.UserSession;

import java.sql.*;
import java.util.HashMap;

public class DbConnectivityClass {
    final static String DB_NAME="wumpus";
        MyLogger lg= new MyLogger();
        final static String SQL_SERVER_URL = "jdbc:mysql://kuczcsc311server.mysql.database.azure.com";//update this server name
        final static String DB_URL = SQL_SERVER_URL + "/" + DB_NAME;//update this database name
        final static String USERNAME = "WumpusThe19th";// update this username
        final static String PASSWORD = "Longtermorganfailure20092020";// update this password
        private static DbConnectivityClass instance;
        private String connectionKey = "DefaultEndpointsProtocol=https;AccountName=wumpblob;AccountKey=T9vwwFRDCTIPgKgWzT9SDc+yepjoeBFoHD82xEfM24GjN61CK+dAyBsbk+YCXtio3AYj96vkAYKh+AStxq0Wrg==;EndpointSuffix=core.windows.net";
        public static DbConnectivityClass getInstance(){
            if (instance == null){
                instance = new DbConnectivityClass();
            }
            return instance;
        }

        private final ObservableList<Person> data = FXCollections.observableArrayList();
        private final HashMap<String, Client> clients = new HashMap<String, Client>();
        //I believe this whole file will eventually need a rework to add UserSessions
        // Method to retrieve all data from the database and store it into an observable list to use in the GUI tableview.



        public ObservableList<Person> getUserData() {
            connectToDatabase();
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                String sql = "SELECT * FROM users ";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (!resultSet.isBeforeFirst()) {
                    lg.makeLog("No data");
                }
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String first_name = resultSet.getString("first_name");
                    String last_name = resultSet.getString("last_name");
                    String department = resultSet.getString("department");
                    String major = resultSet.getString("major");
                    String email = resultSet.getString("email");
                    String imageURL = resultSet.getString("imageURL");
                    data.add(new Person(id, first_name, last_name, department, major, email, imageURL));
                }
                preparedStatement.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return data;
        }

        public HashMap<String, Client> getClientData(Boolean shouldFetch){
            //System.out.println("getClientData");
            if (!shouldFetch){
                System.out.println("Clients is already initialized, and we don't need to re-fetch it");
                return clients;
            }
            System.out.println("We are grabbing Clients from the database");
            connectToDatabase();
                try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT * FROM clients ";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.isBeforeFirst()) {
                lg.makeLog("No data");
            }
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                System.out.println(username);
                String password = resultSet.getString("password");
                System.out.println(password);
                String privileges = resultSet.getString("privileges");
                System.out.println(privileges);
                boolean lightTheme = resultSet.getBoolean("lighttheme");
                System.out.println(lightTheme);
                boolean isCurrentUser = resultSet.getBoolean("isCurrentUser");
                clients.put(username, new Client(username, password, privileges, lightTheme, isCurrentUser));
            }
            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
                return clients;
    }


        public boolean connectToDatabase() {
            boolean hasRegistredUsers = false;
            boolean hasRegistredClients = false;
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");

                //First, connect to MYSQL server and create the database if not created
                Connection conn = DriverManager.getConnection(SQL_SERVER_URL, USERNAME, PASSWORD);
                Statement statement = conn.createStatement();
                statement.executeUpdate("CREATE DATABASE IF NOT EXISTS "+DB_NAME+"");
                statement.close();
                conn.close();

                //Second, connect to the database and create the table "users" if cot created
                conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                statement = conn.createStatement();
                String sql = "CREATE TABLE IF NOT EXISTS users (" + "id INT( 10 ) NOT NULL PRIMARY KEY AUTO_INCREMENT,"
                        + "first_name VARCHAR(200) NOT NULL," + "last_name VARCHAR(200) NOT NULL,"
                        + "department VARCHAR(200),"
                        + "major VARCHAR(200),"
                        + "email VARCHAR(200) NOT NULL UNIQUE,"
                        + "imageURL VARCHAR(200))";
                statement.executeUpdate(sql);

                //check if we have users in the table users
                statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM users");

                statement = conn.createStatement();
                String sql2 = "CREATE TABLE IF NOT EXISTS clients (" +
                         "username VARCHAR(200) NOT NULL," + "password VARCHAR(200) NOT NULL," + "privileges VARCHAR(200) NOT NULL," + "lighttheme BOOLEAN NOT NULL)";
                statement.executeUpdate(sql2);
                //statement = conn.createStatement();
                //String sqlImSoSorryIForgot = "ALTER TABLE clients ADD COLUMN isCurrentUser BOOLEAN;";
                //statement.executeUpdate(sqlImSoSorryIForgot);
                //System.out.println("Something");
                //check if we have users in the table users
                statement = conn.createStatement();
                ResultSet resultSet2 = statement.executeQuery("SELECT COUNT(*) FROM clients");

                if (resultSet.next()) {
                    int numUsers = resultSet.getInt(1);
                    if (numUsers > 0) {
                        hasRegistredUsers = true;
                    }
                }

                if (resultSet2.next()) {
                    int numUsers = resultSet.getInt(1);
                    if (numUsers > 0) {
                        hasRegistredClients = true;
                    }
                }
                String dsql = "DESCRIBE clients";
                PreparedStatement pStatement = conn.prepareStatement(dsql);
                ResultSet rs = pStatement.executeQuery(dsql);
                while (rs.next()) {
                    String field = rs.getString("Field");
                    String type = rs.getString("Type");
                    String isNullable = rs.getString("Null");
                    String key = rs.getString("Key");
                    String defaultValue = rs.getString("Default");
                    String extra = rs.getString("Extra");

                    System.out.printf("%s\t%s\t%s\t%s\t%s\t%s%n",
                            field, type, isNullable, key, defaultValue, extra);
                }
                //System.out.println(hasRegistredClients);
                statement.close();
                conn.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return hasRegistredUsers;
        }

        public void queryUserByLastName(String name) {
            connectToDatabase();
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                String sql = "SELECT * FROM users WHERE last_name = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, name);

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String first_name = resultSet.getString("first_name");
                    String last_name = resultSet.getString("last_name");
                    String major = resultSet.getString("major");
                    String department = resultSet.getString("department");

                    lg.makeLog("ID: " + id + ", Name: " + first_name + " " + last_name + " "
                            + ", Major: " + major + ", Department: " + department);
                }
                preparedStatement.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    public void queryClientsByUserName(String name) {
        connectToDatabase();
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT * FROM clients WHERE username = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                String username = resultSet.getString("username");
                lg.makeLog("Username: " + username);
            }

            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Well. Something happened");
    }
        public void listAllUsers() {
            connectToDatabase();
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                String sql = "SELECT * FROM users ";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String first_name = resultSet.getString("first_name");
                    String last_name = resultSet.getString("last_name");
                    String department = resultSet.getString("department");
                    String major = resultSet.getString("major");
                    String email = resultSet.getString("email");

                    lg.makeLog("ID: " + id + ", Name: " + first_name + " " + last_name + " "
                            + ", Department: " + department + ", Major: " + major + ", Email: " + email);
                }

                preparedStatement.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void insertUser(Person person) {
            connectToDatabase();
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                String sql = "INSERT INTO users (first_name, last_name, department, major, email, imageURL) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, person.getFirstName());
                preparedStatement.setString(2, person.getLastName());
                preparedStatement.setString(3, person.getDepartment());
                preparedStatement.setString(4, person.getMajor());
                preparedStatement.setString(5, person.getEmail());
                preparedStatement.setString(6, person.getImageURL());
                int row = preparedStatement.executeUpdate();
                if (row > 0) {
                    lg.makeLog("A new user was inserted successfully.");
                }
                preparedStatement.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    public boolean insertClient(Client user) {
        connectToDatabase();
        try {
            System.out.println("We are trying to add your client");
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "INSERT INTO clients (username, password, privileges, lighttheme) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getPrivileges());
            System.out.println(String.valueOf(user.isLightTheme()));
            preparedStatement.setBoolean(4, true);

            int row = preparedStatement.executeUpdate();
            System.out.println("We are trying");

            preparedStatement.close();
            conn.close();
            if (row > 0) {
                lg.makeLog("A new client was inserted successfully.");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public void editClient(String id, Client p) {
        connectToDatabase();
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            //Ensure
            String resetPhrase = "UPDATE clients SET IsCurrentUser = false";
            PreparedStatement resetStatement = conn.prepareStatement(resetPhrase);
            resetStatement.executeUpdate();
            String sql = "UPDATE clients SET username=?, password=?, privileges=?, lighttheme=?, isCurrentUser=? WHERE username=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, p.getUsername());
            preparedStatement.setString(2, p.getPassword());
            preparedStatement.setString(3, p.getPrivileges());
            preparedStatement.setBoolean(4, p.isLightTheme());
            preparedStatement.setBoolean(5, p.isItCurrentUser());
            preparedStatement.setString(6, id);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
        public void editUser(int id, Person p) {
            connectToDatabase();
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                String sql = "UPDATE users SET first_name=?, last_name=?, department=?, major=?, email=?, imageURL=? WHERE id=?";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, p.getFirstName());
                preparedStatement.setString(2, p.getLastName());
                preparedStatement.setString(3, p.getDepartment());
                preparedStatement.setString(4, p.getMajor());
                preparedStatement.setString(5, p.getEmail());
                preparedStatement.setString(6, p.getImageURL());
                preparedStatement.setInt(7, id);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public void deleteRecord(Person person) {
            int id = person.getId();
            connectToDatabase();
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                String sql = "DELETE FROM users WHERE id=?";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        //Method to retrieve id from database where it is auto-incremented.
        public int retrieveId(Person p) {
            connectToDatabase();
            int id;
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                String sql = "SELECT id FROM users WHERE email=?";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, p.getEmail());

                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                id = resultSet.getInt("id");
                preparedStatement.close();
                conn.close();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            lg.makeLog(String.valueOf(id));
            return id;
        }
}