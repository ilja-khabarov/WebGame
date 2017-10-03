import java.sql.*;

public class DatabaseController {

    Connection connection;
    int queryCounter = 0;
    long queryTimeSummary = 0;

    DatabaseController()
    {
        connect();
    }
    void connect()
    {
        if ( connection != null ) // should be an exception here
            return;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost/WebGame?" +
                    "useSSL=false&user=root&password=1337");

        }
        catch(ClassNotFoundException cnf)
        {
            cnf.printStackTrace();
        }
        catch (InstantiationException ine )
        {
            ine.printStackTrace();
        }
        catch(IllegalAccessException ille )
        {
            ille.printStackTrace();
        }
        catch (SQLException sqlex )
        {
            sqlex.printStackTrace();
        }

    }
    Connection getConnection()
    {
        return connection;
    }
    String generateInsertStatementForUser(String name, String pass)
    {
        return ("insert into users(name,password,health,damage,rating) values ('"
        +name+"','"+pass+"', 100,10,0)");
    }
    AccessStatuses checkUser(String name, String pass )
    {
        long pretime, posttime;
        try {
            Statement statement = connection.createStatement();

            pretime = System.nanoTime();
            ResultSet resultSet = statement.executeQuery(
                    "select * from users where name = '" +
                            name+
                            "';");
            queryCounter++;
            posttime = System.nanoTime();
            queryTimeSummary += posttime-pretime;
            if ( !resultSet.next()) // if the set is not empty
            {
                System.out.println("\nUser created");
                pretime = System.nanoTime();
                posttime = System.nanoTime();
                queryCounter++;
                queryTimeSummary += posttime-pretime;
                statement.execute(generateInsertStatementForUser(name,pass));
                statement.close();
                return AccessStatuses.USER_CREATED;
            }
            else if ( resultSet.getString(2).equals(pass) )
            {
                System.out.println("\nPasswords match");

                return AccessStatuses.PASSWORD_MATCH;
            }
        }
        catch (SQLException sqlEx )
        {
            sqlEx.printStackTrace();
        }
        System.out.println("\nPassword Wrong");

        return AccessStatuses.PASSWORD_WRONG;
    }
    public static void main(String[] args)
    {
        DatabaseController databaseController = new DatabaseController();
        databaseController.connect();
    }
    public void incQueryCounter()
    {
        queryCounter++;
    }
    public void increaseTime(long addition)
    {
        queryTimeSummary += addition;
    }
}
