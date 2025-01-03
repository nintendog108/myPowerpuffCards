package testing;
// LÄUFT 8/3
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thePowerpuffCards.core.models.cards.*;
import thePowerpuffCards.core.models.cards.monster.MonsterCard;
import thePowerpuffCards.core.models.cards.monster.MonsterType;
import thePowerpuffCards.core.models.cards.spell.SpellCard;
import thePowerpuffCards.core.services.BattleService;
import thePowerpuffCards.persistence.dao.UsersDaoDb;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.List;

public class BattleServiceTest {
    private BattleService battleService;
    private UsersDaoDb usersDao;

    @BeforeEach
    void setup() {
        usersDao = mock(UsersDaoDb.class); // Mock UsersDaoDb
    }
/*
    @Test
    void testStartBattle_Player1Wins() throws Exception {
        StringWriter stringWriter = new StringWriter(); // Initialisiere StringWriter
        BufferedWriter out = new BufferedWriter(stringWriter); // Verbinde mit BufferedWriter

        List<Card> deck1 = List.of(new MonsterCard("card1", "Dragon", 50.0, ElementType.FIRE, MonsterType.DRAGON));
        List<Card> deck2 = List.of(new MonsterCard("card2", "Goblin", 20.0, ElementType.NORMAL, MonsterType.GOBLIN));

        BattleService battleService = new BattleService("player1", "player2", deck1, deck2, mock(UsersDaoDb.class), out);

        String result = battleService.startBattle();

        System.out.println("Battle Log: " + stringWriter.toString()); // Debugging-Ausgabe
        assertTrue(result.contains("player1 wins the battle!"));
    }



    @Test
    void testStartBattle_Draw() {
        // Beide Spieler haben leere Decks
        List<Card> deck1 = List.of();
        List<Card> deck2 = List.of();

        battleService = new BattleService("player1", "player2", deck1, deck2, usersDao);

        String result = battleService.startBattle();

        assertTrue(result.contains("The battle ends in a draw."));
    }

    @Test
    void testKrakenImmuneToSpell() {
        // Kraken gegen Zauber
        Card kraken = new MonsterCard("card1", "Kraken", 50.0, ElementType.WATER, MonsterType.KRAKEN);
        Card spell = new SpellCard("card2", "Fireball", 60.0, ElementType.FIRE);

        battleService = new BattleService("player1", "player2", List.of(kraken), List.of(spell), usersDao);

        int result = battleService.calculateRoundResult(kraken, spell);

        assertTrue(result > 0); // Kraken gewinnt
    }

    @Test
    void testGoblinCannotFightDragon() {
        // Goblin gegen Drachen (Spezialregel)
        Card goblin = new MonsterCard("card1", "Goblin", 30.0, ElementType.NORMAL, MonsterType.GOBLIN);
        Card dragon = new MonsterCard("card2", "Dragon", 50.0, ElementType.FIRE, MonsterType.DRAGON);

        battleService = new BattleService("player1", "player2", List.of(goblin), List.of(dragon), usersDao);

        int result = battleService.calculateRoundResult(goblin, dragon);

        assertTrue(result < 0); // Goblin verliert
    }

    @Test
    void testRoundWithBooster() {
        // Runde mit Element-Booster
        Card fireSpell = new SpellCard("card1", "Fireball", 40.0, ElementType.FIRE);
        Card waterMonster = new MonsterCard("card2", "WaterDragon", 30.0, ElementType.WATER, MonsterType.DRAGON);

        battleService = new BattleService("player1", "player2", List.of(fireSpell), List.of(waterMonster), usersDao);

        int result = battleService.calculateRoundResult(fireSpell, waterMonster);

        assertTrue(result > 0); // Fireball gewinnt wegen Booster gegen Wasser
    }

    @Test
    void testStartBattle_MaxRoundsReached() {
        // Beide Spieler mit gleichen Kartenwerten
        List<Card> deck1 = List.of(new MonsterCard("card1", "Dragon", 50.0, ElementType.NORMAL, MonsterType.DRAGON));
        List<Card> deck2 = List.of(new MonsterCard("card2", "Dragon", 50.0, ElementType.NORMAL, MonsterType.DRAGON));

        battleService = new BattleService("player1", "player2", deck1, deck2, usersDao);

        String result = battleService.startBattle();

        assertTrue(result.contains("The battle ended after 100 rounds with no winner."));
    }

    @Test
    void testBattleWithEmptyDecks() {
        // Spieler mit leeren Decks
        battleService = new BattleService("player1", "player2", List.of(), List.of(), usersDao);

        String result = battleService.startBattle();

        assertTrue(result.contains("One or both players have no valid deck."));
    }

    @Test
    void testBattleWithNullDecks() {
        // Spieler mit null Decks
        battleService = new BattleService("player1", "player2", null, null, usersDao);

        String result = battleService.startBattle();

        assertTrue(result.contains("One or both players have no valid deck."));
    }*/
}
