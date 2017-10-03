import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDAO
{
    public static User createUserFromDatabase(DatabaseController databaseController, String name )
    {
        User user=null;
        String pswd = null;
        int hp, dmg, rating;
        String query = null;
        long pretime, posttime;

        try {
            query = "select * from users where name='"+name+"';";
            Statement statement = databaseController.getConnection().createStatement();

            pretime = System.nanoTime();


            ResultSet resultSet = statement.executeQuery(query);

            posttime = System.nanoTime();
            databaseController.incQueryCounter();
            databaseController.increaseTime(posttime-pretime);
            //ResultSetMetaData rsmd = resultSet.getMetaData();

            if ( !resultSet.next() )
                throw new SQLException("Error with resultSet in UserDAO");

            hp = resultSet.getInt(3);
            dmg = resultSet.getInt(4);
            rating = resultSet.getInt(5);

            return new User(name, null, hp, dmg, rating );
            // to not put password in User is a good idea I think


        }
        catch ( SQLException sqlEx )
        {
            System.out.println("Name problems:( " + name + " - " + query);
            sqlEx.printStackTrace();
        }
        return new User();
    }
    static public void addWinToUser(DatabaseController databaseController, String name)
    {
        long pretime, posttime;

        String query = "update users set health = health+1, damage = damage+1, rating = rating+1 " +
                " where name = '" +name+"'";
        try
        {
            Statement statement = databaseController.getConnection().createStatement();
            pretime = System.nanoTime();

            statement.execute(query);

            posttime = System.nanoTime();
            databaseController.incQueryCounter();
            databaseController.increaseTime(posttime-pretime);


        }
        catch (SQLException sqlEx )
        {
            sqlEx.printStackTrace();
        }
    }
    static public void addLoseToUser(DatabaseController databaseController, String name)
    {
        long pretime, posttime;

        String query = "update users set health = health+1, damage = damage+1 " +
                " where name = '" +name+"'";
        try
        {
            Statement statement = databaseController.getConnection().createStatement();
            pretime = System.nanoTime();

            statement.execute(query);

            posttime = System.nanoTime();
            databaseController.incQueryCounter();
            databaseController.increaseTime(posttime-pretime);
        }
        catch (SQLException sqlEx )
        {
            sqlEx.printStackTrace();
        }
    }

}
