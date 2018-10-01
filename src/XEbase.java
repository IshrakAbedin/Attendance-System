import java.sql.*;

/**
 * <h1> Controls backend connection with OracleXE database</h1>
 * The main purpose of this class is to streamline the database connectivity and query precessing for front end
 * developers. Extensive uses of methods simplifies both connectivity, result retrieval and exception handling.
 * Front end developers only have to deal with SQLException while processing 'ResultSet' type objects.
 * <p>
 * Generally the methods that end with or has 'List' in them returns ResultSet type objects. Returns NULL in case of error.
 * Generally the methods that end with or has 'Count' in them returns int >= 0 or -1 in case of error
 * <p>
 * <b> N.B. Initiate object of class with username and password and always call the method close at end to disconnect
 * from database</b>
 *
 * @author Mohammad Ishrak Abedin
 * @version 0.1.0
 * @since 2018-09-24
 */
public class XEbase {
    private Connection connection; // Connection type object to get connection using driver.
    private String dbURL; // URL for database used.
    public Statement statement; // Statement type object to execute DDL and DML queries.
    public String query; // Holds query for execution by statement object.
    public ResultSet resultSet; // ResultSet type object to return database tables.
    public String name; // Username of teacher.
    public String password; // Password for database.
    public String sectionname; // Name of section, e.g. CSE16, EEE17A etc. Name formats are subject to change.
    public String classname; // Name of subject wise class, e.g. MATH4441, CSE4407. Name formats are subject to change.
    public boolean error; // Error state. Can be called at any time to check for errors.
    public boolean connected; // Connection state. Can be called at any time to check for connectivity.
    private int rt; // Private variable to return int type data by methods.

    /**
     * Constructor for XEbase class objects. Creates connection with database.
     * By default 'classname' will be selected as DEMOCLASS and 'sectionname' as DEMOSECTION.
     * @param _username String Username for the database account
     * @param _password String Password that is allocated to the user
     */
    public XEbase (String _username, String _password){
        connection = null;
        error = false;
        connected = false;
        classname = "DEMOCLASS";
        sectionname = "DEMOSECTION";
        try{
            Class.forName("oracle.jdbc.OracleDriver");
            dbURL = "jdbc:oracle:thin:@localhost:1521:xe";
            name = _username;
            password = _password;
            connection = DriverManager.getConnection(dbURL, name, password);

            if(connection != null) {
                connected = true;
                statement = connection.createStatement();
            }
            else connected = false;
        }
        catch (ClassNotFoundException | SQLException ex){
            error = true;
            ex.printStackTrace();
        }
    }

    /**
     * Overloaded constructor for XEbase class objects. Creates connection with database.
     * @param _username String Username for the database account
     * @param _password String Password that is allocated to the user
     * @param _sectionname String Name of the section where class will be taken
     * @param _classname String Name of the class where attendance will be taken
     */
    public XEbase (String _username, String _password, String _sectionname, String _classname){
        connection = null;
        error = false;
        connected = false;
        classname = _classname;
        sectionname = _sectionname;
        try{
            Class.forName("oracle.jdbc.OracleDriver");
            dbURL = "jdbc:oracle:thin:@localhost:1521:xe";
            name = _username;
            password = _password;
            connection = DriverManager.getConnection(dbURL, name, password);

            if(connection != null) {
                connected = true;
                statement = connection.createStatement();
            }
            else connected = false;
        }
        catch (ClassNotFoundException | SQLException ex){
            error = true;
            ex.printStackTrace();
        }
    }

    /**
     * Closes connection with database. Should always be called at the end of each session to close the database.
     */
    public void close(){
        try{
            if(connection != null && !connection.isClosed())
                connection.close();
        }
        catch (SQLException ex){
            error = true;
            ex.printStackTrace();
        }
    }

    /**
     * A method to get the Student ID, Name, Address and Contact Number of all students present in the section.
     * @return ResultSet Contains 4 columns. ColumnLabel: "SID" , "NAME" , "ADDRESS" , "CONTACT_NUMBER".
     */
    public ResultSet getStudentList(){
        query = "SELECT * FROM " + sectionname;
        if (resultSetHandler()) return resultSet;
        return null;
    }

    /**
     * A method to get total number of students in the class.
     * @return int Total number of student. Returns NULL in case of error.
     */
    public int getTotalStudentCount(){
        query = "SELECT * FROM " + sectionname + "_STUDENT_COUNT";
        if (integerHandler()) return rt;
        return -1; // returns -1 if error
    }

