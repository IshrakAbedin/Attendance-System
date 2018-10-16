import java.sql.*;

public class XEadmin {
    private Connection connection; // Connection type object to get connection using driver.
    private String dbURL; // URL for database used.
    private Statement statement; // Statement type object to execute DDL and DML queries.
    private String query; // Holds query for execution by statement object.
    private ResultSet resultSet; // ResultSet type object to return database tables.
    String name; // Username of teacher.
    private String password; // Password for database.
    private boolean error; // Error state. Can be called at any time to check for errors.
    boolean connected; // Connection state. Can be called at any time to check for connectivity.
    private int rt; // Private variable to return int type data by methods.
    private String teacherPassword; // Private variable for password retrieval

    /**
     * Constructor for XEadmin class objects. Creates connection with database.
     * @param _username String Username for admin
     * @param _password String Password that is allocated to the admin
     */
    public XEadmin (String _username, String _password){
        connection = null;
        error = false;
        connected = false;

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
            if(connection != null && !connection.isClosed()){
                connection.close();
            }
        }
        catch (SQLException ex){
            error = true;
            ex.printStackTrace();
        }
    }

    /**
     * A method for checking error state.
     * @return boolean True if there is any error.
     */
    boolean getErrorState(){
        return error;
    }

    /**
     * A method for checking connection state.
     * @return boolean False if there is no connection.
     */
    boolean getConnectonState(){
        return connected;
    }

    /**
     * A method for creating new teacher.
     * @param teacherName String Username for the teacher.
     * @param teacherPassword String Password for the teacher.
     * @return Boolean Returns 'true' if creating teacher is successful. Returns 'false' in case of error.
     */
    public boolean createTeacher (String teacherName, String teacherPassword){
        try{
            query = "INSERT INTO TEACHER_VAULT VALUES ('" + teacherName + "', '" + teacherPassword + "')";
            int rtrn = statement.executeUpdate(query);

            if(rtrn > 0) {
                query = "CREATE USER " + teacherName + " IDENTIFIED BY " + teacherName;
                statement.execute(query);

                query = "GRANT CONNECT, RESOURCE, CREATE SESSION, DBA TO " + teacherName;
                statement.execute(query);

                XEbase newTeacher = new XEbase(teacherName, teacherPassword);
                newTeacher.init();
                newTeacher.close();

                return true;
            }
            else return false;
        }
        catch (SQLException ex){
            error = true;
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * A method to retrieve the password for a specific teacher
     * @param teacherName String Username of the teacher whose password needs to be retrieved,
     * @return String Password of the user. Returns null in case of error.
     */
    public String retrievePassword(String teacherName){
        String rtrn = null;
        try {
            query = "SELECT TPASSWORD FROM TEACHER_VAULT WHERE TNAME = '" + teacherName + "'";
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                rtrn = resultSet.getString("TPASSWORD");
            }
            return rtrn;
        }
        catch (SQLException ex){
            error = true;
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * A method for removing teacher.
     * @param teacherName String Username of the teacher to be removed.
     * @return boolean Returns 'true' is removal is successful. Returns 'false' in case of failure or error.
     */
    public boolean deleteTeacher (String teacherName){
        try {
            teacherPassword = retrievePassword(teacherName);
            if(teacherPassword != null){
                query = "DELETE FROM TEACHER_VAULT WHERE TNAME = '" + teacherName + "'";
                int temp = statement.executeUpdate(query);
                if(temp>0){
                    query = "DROP USER "+ teacherName + " CASCADE";
                    statement.execute(query);
                    return true;
                }
                else return false;
            }
            else return false;

        }
        catch (SQLException ex){
            error = true;
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * A method for creating a new section under a teacher.
     * @param teacherName String Username of the teacher under whom the new section needs to be created.
     * @param sectionName String Name of the new section.
     * @return boolean Returns 'true' if section creation is successful. Returns 'false' in case of error.
     */
    public boolean createSection (String teacherName, String sectionName){
        teacherPassword = retrievePassword(teacherName);
        if(teacherPassword != null){
            XEbase teacher = new XEbase(teacherName,teacherPassword);
            return teacher.createSection(sectionName);
        }
        else return false;
    }

    /**
     * A method for deleting a section under a teacher.
     * @param teacherName String Username of the teacher who's section needs to be deleted.
     * @param sectionName String Name of the section that needs to be deleted.
     * @return boolean Returns 'true' if section deletion is successful. Returns 'false' in case of error.
     */
    public boolean deleteSection (String teacherName, String sectionName){
        teacherPassword = retrievePassword(teacherName);
        if(teacherPassword != null){
            XEbase teacher = new XEbase(teacherName,teacherPassword);
            return teacher.deleteSection(sectionName);
        }
        else return false;
    }

    /**
     * A method for creating a new class of a specific section under a teacher.
     * @param teachername String Username of the teacher under whom new class needs to be created.
     * @param sectionname String Owning section of the new class.
     * @param classname String Name of the new class.
     * @return boolean Returns 'true' if class creation is successful. Returns 'false' in case of error.
     */
    public boolean createClass (String teachername, String sectionname, String classname){
        teacherPassword = retrievePassword(teachername);
        if(teacherPassword != null){
            try {
                query = "INSERT INTO MANAGERCLASSHUB VALUES ('" + teachername + "', '" + sectionname + "', '" + classname + "')";
                int temp = statement.executeUpdate(query);
                if(temp > 0){
                    XEbase teacher = new XEbase(teachername,teacherPassword);
                    return teacher.createClass(classname, sectionname);
                }
                else return false;
            }
            catch (SQLException ex){
                error = true;
                ex.printStackTrace();
            }
        }
        return false;
    }

    /**
     * A method to delete a class of a specific section under a teacher.
     * @param teachername String Username name of the teacher who's class needs to be deleted.
     * @param sectionname String Name of the owning section of the class.
     * @param classname String Name of the class that needs to be deleted.
     * @return boolean Returns 'true' if deletion is successful. Returns 'false' in case of error.
     */
    public boolean deleteClass (String teachername, String sectionname, String classname){
        teacherPassword = retrievePassword(teachername);
        if(teacherPassword != null){
            try {
                query = "DELETE FROM MANAGERCLASSHUB WHERE TNAME = '" + teachername + "' AND SECTION = '" + sectionname + "' AND CLASS = '" + classname + "'";
                int temp = statement.executeUpdate(query);
                if(temp > 0){
                    XEbase teacher = new XEbase(teachername,teacherPassword);
                    return teacher.deleteClass(classname, sectionname);
                }
                else return false;
            }
            catch (SQLException ex){
                error = true;
                ex.printStackTrace();
            }
        }
        return false;
    }

    /**
     * A method for inserting new student into a section
     * @param teachername String Username of the teacher who owns the section.
     * @param sectionname String Name of the section.
     * @param SID String Student ID of the new student.
     * @param name String Name of the new student.
     * @param address String Address of the new student.
     * @param contact_number String Contact number of the new student.
     * @return boolean Returns 'true' if student insertion is successful. Returns 'false' in case of error.
     */
    public boolean insertStudent(String teachername, String sectionname, String SID, String name, String address, String contact_number){
        teacherPassword = retrievePassword(teachername);
        if(teacherPassword != null){
            XEbase teacher = new XEbase(teachername,teacherPassword);
            return teacher.insertStudent(sectionname,SID,name,address,contact_number);
        }
        else return false;
    }

    /**
     * A method to delete existing student of a specific section under a teacher.
     * @param teachername String Username of the teacher who owns the section.
     * @param sectionname String Name of the section.
     * @param SID String Student ID of the student that needs to be removed.
     * @return boolean Returns 'true' if deletion is successful. Returns 'false' in case of error.
     */
    public boolean deleteStudent(String teachername,String sectionname, String SID){
        teacherPassword = retrievePassword(teachername);
        if(teacherPassword != null){
            XEbase teacher = new XEbase(teachername,teacherPassword);
            return teacher.deleteStudent(sectionname,SID);
        }
        else return false;
    }

    /**
     * A method to get information about teacher, section and corresponding courses.
     * @return ResultSet Contains 3 columns. ColumnLabel : "TNAME" (teacher's name) , "SECTION" , "CLASS". Returns NULL in case of error.
     */
    public ResultSet getCompleteTeacherSectionCourseList (){
        query = "SELECT * FROM MANAGERCLASSHUB";
        if (resultSetHandler()) return resultSet;
        else return null;
    }

    /**
     * A method to get the sections and corresponding courses under a certain teacher.
     * @param teachername String Username of the teacher who's section and courses needs to be retrieved.
     * @return ReultSet Contains 2 columns. ColumnLabel : "SECTION" , "CLASS". Returns NULL in case of error.
     */
    public ResultSet getSectionCourseByTeacherList (String teachername) {
        query = "SELECT SECTION, CLASS FROM MANAGERCLASSHUB WHERE TNAME = '" + teachername + "'";
        if (resultSetHandler()) return resultSet;
        else return null;
    }

    /**
     * A method to get the username of all teachers.
     * @return ResultSet Contains 1 column. ColumnLabel : "TNAME" (teacher's name). Returns NULL in case of error.
     */
    public ResultSet getTeacherList () {
        query = "SELECT UNIQUE TNAME FROM TEACHER_VAULT";
        if (resultSetHandler()) return resultSet;
        else return null;
    }

    /**
     * A method for getting the student lists of a section under a certain teacher.
     * @param teacherName String Username of the teacher.
     * @param sectionName String Name of the section.
     * @return ResultSet Contains 4 columns. ColumnLabel: "SID" , "NAME" , "ADDRESS" , "CONTACT_NUMBER".
     * Returns NULL in case of error.
     */
    public ResultSet getStudentListByTeacher (String teacherName, String sectionName){
        teacherPassword = retrievePassword(teacherName);
        if (teacherPassword != null){
            XEbase teacher = new XEbase(teacherName, teacherPassword);
            if (teacher != null){
                teacher.setSectionname(sectionName);
                return teacher.getStudentList();
            }
            else return null;
        }
        else return null;
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
