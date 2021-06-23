package bean;

import java.io.Serializable;

public class Manager implements Serializable {
    private String managerId;

    public Manager(String managerId) {
        this.managerId = managerId;
    }

    public String getManagerId() {
        return managerId;
    }
}
