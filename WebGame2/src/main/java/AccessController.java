public class AccessController
{
    DatabaseController databaseController;


    AccessStatuses tryAccess(String name, String password)
    {
        return databaseController.checkUser(name, password);
    }

    AccessController()
    {
        this.databaseController = new DatabaseController();
    }
    AccessController(DatabaseController databaseController)
    {
        this.databaseController = databaseController;
    }

}