    /**
     * A method to get the attendance list for the whole section for whole semester for every day.
     * @return ResultSet Contains 2 columns. ColumnLabel: "SID" , "DAY". Returns NULL in case of error.
     */
    public ResultSet getCompleteAttendanceList(){
        query = "SELECT * FROM " + classname;
        if (resultSetHandler()) return resultSet;
        return null;
    }

    /**
     * A method to get the complete attendance list for the whole section with student ID, name, address,
     * contact number, attendance count and attendance percentage.
     * @return ResultSet Contains 6 columns. ColumnLabel: "SID" , "NAME" , "ADDRESS" , "CONTACT_NUMBER" ,
     * "ATTENDANCE_COUNT" , "ATTENDANCE_PERCENTAGE". Returns NULL in case of error.
     */
    public ResultSet getExtendedAttendanceList(){
        query = "SELECT * FROM " + classname+"_EXTENDED_ATTENDANCE";
        if (resultSetHandler()) return resultSet;
        return null;
    }

    /**
     * A method to get the Student IDs for those students who were present on a specific day.
     * @param _day String Day of inquiry. Format is first three letters of month + date. E.g. "SEP24". Format subject to change.
     * @return ResultSet Contains 2 columns. ColumnLabel: "SID" , "DAY".  Returns NULL in case of error.
     */
    public ResultSet getAttendanceByDayList(String _day){
        query = "SELECT * FROM " + classname + " WHERE DAY = '" + _day + "'" ;
        if (resultSetHandler()) return resultSet;
        return null;
    }

    /**
     * A method to get the attendance days for a specific student.
     * @param _id String Student ID of inquiry.
     * @return ResultSet Contains 2 columns. ColumnLabel: "SID" , "DAY".  Returns NULL in case of error.
     */
    public ResultSet getAttendanceBySIDList(String _id){
        query = "SELECT * FROM " + classname + " WHERE SID = '" + _id + "'" ;
        if (resultSetHandler()) return resultSet;
        return null;
    }

    /**
     * A method to delete attendance of a specific student on a specific day in case of mistakes or change.
     * @param _id String ID of the student to be deleted
     * @param _day String Day on which the attendance needs to be deleted
     * @return int Number of entries modified/ deleted in database. Returns -1 in case of error.
     */
    public int deleteAttendance(String _id, String _day){
        query = "DELETE FROM " + classname + " WHERE SID = " + _id + " AND DAY = '" + _day +"'";
        Integer count1 = getCount();
        if (count1 != null) return count1;
        return -1;
    }

    /**
     * A method to insert attendance for a specific student for a specific day.
     * @param _id String ID of the student.
     * @param _day String Day for which attendance needs to be inserted.
     * @return int Number of entries modified/ added in database. Returns -1 in case of error.
     */
    public int insertAttendance(String _id, String _day){
        query = "INSERT INTO " + classname + " VALUES(" + _id + ", '" + _day + "')";
        Integer count1 = getCount();
        if (count1 != null) return count1;
        return -1;
    }

    /**
     * A method to get total attendance count for every student based on Student ID.
     * @return ResultSet Contains 2 columns. ColumnLabel: "SID" , "ATTENDANCE_COUNT".  Returns NULL in case of error.
     */
    public ResultSet getAttendanceCountPerStudentList(){
        query = "SELECT * FROM " + classname + "_ATTENDANCE_COUNT";
        if (resultSetHandler()) return resultSet;
        return null;
    }

    /**
     * A method to get total attendance count for every student based on Student ID.
     * @return the Attendance count of the sstudent with _id
     */
    public int getAttendanceCountBySIDList(String _id){
        query = "SELECT * FROM " + classname + "_ATTENDANCE_COUNT" + " WHERE SID = '" + _id + "'" ;;
        try{
            resultSet = statement.executeQuery(query);
            resultSet.next();
            return resultSet.getInt(2);
        }
        catch (SQLException ex){
            error = true;
            ex.printStackTrace();
        }
        return -1;
    }

    /**
     * A method to get total number of academic classes/days held for that specific Class.
     * @return int Number of total classes. Returns -1 in case of error.
     */
    public int getTotalClassCount() {
        query = "SELECT * FROM " + classname + "_TOTAL_CLASS_COUNT";
        if (integerHandler()) return rt;
        return -1; // returns -1 if error
    }

