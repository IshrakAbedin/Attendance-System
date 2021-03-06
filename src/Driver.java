import javafx.application.Application;
import javafx.stage.Stage;

/**
 * A temporary driver to debug and demonstrate XEbase's different methods.
 * This is to be removed upon the completion or UI build of project.
 *
 * @author Mohammad Ishrak Abedin
 * @version 0.1.0
 * @since 2018-09-24
 */
public class Driver extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        DrawWindows.getInstance().ManageLogIn();
    }

    /**
     * Temporary main method for the entry point of program.
     * @param args String[] not used
     */
    public static void main(String[] args){
//        System.out.println("Hell-o-World");
//
//        XEbase dbase = new XEbase("testingsalt", "testingsalt","disco","fisco"); // Constructor
////        dbase.setClassname("disco");
////        dbase.setSectionname("fisco");
//        ResultSet rs;
//        try {
//
//            // getTotalStudentCount() method use
//            System.out.println("Total Student Count: " + dbase.getTotalStudentCount());
//            // Separator for ease of viewing
//            System.out.println("-----------------------------------\n\n");
//
//            // getStudentList() method use
//            rs = dbase.getStudentList();
//            if (rs == null)
//                System.out.println("No Result Found");
//            else {
//                System.out.println("SID\tName\t\tAddress\t\tContact Number");
//                while (rs.next()) { // ResultSet must be exhausted by continuous use of next() method to avoid errors.
//                    // columnIndex of ResultSet is used here. Use of columnLabel is shown later.
//                    System.out.print(rs.getString(1) + "\t");
//                    System.out.print(rs.getString(2) + "\t");
//                    System.out.print(rs.getString(3) + "\t");
//                    System.out.print(rs.getString(4) + "\n");
//                }
//            }
//            System.out.println("-----------------------------------\n\n");
//
//            // getCourseList() method use
//            rs = dbase.getCourseList();
//            if (rs == null)
//                System.out.println("No Result Found");
//            else {
//                System.out.println("Section\tClass");
//                while (rs.next()) { // ResultSet must be exhausted by continuous use of next() method to avoid errors.
//                    // columnIndex of ResultSet is used here. Use of columnLabel is shown later.
//                    System.out.print(rs.getString("SECTION") + "\t");
//                    System.out.print(rs.getString("CLASS") + "\n");
//                }
//            }
//            System.out.println("-----------------------------------\n\n");
//
//            rs = dbase.getCompleteAttendanceList(); // getCompleteAttendanceList() method use
//            if (rs == null) System.out.println("No Result Found");
//            else {
//                System.out.println("SID\tDay");
//                while (rs.next()) {
//                    // columnLabel of ResultSet is used here instead of columnIndex
//                    System.out.print(rs.getString("SID") + "\t");
//                    System.out.print(rs.getString("DAY") + "\n");
//                }
//            }
//            System.out.println("-----------------------------------\n\n");
//
//            rs = dbase.getExtendedAttendanceList(); // getCompleteAttendanceList() method use
//            if (rs == null) System.out.println("No Result Found");
//            else {
//                System.out.println("SID\tName\t\tAddress\t\tNumber\tCount\tPercentage");
//                while (rs.next()) {
//                    // columnLabel of ResultSet is used here instead of columnIndex
//                    System.out.print(rs.getString("SID") + "\t");
//                    System.out.print(rs.getString("NAME") + "\t");
//                    System.out.print(rs.getString("ADDRESS") + "\t");
//                    System.out.print(rs.getString("CONTACT_NUMBER") + "\t");
//                    System.out.print(rs.getString("ATTENDANCE_COUNT") + "\t");
//                    System.out.print(rs.getFloat("ATTENDANCE_PERCENTAGE") + "\n");
//                }
//            }
//            System.out.println("-----------------------------------\n\n");
//
//            rs = dbase.getExtendedAttendanceBySIDList("102"); // getCompleteAttendanceList() method use
//            if (rs == null) System.out.println("No Result Found");
//            else {
//                System.out.println("102\tName\t\tAddress\t\tNumber\tCount\tPercentage");
//                while (rs.next()) {
//                    // columnLabel of ResultSet is used here instead of columnIndex
//                    System.out.print(rs.getString("SID") + "\t");
//                    System.out.print(rs.getString("NAME") + "\t");
//                    System.out.print(rs.getString("ADDRESS") + "\t");
//                    System.out.print(rs.getString("CONTACT_NUMBER") + "\t");
//                    System.out.print(rs.getString("ATTENDANCE_COUNT") + "\t");
//                    System.out.print(rs.getFloat("ATTENDANCE_PERCENTAGE") + "\n");
//                }
//            }
//            System.out.println("-----------------------------------\n\n");
//
//            System.out.println("Inserted Rows: " + dbase.insertAttendance("402", "OCT15")); // insertAttendance() method use
//            System.out.println("-----------------------------------\n\n");
//
//            rs = dbase.getAttendanceByDayList("SEP24"); // getAttendanceByDayList() method use
//            if (rs == null) System.out.println("No Result Found");
//            else {
//                System.out.println("SID\tDay");
//                while (rs.next()) {
//                    System.out.print(rs.getString("SID") + "\t");
//                    System.out.print(rs.getString("DAY") + "\n");
//                }
//            }
//            System.out.println("-----------------------------------\n\n");
//
//            System.out.println("Deleted Rows: " + dbase.deleteAttendance("101", "SEP25")); // deleteAttendance() method use
//            System.out.println("-----------------------------------\n\n");
//
//            rs = dbase.getAttendanceByDayList("SEP25"); // getAttendanceByDayList() method use
//            if (rs == null) System.out.println("No Result Found");
//            else {
//                System.out.println("SID\tDay");
//                while (rs.next()) {
//                    System.out.print(rs.getString("SID") + "\t");
//                    System.out.print(rs.getString("DAY") + "\n");
//                }
//            }
//            System.out.println("-----------------------------------\n\n");
//
//            rs = dbase.getAttendanceBySIDList("102"); // getAttendanceByDayList() method use
//            if (rs == null) System.out.println("No Result Found");
//            else {
//                System.out.println("SID\tDay");
//                while (rs.next()) {
//                    System.out.print(rs.getString("SID") + "\t");
//                    System.out.print(rs.getString("DAY") + "\n");
//                }
//            }
//            System.out.println("-----------------------------------\n\n");
//
//            rs = dbase.getAttendanceCountPerStudentList(); // getAttendanceCountPerStudentList() method use
//            if (rs == null) System.out.println("No Result Found");
//            else {
//                System.out.println("SID\tCount");
//                while (rs.next()) {
//                    System.out.print(rs.getString("SID") + "\t");
//                    System.out.print(rs.getInt("ATTENDANCE_COUNT") + "\n");
//                }
//            }
//            System.out.println("-----------------------------------\n\n");
//
//            System.out.println("Total class count: " + dbase.getTotalClassCount()); // getTotalClassCount() method use
//            System.out.println("-----------------------------------\n\n");
//
//            rs = dbase.getAttendancePercentageList(); // getAttendancePercentageList() method use
//            if (rs == null) System.out.println("No Result Found");
//            else {
//                System.out.println("SID\tPercentage");
//                while (rs.next()) {
//                    System.out.print(rs.getString("SID") + "\t");
//                    System.out.print(rs.getInt("ATTENDANCE_PERCENTAGE") + "\n");
//                }
//            }
//            System.out.println("-----------------------------------\n\n");
//
//            rs = dbase.getAttendancePercentageListWithName(); // getAttendancePercentageListWithName() method use
//            if (rs == null) System.out.println("No Result Found");
//            else {
//                System.out.println("SID\tName\tPercentage");
//                while (rs.next()) {
//                    System.out.print(rs.getString(1) + "\t");
//                    System.out.print(rs.getString(2) + "\t");
//                    System.out.print(rs.getInt(3) + "\n");
//                }
//            }
//            System.out.println("-----------------------------------\n\n");
//
//            rs = dbase.getAttendanceDefauterList(); // getAttendanceDefauterList() method use
//            if (rs == null) System.out.println("No Result Found");
//            else {
//                System.out.println("SID\tDef_Percentage");
//                while (rs.next()) {
//                    System.out.print(rs.getString("SID") + "\t");
//                    System.out.print(rs.getInt("ATTENDANCE_PERCENTAGE") + "\n");
//                }
//            }
//            System.out.println("-----------------------------------\n\n");
//
//            rs = dbase.getAttendanceDefauterListWithName(); // getAttendanceDefauterListWithName()
//            if (rs == null) System.out.println("No Result Found");
//            else {
//                System.out.println("SID\tName\tDef_Percentage");
//                while (rs.next()) {
//                    System.out.print(rs.getString(1) + "\t");
//                    System.out.print(rs.getString(2) + "\t");
//                    System.out.print(rs.getInt(3) + "\n");
//                }
//            }
//            System.out.println("-----------------------------------\n\n");
//
//            System.out.println("Def count: "+dbase.getDefaulterCount()); // getDefaulterCount()
//            System.out.println("-----------------------------------\n\n");
//
//            rs = dbase.getTakenDayList(); // getAttendanceDefauterListWithName()
//            if (rs == null) System.out.println("No Result Found");
//            else {
//                System.out.println("Day\n");
//                while (rs.next()) {
//                    System.out.print(rs.getString("DAY") + "\n");
//                }
//            }
//            System.out.println("-----------------------------------\n\n");
//
//            System.out.println(dbase.wasTakenOnDay("SEP24"));
//            System.out.println(dbase.wasTakenOnDay("SEP25"));
//            System.out.println(dbase.wasTakenOnDay("SEP23"));
//            System.out.println(dbase.wasTakenOnDay("SEP26"));
//            System.out.println("-----------------------------------\n\n");
//
//        }
//        catch (SQLException ex){
//            ex.printStackTrace(); // Exception handled and printed
//        }
//        finally {
//            dbase.close(); // finally statement makes sure database is always closed at end regardless of the occurrence of exception
//        }
//
//        XEadmin xeadmin = new XEadmin("ADMIN", "ADMIN");
//        System.out.println(xeadmin.createTeacher("testingsalt", "testingsalt"));
//        System.out.println(xeadmin.deleteTeacher("testingsalt"));
//        System.out.println(xeadmin.createSection("testingsalt","disco"));
//        System.out.println(xeadmin.createClass("testingsalt","disco","fisco"));
//        System.out.println(xeadmin.deleteClass("testingsalt","disco","fisco"));
//        System.out.println(xeadmin.deleteSection("testingsalt","disco"));
//        System.out.println(xeadmin.insertStudent("testingsalt","disco","401","Waterfall","Niagara","420911"));
//        System.out.println(xeadmin.deleteStudent("testingsalt","disco","401"));
//        System.out.println(xeadmin.insertStudent("testingsalt","disco","402","Nvidia GeForce","GTX1080","420912"));
//        try {
//            ResultSet rs = xeadmin.getCompleteTeacherSectionCourseList();
//            if (rs == null) System.out.println("No Result Found");
//            else {
//                System.out.println("Teacher\tSection\tClass");
//                while (rs.next()) {
//                    System.out.print(rs.getString("TNAME") + "\t");
//                    System.out.print(rs.getString("SECTION") + "\t");
//                    System.out.print(rs.getString("CLASS") + "\n");
//                }
//            }
//            System.out.println("-----------------------------------\n\n");
//
//            rs = xeadmin.getSectionCourseByTeacherList("DEMOTEACHER");
//            if (rs == null) System.out.println("No Result Found");
//            else {
//                System.out.println("Section\tClass");
//                while (rs.next()) {
//                    System.out.print(rs.getString("SECTION") + "\t");
//                    System.out.print(rs.getString("CLASS") + "\n");
//                }
//            }
//            System.out.println("-----------------------------------\n\n");
//
//            rs = xeadmin.getTeacherList();
//            if (rs == null) System.out.println("No Result Found");
//            else {
//                System.out.println("Teacher's Name");
//                while (rs.next()) {
//                    System.out.print(rs.getString("TNAME") + "\n");
//                }
//            }
//            System.out.println("-----------------------------------\n\n");
//
//            rs = xeadmin.getStudentListByTeacher("DEMOTEACHER", "CSE16");
//            if (rs == null)
//                System.out.println("No Result Found");
//            else {
//                System.out.println("SID\tName\t\tAddress\t\tContact Number");
//                while (rs.next()) { // ResultSet must be exhausted by continuous use of next() method to avoid errors.
//                    // columnIndex of ResultSet is used here. Use of columnLabel is shown later.
//                    System.out.print(rs.getString(1) + "\t");
//                    System.out.print(rs.getString(2) + "\t");
//                    System.out.print(rs.getString(3) + "\t");
//                    System.out.print(rs.getString(4) + "\n");
//                }
//            }
//            System.out.println("-----------------------------------\n\n");
//        }
//        catch (SQLException ex){
//            ex.printStackTrace();
//        }
//        xeadmin.close();

        launch(args);
    }
}
