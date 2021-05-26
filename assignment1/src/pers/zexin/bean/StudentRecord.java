package pers.zexin.bean;

public class StudentRecord extends Record{
    // maths/french/science, note that student could be registered for multiple courses
    String[] CoursesRegistered;
    // active/inactive
    String status;
    // date when student became active (if status is active) or date when student became inactive (if status is inactive)
    String Date;
}
