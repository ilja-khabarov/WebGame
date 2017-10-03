import javax.servlet.http.HttpSession;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class User
{
    String name;
    String pass;
    int hp = 100;
    int damage = 10;
    int rating = 0;

    User()
    {
        this.name = "DOE";
        this.pass = "123";
    }
    User(String name)
    {
        this.name = name;
    }
    User(String name, String pass, int hp, int damage, int rating)
    {
        this.name = name;
        this.pass = pass;
        this.hp = hp;
        this.damage = damage;
        this.rating = rating;
    }
    User(DatabaseController databaseController, String name )
    {
        UserDAO.createUserFromDatabase(databaseController, name);
    }

    void incHP()
    {
        hp++;
    }
    void incDamage()
    {
        damage++;
    }
    void incRating()
    {
        rating++;
    }
    void decRating()
    {
        rating--;
    }


    int getHP(){return hp;}
    int getDamage(){return damage;}
    int getRating(){return rating;};
    String getName(){return name;};
    String getPass(){return pass;};
}
