import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;

@WebServlet("/s")
public class HelloServlet extends HttpServlet {
    private int currentWindow = 0;
    private Duel duel = null;
    private boolean isDuelOver = false;
    DatabaseController databaseController = new DatabaseController();
    int second = -1;
    int minute = -1;
    private boolean isDuelActive = false;
    ArrayList<String> log = null;

    public void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws IOException {
        PrintWriter out = httpServletResponse.getWriter();



        if ( currentWindow == 1 ) // duel
        {
            duelPage(out, httpServletRequest, httpServletResponse );
            return;
        }
        //this.process(httpServletRequest,httpServletResponse);
        if ( currentWindow == 2 ) // menu
        {
            menuPage(out,httpServletRequest);
            return;
        }

        createStartPage(out);

        out.println("");



    }
    void createStartPage(PrintWriter out)
    {
        out.println("");
        out.println("");

        out.println("<html>");
        out.println("<body>");

        //process(httpServletRequest,httpServletResponse);
        out.println("<form action=\"\" method=\"post\">");
        out.println("Name:<input type=\"text\" name=\"username\"/><br/><br/>  ");
        out.println("Password:<input type=\"password\" name=\"userpass\"/><br/><br/>  ");
        out.println("<input type=\"submit\" value=\"login\"/>  ");

        out.println("</form>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();

//LOGIN
        if ( request.getParameter("username") != null )
        {
            //process(request,response);

            response.setContentType("text/html");
            out = response.getWriter();
            String name = request.getParameter("username");
            String pass = request.getParameter("userpass");

            AccessController accessController = new AccessController(databaseController);

            AccessStatuses status = accessController.tryAccess(name,pass);
            if ( AccessStatuses.PASSWORD_MATCH == status)
            {
                User user = new User(databaseController, name );
                HttpSession session = request.getSession(true);
                session.setAttribute("Name", name );
                this.menuPage(out, request);
            }
            else if ( AccessStatuses.USER_CREATED == status )
            {
                User user = new User(name );
                HttpSession session = request.getSession(true);
                session.setAttribute("Name", name );
                this.menuPage(out,request);
            } else {
                createStartPage(out);
                out.println("<p>Wrong password</p>");
            }

            //out.println("Login: " + name + " Password: " + pass );
        }
//DUEL
        if ( request.getParameter("duelBtn") != null )
        {
            //out.println("Duel starts here");
            if ( duel == null )
            {
                duel = new Duel();
            }
            //duel.addFighter();
            duelPage(out,request,response);

        }
//ATTACK

        if ( request.getParameter("attackBtn" ) != null )
        {
            if ( !isDuelActive )
            {
                duelPage(out, request, response);
                return;
            }
            if ( duel == null )
            {
                menuPage(out, request);
                return;
            }
            if ( duel.getAmountOfFighters() == 2 ) {
                HttpSession session = request.getSession();
                String attackersName = (String) session.getAttribute("Name");

                AliveStatus attackResult = duel.namedFighterAttacks(attackersName);
                log.add("<p>"+ attackersName + " attacks " + duel.getNotThatUsernameByName(attackersName) + " for " +
                duel.getUserByName(attackersName).getDamage() + " damage</p>");

                if (AliveStatus.DEAD == attackResult) {
                    //isDuelOver = true;

                    UserDAO.addWinToUser(databaseController, attackersName);
                    if (duel.fighter1.getName().equals(attackersName)) {
                        UserDAO.addLoseToUser(databaseController, duel.fighter2.getName());
                    } else {
                        UserDAO.addLoseToUser(databaseController, duel.fighter1.getName());
                    }
                    menuPage(out, request);
                    currentWindow = 2;
                    duel = null;
                    second = -1;
                    isDuelActive = false;
                    log = null;
                    return;

                }
            }


            duelPage(out, request, response);


        }
//EXIT
        if ( request.getParameter("exitBtn") != null )
        {
            out.println("Exit");
        }

        addBottomBar(out);

    }


    private void menuPage(PrintWriter out, HttpServletRequest request )
    {
        HttpSession session = request.getSession();
        User user = UserDAO.createUserFromDatabase(databaseController, (String)session.getAttribute("Name") );
        out.println("<html>");
        out.println("<body>");
        out.println("<p>" + user.getName()+" HP: " + user.getHP() + " Dmg: " + user.getDamage() + " Rating: " + user.rating + "</p>");
        out.println("<form action=\"\" method=\"post\">");
        out.println("<input type=\"submit\" name=\"duelBtn\" value=\"Duel\" />");
        out.println("<input type=\"submit\" name=\"exitBtn\" value=\"Exit\" />");

        out.println("</form>");
        out.println("</body>");
        out.println("</html>");
        out.println("");

    }
    private void duelPage(PrintWriter out, HttpServletRequest request, HttpServletResponse response )
    {
        currentWindow = 1;
        //if ( Integer.parseInt(response.getHeader("Refresh")) != 30 )
            response.setIntHeader("Refresh", 1 );


        Calendar calendar = new GregorianCalendar();


        String title = "Duel";
        String docType =
                "<!doctype html public \"-//w3c//dtd html 4.0 " + "transitional//en\">\n";

        out.println(docType +
                "<html>\n" +
                "<head><title>" + title + "</title></head>\n"+
                "<body bgcolor = \"#f0f0f0\">\n"
                //"<p>Current Time is: " + hour+":"+minute+":"+second + "</p>\n" //+
                //here we are adding test pogress bar
                //"<progress max=\"60\" value=\"" + second +"\">"
        );



        HttpSession session = request.getSession();
        String name = (String)session.getAttribute("Name");
        User user = null;// = UserDAO.createUserFromDatabase(databaseController, name);
        Fighter fighter = null;// = new Fighter(user);
        if ( null == duel )
            duel = new Duel();
        { // this block decreases amount of database requests
            if (duel.getAmountOfFighters() == 0) {
                user = UserDAO.createUserFromDatabase(databaseController, name);
                fighter = new Fighter(user);
            } else {
                user = null; // User is used here only to create fighter.
                // but there we'll get fighter directly
                if ( duel.fighter1 == null )
                {
                    user = UserDAO.createUserFromDatabase(databaseController, name);
                    fighter = new Fighter(user);
                }
                if (duel.fighter1.getName().equals(name))
                    fighter = duel.fighter1;
            }
        }
        if ( null == fighter ) {
            user = UserDAO.createUserFromDatabase(databaseController, name);
            fighter = new Fighter(user);
        }
        if ( 2 == duel.addFighter(fighter) )
        {
            if ( second == -1 ) {
                second = calendar.get(Calendar.SECOND);
                minute = calendar.get(Calendar.MINUTE);
            }
            int timerValue = 60-( 60*(calendar.get(Calendar.MINUTE) - minute) - (second-calendar.get(Calendar.SECOND)));
            if (timerValue > 0 )
            {
                out.println("Duel starts in : " + timerValue + "</p>\n");
            }
            else {
                response.setIntHeader("Refresh", 30); // i think 30sec is an acceptable refresher
                isDuelActive = true;
            }

            if ( duel.fighter1.getName().equals(name))
            {
                generateUserDuelPanel(out, duel.fighter1, request );
                generateEnemyDuelPanel(out, duel.fighter2, request);
            }
            else
            {
                generateUserDuelPanel(out, duel.fighter2, request );
                generateEnemyDuelPanel(out, duel.fighter1, request);
            }
            if ( log == null )
            {
                log = new ArrayList<>();
            }
            //generateUserDuelPanel(out);
        }
        else
        {
            generateUserDuelPanel(out, duel.fighter1, request );
        }

        generateLog(out);


        addBottomBar(out);

        //out.println("</body></html>");

    }
    private void generateLog(PrintWriter out)
    {
        if ( log != null )
        for ( String it : log ) {
            out.println(it);
        }
    }

    private void generateUserDuelPanel(PrintWriter out, Fighter fighter, HttpServletRequest request)
    {
        out.println("" + fighter.name );
        out.println("<progress max=\""+fighter.getHP()+"\" value=\""
                + fighter.getCurrentHP()+"\"></progress>" );

        out.println( fighter.getHP());
        out.println("<p>Damage: " + fighter.getDamage() + "  Curr HP: " + fighter.getCurrentHP()+ "</p>" );

        out.println("<form action=\"\" method=\"post\">");
        out.println("<input type=\"submit\" name=\"attackBtn\" value=\"Attack!!\" />");
        out.println("</form>");
        //out.println("");
    }
    private void generateEnemyDuelPanel(PrintWriter out, Fighter fighter, HttpServletRequest request)
    {
        out.println("" + fighter.name );
        out.println("<progress max=\""+fighter.getHP()+"\" value=\""
                + fighter.getCurrentHP()+"\"></progress>" );

        out.println( fighter.getHP());
        out.println("<p>Damage: " + fighter.getDamage() + "  Curr HP: " + fighter.getCurrentHP()+ "</p>" );

        out.println("<form action=\"\" method=\"post\">");
        out.println("</form>");
        //out.println("");
    }
    private void addBottomBar(PrintWriter out)
    {
        out.println("<p style = ");


        out.println("\"position: fixed;");
        out.println("z-index: 100;");
        out.println("bottom: 0;");
        out.println("left: 0;");
        out.println("width: 100%;\">");
        out.println("db: " + databaseController.queryCounter + "req(" + databaseController.queryTimeSummary/1000000+"ms)");

        out.println("</p>");
        out.println("");
        out.println("");
    }

}