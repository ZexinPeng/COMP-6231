package pers.zexin.bean;

import java.io.Serializable;

public class Record implements Serializable {
    String firstName;
    String lastName;
    String recordID;

    public String getRecordID() {
        return recordID;
    }
}
