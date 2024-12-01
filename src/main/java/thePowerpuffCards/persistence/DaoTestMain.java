package thePowerpuffCards.persistence;
import java.util.Optional;


import thePowerpuffCards.persistence.dao.CardDaoDb;
import thePowerpuffCards.persistence.dao.UsersDaoDb;
import thePowerpuffCards.services.models.User;
import thePowerpuffCards.services.models.cards.ElementType;
import thePowerpuffCards.services.models.cards.monster.MonsterCard;
import thePowerpuffCards.services.models.cards.spell.SpellCard;

public class DaoTestMain {
    public static void main(String[] args) {
        UsersDaoDb usersDaoDb = new UsersDaoDb();
        CardDaoDb cardDao = new CardDaoDb();

        // Test: Neuen Benutzer speichern
        User user = new User("justaUser", "user");
        usersDaoDb.save(user);
        System.out.println("User saved: " + user);

        // Test: Benutzer abrufen
        Optional<User> retrievedUser = usersDaoDb.get(user.getId());
        System.out.println("User retrieved: " + retrievedUser.orElse(null));


        // MonsterCard erstellen
        MonsterCard dragon = new MonsterCard("Fire Dragon", 50, ElementType.FIRE, "DRAGON");
        cardDao.saveCard(dragon);
        System.out.println("MonsterCard gespeichert mit ID: " + dragon.getId());

        // SpellCard erstellen
        SpellCard fireSpell = new SpellCard("Fireball", 40, ElementType.FIRE);
        cardDao.saveCard(fireSpell);
        System.out.println("SpellCard gespeichert mit ID: " + fireSpell.getId());
    }


        // Test: Benutzer aktualisieren
       // user.setUsername("updatedBubbles");
      //  usersDaoDb.update(user, new String[]{"1", "updatedBubbles", "newBubbles"});
      //  System.out.println("User updated: " + user);

        // Test: Benutzer löschen
        //usersDaoDb.delete(user);
        //System.out.println("User deleted: " + user);
    }
