import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A temporary driver to debug and demonstrate XEbase's different methods.
 * This is to be removed upon the completion or UI build of project.
 *
 * @author Mohammad Ishrak Abedin
 * @version 0.1.0
 * @since 2018-09-24
 */
public class Driver {
    /**
     * Temporary main method for the entry point of program.
     * @param args String[] not used
     */
    public static void main(String[] args){
        System.out.println("Hell-o-World");
        XEbase dbase = new XEbase("DEMOTEACHER", "DEMOPASSWORD"); // Constructor
        ResultSet rs;
        try {

            System.out.println("Total Student Count: " + dbase.getTotalStudentCount()); // getTotalStudentCount() method use
            System.out.println("-----------------------------------\n\n"); // Separator for ease of viewing


            rs = dbase.getStudentList(); // getStudentList() method use
            if (rs == null) System.out.println("No Result Found");
            else {
                System.out.println("SID\tName\t\tAddress\t\tContact Number");
                while (rs.next()) { // ResultSet must be exhausted by continuous use of next() method to avoid errors.
                    // columnIndex of ResultSet is used here. Use of columnLabel is shown later.
                    System.out.print(rs.getString(1) + "\t");
                    System.out.print(rs.getString(2) + "\t");
                    System.out.print(rs.getString(3) + "\t");
                    System.out.print(rs.getString(4) + "\n");
                }
            }
            System.out.println("-----------------------------------\n\n");

            rs = dbase.getCompleteAttendanceList(); // getCompleteAttendanceList() method use
            if (rs == null) System.out.println("No Result Found");
            else {
                System.out.println("SID\tDay");
                while (rs.next()) {
                    // columnLabel of ResultSet is used here instead of columnIndex
                    System.out.print(rs.getString("SID") + "\t");
                    System.out.print(rs.getString("DAY") + "\n");
                }
            }
            System.out.println("-----------------------------------\n\n");

            System.out.println("Inserted Rows: " + dbase.insertAttendance("101", "SEP25")); // insertAttendance() method use
            System.out.println("-----------------------------------\n\n");

            rs = dbase.getAttendanceByDayList("SEP25"); // getAttendanceByDayList() method use
            if (rs == null) System.out.println("No Result Found");
            else {
                System.out.println("SID\tDay");
                while (rs.next()) {
                    System.out.print(rs.getString("SID") + "\t");
                    System.out.print(rs.getString("DAY") + "\n");
                }
            }
            System.out.println("-----------------------------------\n\n");

            System.out.println("Deleted Rows: " + dbase.deleteAttendance("101", "SEP25")); // deleteAttendance() method use
            System.out.println("-----------------------------------\n\n");

            rs = dbase.getAttendanceByDayList("SEP25"); // getAttendanceByDayList() method use
            if (rs == null) System.out.println("No Result Found");
            else {
                System.out.println("SID\tDay");
                while (rs.next()) {
                    System.out.print(rs.getString("SID") + "\t");
                    System.out.print(rs.getString("DAY") + "\n");
                }
            }
            System.out.println("-----------------------------------\n\n");

            rs = dbase.getAttendanceCountPerStudentList(); // getAttendanceCountPerStudentList() method use
            if (rs == null) System.out.println("No Result Found");
            else {
                System.out.println("SID\tCount");
                while (rs.next()) {
                    System.out.print(rs.getString("SID") + "\t");
                    System.out.print(rs.getInt("ATTENDANCE_COUNT") + "\n");
                }
            }
            System.out.println("-----------------------------------\n\n");

            System.out.println("Total class count: " + dbase.getTotalClassCount()); // getTotalClassCount() method use
            System.out.println("-----------------------------------\n\n");

            rs = dbase.getAttendancePercentageList(); // getAttendancePercentageList() method use
            if (rs == null) System.out.println("No Result Found");
            else {
                System.out.println("SID\tPercentage");
                while (rs.next()) {
                    System.out.print(rs.getString("SID") + "\t");
                    System.out.print(rs.getInt("ATTENDANCE_PERCENTAGE") + "\n");
                }
            }
            System.out.println("-----------------------------------\n\n");

            rs = dbase.getAttendancePercentageListWithName(); // getAttendancePercentageListWithName() method use
            if (rs == null) System.out.println("No Result Found");
            else {
                System.out.println("SID\tName\tPercentage");
                while (rs.next()) {
                    System.out.print(rs.getString(1) + "\t");
                    System.out.print(rs.getString(2) + "\t");
                    System.out.print(rs.getInt(3) + "\n");
                }
            }
            System.out.println("-----------------------------------\n\n");

            rs = dbase.getAttendanceDefauterList(); // getAttendanceDefauterList() method use
            if (rs == null) System.out.println("No Result Found");
            else {
                System.out.println("SID\tDef_Percentage");
                while (rs.next()) {
                    System.out.print(rs.getString("SID") + "\t");
                    System.out.print(rs.getInt("ATTENDANCE_PERCENTAGE") + "\n");
                }
            }
            System.out.println("-----------------------------------\n\n");

            rs = dbase.getAttendanceDefauterListWithName(); // getAttendanceDefauterListWithName()
            if (rs == null) System.out.println("No Result Found");
            else {
                System.out.println("SID\tName\tDef_Percentage");
                while (rs.next()) {
                    System.out.print(rs.getString(1) + "\t");
                    System.out.print(rs.getString(2) + "\t");
                    System.out.print(rs.getInt(3) + "\n");
                }
            }
            System.out.println("-----------------------------------\n\n");

            System.out.println(dbase.getDefaulterCount()); // getDefaulterCount()
            System.out.println("-----------------------------------\n\n");



        }
        catch (SQLException ex){
            ex.printStackTrace(); // Exception handled and printed
        }
        finally {
            dbase.close(); // finally statement makes sure database is always closed at end regardless of the occurance of exception
        }
    }
}
