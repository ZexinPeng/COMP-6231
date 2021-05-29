package pers.zexin.bean;

import java.io.Serializable;

public class TeacherRecord extends Record implements Serializable{
    private String address;
    private String phone;
    // french, maths, etc
    private String specialization;
    // mtl, lvl, ddo
    private Location location;

    public TeacherRecord(String recordID, String firstName, String lastName, String address, String phone, String specialization, Location location) {
        this.recordID = recordID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
        this.specialization = specialization;
        this.location = location;
    }

    /**
     * First name
     *  Last name
     *  Address
     *  Phone
     *  Specialization (e.g. french, maths, etc)
     *  Location (mtl, lvl, ddo)
     * @return
     */
    @Override
    public String toString() {
        return "{recordID: " + this.recordID + ", first name: " + this.firstName + ", last name: " + this.lastName + ", address: " + this.address
                + ", phone: " + this.phone + ", specialization: " + this.specialization + ", location: "
                + this.location + "}";
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
