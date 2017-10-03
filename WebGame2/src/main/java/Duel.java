public class Duel
{
    Fighter fighter1=null;
    Fighter fighter2=null;
    private int amountOfFighters = 0;

    Duel(){};
    Duel(User user1, User user2)
    {
        fighter1 = new Fighter(user1);
        fighter2 = new Fighter(user2);
    }
    AliveStatus namedFighterAttacks(String name)
    {
        if ( fighter1.getName().equals(name))
        {
            return firstFighterAttacks();
        }
        else {
            return secondFighterAttacks();
        }
    }
    AliveStatus firstFighterAttacks()
    {
        fighter2.modifyHPbyValue( -fighter1.getDamage());
        if ( fighter2.hp <= 0 )
        {
            return AliveStatus.DEAD;
        }
        return AliveStatus.ALIVE;
    }
    AliveStatus secondFighterAttacks()
    {
        fighter1.modifyHPbyValue(-fighter2.getDamage());
        if ( fighter1.hp <= 0 )
        {
            return AliveStatus.DEAD;
        }
        return AliveStatus.ALIVE;
    }

    int addFighter(Fighter fighter) // returns amount of fighters active
    {
        if ( fighter1 == null )
        {
            fighter1 = fighter;
            amountOfFighters = 1;
            return 1;
        }
        else if ( fighter2 == null && !fighter1.getName().equals(fighter.getName()) )
        {
            fighter2 = fighter;
            amountOfFighters = 2;
            return 2;
        }
        if ( fighter2 == null && fighter1.getName().equals(fighter.getName()))
            return 1;
        return 2;
    }

    String getFighter1Name()
    {
        return fighter1.getName();
    }
    String getFighter2Name()
    {
        return fighter2.getName();
    }
    int getAmountOfFighters()
    {
        return amountOfFighters;
    }
    User getUserByName(String name)
    {
        if ( fighter1.getName().equals(name))
            return fighter1;
        else if (fighter2.getName().equals(name) )
            return fighter2;
        else
            return null;
    }
    String getNotThatUsernameByName(String name)
    {
        if ( !fighter1.getName().equals(name))
            return fighter1.getName();
        else if (!fighter2.getName().equals(name) )
            return fighter2.getName();
        else
            return null;
    }
}
