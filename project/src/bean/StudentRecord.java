package bean;

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

    /**
     * convert the record into serialization format.
     * @return serialization format
     */
    public String toSerialize() {
        StringBuilder sb = new StringBuilder("student,");
        sb.append(recordID).append(",").append(firstName).append(",").append(lastName).append(",");
        for (String str: coursesRegistered) {
            sb.append(str);
            if (str.equals(coursesRegistered[coursesRegistered.length - 1])) {
                continue;
            }
            sb.append("&");
        }
        sb.append(",").append(status).append(",").append(statusDate).toString();
        return sb.toString();
    }

    public static StudentRecord deserialize(String str) {
        String[] arr = str.split(",");
        return new StudentRecord(arr[1], arr[2], arr[3], arr[4].split("&"), arr[5], arr[6]);
    }

    public String toSerialize(String managerID) {
        StringBuilder sb = new StringBuilder(managerID);
        sb.append(",student").append(",").append(firstName).append(",").append(lastName).append(",");
        for (String str: coursesRegistered) {
            sb.append(str);
            if (str.equals(coursesRegistered[coursesRegistered.length - 1])) {
                continue;
            }
            sb.append("&");
        }
        sb.append(",").append(status).append(",").append(statusDate).toString();
        return sb.toString();
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

    public static String convertCoursesRegistered2Serialize(String[] coursesRegistered) {
        if (coursesRegistered == null || coursesRegistered.length == 0) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < coursesRegistered.length - 1; i++) {
            sb.append(coursesRegistered[i]);
            sb.append("&");
        }
        sb.append(coursesRegistered[coursesRegistered.length - 1]);
        return sb.toString();
    }

    public static String[] convertCoursesRegistered2Arr(String str) {
        return str.split("&");
    }

    public void setCoursesRegistered(String[] coursesRegistered) {
        this.coursesRegistered = coursesRegistered;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStatusDate(String statusDate) {
        this.statusDate = statusDate;
    }

    public String[] getCoursesRegistered() {
        return coursesRegistered;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusDate() {
        return statusDate;
    }
}
