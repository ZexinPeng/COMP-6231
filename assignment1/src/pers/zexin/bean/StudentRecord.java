package pers.zexin.bean;

import java.io.Serializable;

public class StudentRecord extends Record implements Serializable {
    // maths/french/science, note that student could be registered for multiple courses
    String[] coursesRegistered;
    // active/inactive
    String status;
    // date when student became active (if status is active) or date when student became inactive (if status is inactive)
    String statusDate;

    public StudentRecord(String recordID, String firstName, String lastName, String[] coursesRegistered, String status, String statusDate) {
        this.recordID = recordID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.coursesRegistered = coursesRegistered;
        this.status = status;
        this.statusDate = statusDate;
    }

    @Override
    public String toString() {
        return "{recordID: " + this.recordID + ", first name: " + this.firstName + ", last name: " + this.lastName +
                ", courses registered: {" + convertCoursesRegistered2String() + "}, status: " + this.status
                + ", status date: " + this.statusDate + "}";
    }

    private String convertCoursesRegistered2String() {
        if (coursesRegistered == null || coursesRegistered.length == 0) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < coursesRegistered.length - 1; i++) {
            sb.append(coursesRegistered[i]);
            sb.append(", ");
        }
        sb.append(coursesRegistered[coursesRegistered.length - 1]);
        return sb.toString();
    }
}
