package bean;

import java.io.Serializable;

public class Record implements Serializable {
    String firstName;
    String lastName;
    String recordID;

    public String getRecordID() {
        return recordID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
