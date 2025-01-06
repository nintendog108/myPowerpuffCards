package thePowerpuffCards.core.services;

import thePowerpuffCards.core.models.cards.Card;
import thePowerpuffCards.core.models.cards.ElementType;
import thePowerpuffCards.core.models.cards.monster.MonsterCard;
import thePowerpuffCards.core.models.cards.spell.SpellCard;
import thePowerpuffCards.core.models.cards.monster.MonsterType;
import thePowerpuffCards.persistence.dao.UsersDaoDb;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BattleService {
    private List<Card> player1Deck;
    private List<Card> player2Deck;
    private String player1;
    private String player2;
    private StringBuilder battleLog;
    private UsersDaoDb usersDao;

    private static final int MAX_ROUNDS = 100;

    public BattleService(String player1, String player2, List<Card> player1Deck, List<Card> player2Deck, UsersDaoDb usersDao) {
        this.player1Deck = new ArrayList<>(player1Deck);
        this.player2Deck = new ArrayList<>(player2Deck);
        this.player1 = player1;
        this.player2 = player2;
        this.battleLog = new StringBuilder();
        this.usersDao = usersDao;
    }
/*
  * Special interactions are checked in the following order:
 * 1. Goblins are afraid of Dragons → Goblin automatically loses.
 * 2. Wizards can control Orks → Wizard automatically wins.
 * 3. Knights drown against Water-type monsters → Knight automatically loses.
 * 4. Krakens are immune to spells → Kraken automatically wins against spell cards.
 * 5. Fire Elves can evade Dragons → Fire Elf automatically wins.
 *
 * If no special conditions apply, the card with the higher damage value wins.*/
    public String startBattle() {
        battleLog.setLength(0); // alte Logs löschen
        battleLog.append("Starting battle: ").append(player1).append(" vs ").append(player2).append("\n");

        System.out.println("Starting battleeeeee: " + player1 + " vs " + player2); // Nur hier loggen
        System.out.println("Player 1 deck size: " + player1Deck.size());
        System.out.println("Player 2 deck size: " + player2Deck.size());

        if (player1Deck.isEmpty() || player2Deck.isEmpty()) {
            battleLog.append("One or both players have no valid deck.\n");
            return battleLog.toString();
        }

        int roundCounter = 1;
        Random random = new Random();
        boolean player1BoosterUsed = false;
        boolean player2BoosterUsed = false;
        int player1Wins = 0;
        int player1Losses = 0;
        int draws = 0;

        while (roundCounter <= MAX_ROUNDS && !player1Deck.isEmpty() && !player2Deck.isEmpty()) {
            battleLog.append("\nRound ").append(roundCounter).append(":\n");

            Card player1Card = player1Deck.get(random.nextInt(player1Deck.size()));
            Card player2Card = player2Deck.get(random.nextInt(player2Deck.size()));

            if (!player1BoosterUsed && random.nextBoolean()) {
                applyBooster(player1Card);
                player1BoosterUsed = true;
                battleLog.append(player1).append(" activated a Power-Up Booster!\n").append("Card: ").append(player1Card.getName()).append("\n").append(" | New Damage: ").append(player1Card.getDamage()).append("\n");
            //    System.out.println("Player 1 using booster");
            }
            if (!player2BoosterUsed && random.nextBoolean()) {
                applyBooster(player2Card);
                player2BoosterUsed = true;
                battleLog.append(player2).append(" activated a Power-Up Booster!\n").append("Card: ").append(player2Card.getName()).append("\n").append(" | New Damage: ").append(player2Card.getDamage()).append("\n");
              //  System.out.println("Player 2 using booster");
            }

            formatRoundLog(player1, player1Card);
            formatRoundLog(player2, player2Card);

            int roundResult = calculateRoundResult(player1Card, player2Card);

            if (roundResult > 0) {
                battleLog.append(player1).append(" won this round!\n");
                player2Deck.remove(player2Card);
                player1Deck.add(player2Card);
                player1Wins++;
            } else if (roundResult < 0) {
                battleLog.append(player2).append(" won this round!\n");
                player1Deck.remove(player1Card);
                player2Deck.add(player1Card);
                player1Losses++;
            } else {
                battleLog.append("It's a draw! No cards were moved.\n");
                draws++;
            }

            roundCounter++;
        }
        saveDeckChanges();
        determineWinner();
        System.out.println("===== Battle Results =====");
        System.out.println(player1 + " won " + player1Wins + " rounds.");
        System.out.println(player1 + " lost " + player1Losses + " rounds.");
        System.out.println("Draws: " + draws);
        System.out.println(player2 + " won " + player1Losses + " rounds.");
        System.out.println(player2 + " lost " + player1Wins + " rounds.");
        System.out.println("=========================");

        return battleLog.toString();
    }
/*
    private void saveDeckChanges() {
        usersDao.clearDeck(player1);
        usersDao.saveDeck(player1, player1Deck);
        usersDao.clearDeck(player2);
        usersDao.saveDeck(player2, player2Deck);
    }
*/

private void saveDeckChanges() {
    if (!player1Deck.isEmpty() && usersDao.getDeck(player1).size() > 0) {
        System.out.println("DEBUG: deleting old deck for  " + player1);
        usersDao.clearDeck(player1);
        usersDao.saveDeck(player1, player1Deck);
    }/* else {
        System.out.println("DEBUG: nothing to save");
    }*/

    if (!player2Deck.isEmpty() && usersDao.getDeck(player2).size() > 0) {
        System.out.println("DEBUG: deleting old deck for " + player2);
        usersDao.clearDeck(player2);
        usersDao.saveDeck(player2, player2Deck);
    } /*else {
        System.out.println("DEBUG: nothing to save");
    } */
}

/*
* Mit einer 50% Wahrscheinlichkeit erhält ein Spieler ein Power-Up Booster.
* Sobald der Booster aktiviert ist, wird die applyBooster() aufgerufen
* und diese Methode verdoppelt den aktuellen Schaden der card
* */
    private void applyBooster(Card card) {
        if (card != null) {
            System.out.println("Applying booster to card: " + card.getName());
            // Booster-Effekt: Schaden verdoppeln
            double boostedDamage = card.getDamage() * 2;
            card.setDamage(boostedDamage);
        }
    }


    public int calculateRoundResult(Card player1Card, Card player2Card) {
        if (player1Card instanceof MonsterCard && player2Card instanceof MonsterCard) {
            return handleMonsterSpecialties((MonsterCard) player1Card, (MonsterCard) player2Card);
        }

        return calculateDamage(player1Card, player2Card);
    }

    private int handleMonsterSpecialties(MonsterCard card1, MonsterCard card2) {
        if (isGoblinAfraidOfDragon(card1, card2)) return -1;
        if (isGoblinAfraidOfDragon(card2, card1)) return 1;

        if (isWizardControllingOrk(card1, card2)) return 1;
        if (isWizardControllingOrk(card2, card1)) return -1;

        if (isKnightDrowning(card1, card2)) return -1;
        if (isKnightDrowning(card2, card1)) return 1;

        if (isKrakenImmuneToSpell(card1, card2)) return 1;
        if (isKrakenImmuneToSpell(card2, card1)) return -1;

        if (isFireElfEvadingDragon(card1, card2)) return 1;
        if (isFireElfEvadingDragon(card2, card1)) return -1;

        return Double.compare(card1.getDamage(), card2.getDamage());
    }

    private boolean isFireElfEvadingDragon(MonsterCard elf, MonsterCard dragon) {
        return elf.getName().toLowerCase().contains("fireelf") &&
                dragon.getName().toLowerCase().contains("dragon");
    }

    private boolean isGoblinAfraidOfDragon(MonsterCard goblin, MonsterCard dragon) {
        return MonsterType.getMonsterType(goblin.getName()) == MonsterType.GOBLIN &&
                MonsterType.getMonsterType(dragon.getName()) == MonsterType.DRAGON;
    }

    private boolean isWizardControllingOrk(MonsterCard wizard, MonsterCard ork) {
        return MonsterType.getMonsterType(wizard.getName()) == MonsterType.WIZARD &&
                MonsterType.getMonsterType(ork.getName()) == MonsterType.ORK;
    }

    private boolean isKnightDrowning(MonsterCard knight, MonsterCard opponent) {
        return MonsterType.getMonsterType(knight.getName()) == MonsterType.KNIGHT &&
                opponent.getElementType() == ElementType.WATER;
    }

    private boolean isKrakenImmuneToSpell(MonsterCard kraken, Card opponent) {
        return MonsterType.getMonsterType(kraken.getName()) == MonsterType.KRAKEN &&
                opponent instanceof SpellCard;
    }
/*
*  * - Water beats Fire → Damage is doubled (×2.0).
 * - Fire beats Normal → Damage is doubled (×2.0).
 * - Normal beats Water → Damage is doubled (×2.0).
 * - Fire is weak against Water → Damage is halved (×0.5).
 * - Water is weak against Normal → Damage is halved (×0.5).
 * - Normal is weak against Fire → Damage is halved (×0.5).
* */
    private int calculateDamage(Card card1, Card card2) {
        double damageMultiplier = 1.0;

        if (card1 instanceof SpellCard || card2 instanceof SpellCard) {
            if (card1.getElementType() == ElementType.WATER && card2.getElementType() == ElementType.FIRE) {
                damageMultiplier = 2.0;
            } else if (card1.getElementType() == ElementType.FIRE && card2.getElementType() == ElementType.NORMAL) {
                damageMultiplier = 2.0;
            } else if (card1.getElementType() == ElementType.NORMAL && card2.getElementType() == ElementType.WATER) {
                damageMultiplier = 2.0;
            } else if (card1.getElementType() == ElementType.FIRE && card2.getElementType() == ElementType.WATER) {
                damageMultiplier = 0.5;
            } else if (card1.getElementType() == ElementType.WATER && card2.getElementType() == ElementType.NORMAL) {
                damageMultiplier = 0.5;
            } else if (card1.getElementType() == ElementType.NORMAL && card2.getElementType() == ElementType.FIRE) {
                damageMultiplier = 0.5;
            }
        }

        return (int) ((card1.getDamage() * damageMultiplier) - card2.getDamage());
    }

    private void determineWinner() {
        if (player1Deck.isEmpty() && player2Deck.isEmpty()) {
            battleLog.append("It's a draw!\n");
            updateStats(null, null);
        } else if (player1Deck.isEmpty()) {
            battleLog.append(player2).append(" won the game!\n");
            updateStats(player2, player1);
        } else {
            battleLog.append(player1).append(" won the game!\n");
            updateStats(player1, player2);
        }
    }

    private void updateStats(String winner, String loser) {
        usersDao.incrementGamesPlayed(player1);
        usersDao.incrementGamesPlayed(player2);

        if (winner != null) {
            usersDao.incrementGamesWon(winner);
            usersDao.incrementGamesLost(loser);
            usersDao.updateElo(winner, 3);
            usersDao.updateElo(loser, -5);
        }
    }

    private void formatRoundLog(String player, Card card) {
        battleLog.append(String.format("%s plays: %-15s | Damage: %-5.1f | Element: %s\n",
                player, card.getName(), card.getDamage(), card.getElementType()));
    }
}
