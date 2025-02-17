package models;

public class User {
    // ... rest of the code ...
    public enum Role {

        CUSTOMER,

        DRIVER,

        SCHEDULER

    }

    private String userName;

    private String userEmail;

    private String userPassword;

    private String phoneNumber;

    private Role role;

    public User(String userName, String userEmail, String phoneNumber, String userPassword, Role role) {

        this.userName = userName;

        this.userEmail = userEmail;

        this.phoneNumber = phoneNumber;

        this.userPassword = userPassword;

        this.role = role;

    }

    public String getUserName() {

        return userName;

    }


    public void setUserName(String userName) {

        this.userName = userName;

    }

    public String getUserEmail() {

        return userEmail;

    }

    public void setUserEmail(String userEmail) {

        this.userEmail = userEmail;

    }

    public String getPhoneNumber() {

    	return phoneNumber;

    }


    public void setPhoneNumber(String phoneNumber) {

    	this.phoneNumber = phoneNumber;

    }

    public String getUserPassword() {

        return userPassword;

    }

    public void setUserPassword(String userPassword) {

        this.userPassword = userPassword;

    }

    public Role getRole() {

        return role;

    }



    public void setRole(Role role) {

        this.role = role;

    }

    public String getEmail() {
        return userEmail;
    }
}