/**
 * this class represents User during fight. It allows more actions
 * with damage and hp
 *
 */

public class Fighter extends User
{
    int currentHP = 0;

    Fighter(User user)
    {
        this.hp = user.getHP();
        this.damage = user.getDamage();
        this.rating = user.getRating();
        this.name = user.getName();
        this.currentHP = this.hp;
    }
    void modifyHPbyValue(int value)
    {
        hp += value;
    }
    void modifyDamagebyValue(int value)
    {
        damage += value;
    }
    int getCurrentHP()
    {
        return currentHP;
    }

}
