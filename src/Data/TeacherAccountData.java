package Data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TeacherAccountData {
    private String AccountName;
    private String Password;
    private ObservableList<String> CourseList;
    private ObservableList<String> SectionList;
    private ObservableList<String> ClassDaysList;
    private DateTimeFormatter dateTimeFormatter;

    public TeacherAccountData(String accountName, String password) {
        AccountName = accountName;
        Password = password;
        CourseList = FXCollections.observableArrayList();
        SectionList = FXCollections.observableArrayList();
        ClassDaysList = FXCollections.observableArrayList();
        dateTimeFormatter = DateTimeFormatter.ofPattern("MMMdd");
    }

    public void addToCourseList(String Course, String Section){
        CourseList.add(Course);
        SectionList.add(Section);
    }

    public void addToClassDays(LocalDate ClassDate){
        String date = dateTimeFormatter.format(ClassDate).toUpperCase();
        ClassDaysList.add(date);
    }

    public void addToClassDays(String date){
        ClassDaysList.add(date);
    }

    public String getAccountName() {
        return AccountName;
    }

    public void setAccountName(String accountName) {
        AccountName = accountName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public ObservableList<String> getCourseList() {
        return CourseList;
    }

    public ObservableList<String> getClassDaysList() {
        return ClassDaysList;
    }

    public ObservableList<String> getSectionList() {
        return SectionList;
    }

    public String getSectionForCourse(String Course){
        int index = CourseList.indexOf(Course);

        if (index == -1){
            return null;
        }

        return SectionList.get(index);
    }
}
