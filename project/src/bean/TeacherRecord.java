package bean;

import java.io.Serializable;

public class TeacherRecord extends Record implements Serializable{
    private String address;
    private String phone;
    // french, maths, etc
    private String specialization;
    // mtl, lvl, ddo
    private String location;

    public TeacherRecord(String recordID, String firstName, String lastName, String address, String phone, String specialization, String location) {
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
     * @return all information of the current record
     */
    @Override
    public String toString() {
        return "{recordID: " + this.recordID + ", first name: " + this.firstName + ", last name: " + this.lastName + ", address: " + this.address
                + ", phone: " + this.phone + ", specialization: " + this.specialization + ", location: "
                + this.location + "}";
    }

    /**
     * convert the record into serialization format.
     * @return serialization format
     */
    public String toSerialize() {
        StringBuilder sb = new StringBuilder("teacher,");
        return sb.append(recordID).append(",").append(firstName).append(",").append(lastName).append(",").append(address).append(",")
                .append(phone).append(",").append(specialization).append(",").append(location).toString();
    }

    public static TeacherRecord deserialize(String str) {
        String[] arr = str.split(",");
        return new TeacherRecord(arr[1], arr[2], arr[3], arr[4], arr[5], arr[6], arr[7]);
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSpecialization() {
        return specialization;
    }
}
