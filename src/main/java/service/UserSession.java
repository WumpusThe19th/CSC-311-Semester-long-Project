package service;

import model.Client;

import java.util.HashSet;
import java.util.Set;
import java.util.prefs.Preferences;

public class UserSession {

    private static UserSession instance;

    private String userName;

    private String password;
    private String privileges;

    private boolean theme;

    private UserSession(String userName, String password, String privileges, boolean lightTheme) {
        this.userName = userName;
        this.password = password;
        this.privileges = privileges;
        this.theme = lightTheme;
        Preferences userPreferences = Preferences.userRoot();
        userPreferences.put("USERNAME",userName);
        userPreferences.put("PASSWORD",password);
        userPreferences.put("PRIVILEGES",privileges);
        userPreferences.putBoolean("THEME", theme);
    }



    public static synchronized UserSession getInstance(String userName,String password, String privileges, boolean lightTheme) {
        if(instance == null) {
            instance = new UserSession(userName, password, privileges, lightTheme);
        }
        System.out.println("The 4 argument method says there's already an instance");
        return instance;
    }

    //Oh wait. This doesn't actually require an identifier.
    public static synchronized UserSession getInstance(String userName,String password) {
        if(instance == null) {
            System.out.println("The 2 argument method says there is no previous instance");
            instance = new UserSession(userName, password, "NONE", true);
        }
        return instance;
    }

    public static UserSession getInstance(Client client){
        if (instance == null){
            System.out.println("The client method says there's no previous instance");
            instance = new UserSession(client.getUsername(), client.getPassword(), client.getPrivileges(), client.isLightTheme());
        }
        return instance;
    }
    public String getUserName() {
        return this.userName;
    }

    public String getPassword() {
        return this.password;
    }

    public String getPrivileges() {
        return this.privileges;
    }

    public boolean getTheme(){ return this.theme;};
    public void cleanUserSession() {
        this.userName = "";// or null
        this.password = "";
        this.privileges = "";// or null
    }

    @Override
    public String toString() {
        return "UserSession{" +
                "userName='" + this.userName + '\'' +
                ", privileges=" + this.privileges +
                '}';
    }
}