    /**
     * A method to get attendance percentage for every student.
     * @return ResultSet Contains 2 columns. ColumnLabel : "SID" , "ATTENDANCE_PERCENTAGE". Returns NULL in case of error.
     */
    public ResultSet getAttendancePercentageList(){
        int classcount = this.getTotalClassCount();
        query ="SELECT * FROM " + classname + "_PERCENTAGE";
        if(classcount > 0)
            if (resultSetHandler()) return resultSet;
        return null;
    }

    /**
     * A method to get attendance percentage for every student.
     * @return the Attendance count of the sstudent with _id
     */
    public int getAttendancePercentageListBySIDList(String _id){
        int classcount = this.getTotalClassCount();
        query ="SELECT * FROM " + classname + "_PERCENTAGE" + " WHERE SID = '" + _id + "'" ;
        if(classcount > 0)
            try {
                resultSet = statement.executeQuery(query);
                resultSet.next();
                return resultSet.getInt(2);
            } catch (SQLException ex) {
                error = true;
                ex.printStackTrace();
            }
        return -1;
    }

    /**
     * A method to get attendance percentage for every student alongside their names.
     * @return ResultSet Contains 3 columns. ColumnLabel : "SID" , "NAME" , "ATTENDANCE_PERCENTAGE". Returns NULL in case of error.
     */
    public ResultSet getAttendancePercentageListWithName(){
        int classcount = this.getTotalClassCount();
        query ="SELECT * FROM " + classname + "_PERCENTAGE_W_NAME";
        if(classcount > 0)
            if (resultSetHandler()) return resultSet;
        return null;
    }

    /**
     * A method to find out the defaulters with their attendance percentage.
     * N.B. Defaulting parameter is hardcoded in database and can only be changed by DBA.
     * @return ResultSet Contains 2 columns. ColumnLabel : "SID" , "ATTENDANCE_PERCENTAGE". Returns NULL in case of error.
     */
    public ResultSet getAttendanceDefauterList(){
        int classcount = this.getTotalClassCount();
        query ="SELECT * FROM " + classname + "_DEFAULTERS";
        if(classcount > 0)
            if (resultSetHandler()) return resultSet;
        return null;
    }

    /**
     * A method to find out the defaulters with their names and attendance percentage.
     * N.B. Defaulting parameter is hardcoded in database and can only be changed by DBA.
     * @return ResultSet Contains 3 columns. ColumnLabel : "SID" , "NAME" , "ATTENDANCE_PERCENTAGE". Returns NULL in case of error.
     */
    public ResultSet getAttendanceDefauterListWithName(){
        int classcount = this.getTotalClassCount();
        query ="SELECT * FROM " + classname + "_DEFAULTERS_W_NAME";
        if(classcount > 0)
            if (resultSetHandler()) return resultSet;
        return null;
    }

    /**
     * A method to get total number of defaulters in the class.
     * @return int Number of defaulters. Returns -1 in case of error.
     */
    public int getDefaulterCount() {
        query = "SELECT * FROM " + classname + "_DEFAULTERS_COUNT";
        try {
            resultSet = statement.executeQuery(query);
            while(resultSet.next())
                rt = resultSet.getInt("DEFAULTER_COUNT");
            return rt;
        } catch (SQLException ex) {
            error = true;
            ex.printStackTrace();
        }
        return -1; // returns -1 if error
    }

    /**
     * Repetition preventing method.
     * @return Integer nullable integer data type as count.
     */
    private Integer getCount() {
        int count;
        try{
            count = statement.executeUpdate(query);
            return count;
        }
        catch (SQLException ex) {
            error = true;
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Repetition preventing method.
     * @return boolean Whether setting value of rs was successful or not.
     */
    private boolean integerHandler() {
        try {
            resultSet = statement.executeQuery(query);
            while(resultSet.next())
                rt = resultSet.getInt(1);
            return true;
        } catch (SQLException ex) {
            error = true;
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Repetition preventing method.
     * @return boolean Whether assigning resultSet was successful or not.
     */
    private boolean resultSetHandler() {
        try{
            resultSet = statement.executeQuery(query);
            return true;
        }
        catch (SQLException ex){
            error = true;
            ex.printStackTrace();
        }
        return false;
    }
}
