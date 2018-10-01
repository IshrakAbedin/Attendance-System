package Data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Comparator;

/**
 * Class that stores student information : ID, Name, Address and contact information.
 * @author Shakleen Ishfar
 * @version 0.1.0
 * @since 2018-09-29
 */
public class StudentInformation {
    private String SID;                                         // Student ID
    private String Name;                                        // Name of student
    private String Address;                                     // Address of student
    private String ContactNumber;                               // Contact number for student
    private Integer AttendanceCount;                            // No of attendance a student has
    private Integer AttendancePercentage;                       // Attendance percentange of a student
    private ObservableList<String> PresentDayList;  // The days a student was present

    /**
     * Constructor for creating Data.StudentInformation type object.
     * @param SID Student ID
     * @param name Student Name
     * @param address Student address
     * @param contactNumber Student contact information
     */
    public StudentInformation(String SID, String name, String address, String contactNumber) {
        this.SID = SID;
        Name = name;
        Address = address;
        ContactNumber = contactNumber;
        PresentDayList = FXCollections.observableArrayList();
    }

    /**
     * @return Student ID
     */
    public String getSID() {
        return SID;
    }

    /**
     * @return Student Name
     */
    public String getName() {
        return Name;
    }

    /**
     * @return Student Address
     */
    public String getAddress() {
        return Address;
    }

    /**
     * @return Student Contact number.
     */
    public String getContactNumber() {
        return ContactNumber;
    }

    /**
     * @return Attendance Count.
     */
    public Integer getAttendanceCount() {
        return AttendanceCount;
    }

    /**
     * Sets the value for attendanceCount
     * @param attendanceCount
     */
    public void setAttendanceCount(Integer attendanceCount) {
        AttendanceCount = attendanceCount;
    }

    /**
     * @return Attendance percentage.
     */
    public Integer getAttendancePercentage() {
        return AttendancePercentage;
    }

    /**
     * Sets the value for attendancePercentage
     * @param attendancePercentage
     */
    public void setAttendancePercentage(Integer attendancePercentage) {
        AttendancePercentage = attendancePercentage;
    }

    /**
     * @return Present days of a student
     */
    public ObservableList<String> getPresentDayList() {
        return PresentDayList;
    }

    /**
     * Adds present days to list.
     */
    public void addPresentDayList(String presentDay) {
        PresentDayList.add(presentDay);
    }

    /**
     * Removes a day from present day list.
     * @param presentDay the day to remove
     * @return true if successful. Otherwise false.
     */
    public boolean removePresentDayList(String presentDay){
        return PresentDayList.remove(presentDay);
    }
}
