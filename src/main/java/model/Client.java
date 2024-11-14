package model;

public class Client {
    public Client(String userName, String passEord, String privd, boolean light) {
        username = userName;
        password = passEord;
        privileges = privd;
        lightTheme = light;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPrivileges() {
        return privileges;
    }

    public void setPrivileges(String privileges) {
        this.privileges = privileges;
    }

    public boolean isLightTheme() {
        return lightTheme;
    }

    public void setLightTheme(boolean lightTheme) {
        this.lightTheme = lightTheme;
    }

    String username;
    String password;
    String privileges;
    boolean lightTheme;
}
