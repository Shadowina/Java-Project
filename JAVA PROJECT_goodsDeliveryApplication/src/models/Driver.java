package models;

public class Driver extends User {
    private String truckRegNo;
    private String truckCapacity;

    public Driver(String userName, String userEmail, String phoneNumber, String userPassword,
                 String truckRegNo, String truckCapacity)
    {
        super(userName, userEmail, phoneNumber, userPassword, Role.DRIVER);
        this.truckRegNo = truckRegNo;
        this.truckCapacity = truckCapacity;
    }

    public void setTruckRegNo(String truckRegNo) {
        this.truckRegNo = truckRegNo;
    }

    public void setTruckCapacity(String truckCapacity) {
        this.truckCapacity = truckCapacity;
    }
    public String getTruckRegNo() {
        return truckRegNo;
    }

    public String getTruckCapacity() {
        return truckCapacity;
    }
}
