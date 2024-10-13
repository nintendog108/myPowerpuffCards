package thePowerpuffCards.services;

import thePowerpuffCards.services.models.cards.Card;
import thePowerpuffCards.services.models.cards.ElementType;
import thePowerpuffCards.services.models.cards.monster.MonsterCard;
import thePowerpuffCards.services.models.cards.spell.SpellCard;
import thePowerpuffCards.services.models.cards.monster.MonsterType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BattleService {
    private List<Card> player1Deck;
    private List<Card> player2Deck;
    private String player1;
    private String player2;
    private StringBuilder battleLog;
    /* "As a result of the battle we want to return a log which describes
    the battle in great detail.
    Afterward the player stats (see scoreboard) need to be updated (count of games played and ELO calculation)."*/

    private static final int MAX_ROUNDS = 100;

    public BattleService(String player1, String player2, List<Card> player1Deck, List<Card> player2Deck) {
        this.player1Deck = new ArrayList<>(player1Deck);
        this.player2Deck = new ArrayList<>(player2Deck);
        this.player1 = player1;
        this.player2 = player2;
        this.battleLog = new StringBuilder();
    }

    public String startBattle() {
        int roundCounter = 1;
        Random random = new Random();

        while (roundCounter <= MAX_ROUNDS && !player1Deck.isEmpty() && !player2Deck.isEmpty()) {
            battleLog.append("\nRound: ").append(roundCounter).append(":\n");

            // Zufällige Karte aus jedem Deck wählen
            Card player1Card = player1Deck.get(random.nextInt(player1Deck.size()));
            Card player2Card = player2Deck.get(random.nextInt(player2Deck.size()));

            battleLog.append(player1).append(" hugs: ").append(player1Card.getName()).append(" (").append(player1Card.getDamage()).append(" Damage, ").append(player1Card.getElementType()).append(")\n");
            battleLog.append(player2).append(" hugs: ").append(player2Card.getName()).append(" (").append(player2Card.getDamage()).append(" Damage, ").append(player2Card.getElementType()).append(")\n");

            int roundResult = calculateRoundResult(player1Card, player2Card);

            if (roundResult > 0) {
                // if player 1 won
                battleLog.append(player1).append(" won this round!\n");
                player2Deck.remove(player2Card); // player 2 verliert eine karte
                player1Deck.add(player2Card); // player 1 bekommt eine karte von player 2
            } else if (roundResult < 0) {
                // if player 2 won
                battleLog.append(player2).append(" won this round!\n");
                player1Deck.remove(player1Card);
                player2Deck.add(player1Card);
            } else {
                battleLog.append("Unentschieden! Keine Karten wurden bewegt.\n");
            }

            roundCounter++;
        }

        determineWinner();
        return battleLog.toString(); // this is the requied log
    }

    private int calculateRoundResult(Card player1Card, Card player2Card) {
        // wenn beide Karten monsterkarten sind, sollte der elementtyp keine Rolle spielen
        if (player1Card instanceof MonsterCard && player2Card instanceof MonsterCard) {
            return handleMonsterSpecialties((MonsterCard) player1Card, (MonsterCard) player2Card);
        }

        return calculateDamage(player1Card, player2Card);
    }


    private int handleMonsterSpecialties(MonsterCard card1, MonsterCard card2) {
        if (card1.getMonsterType().equals(MonsterType.GOBLIN) && card2.getMonsterType().equals(MonsterType.DRAGON)) {
            battleLog.append("Goblin ist zu ängstlich, um gegen einen Drachen zu kämpfen!\n");
            return -1; // Goblin verliert automatisch
        }
        if (card1.getMonsterType().equals(MonsterType.WIZARD) && card2.getMonsterType().equals(MonsterType.ORK)) {
            battleLog.append("Wizzard kontrolliert den Ork und gewinnt automatisch!\n");
            return 1; // Wizzard gewinnt automatisch
        }
        if (card1.getMonsterType().equals(MonsterType.KNIGHT) && card2.getElementType() == ElementType.WATER) {
            battleLog.append("Ritter ertrinkt sofort bei Wassersprüchen!\n");
            return -1; // Ritter verliert sofort
        }

        if (card1.getMonsterType().equals(MonsterType.BUBBLES) && card2.getMonsterType().equals(MonsterType.DRAGON)) {
            battleLog.append("Feuerelf entkommt dem Drachenangriff!\n");
            return 1; // Bubbles statt Fire elf entkommt Drachenangriffen

            //TODO: da fehlen noch einige..
        }
        return Integer.compare(card1.getDamage(), card2.getDamage());
    }

    private int calculateDamage(Card card1, Card card2) {
        double damageMultiplier = 1.0;

        // nut zauberkarten beeinflussen den elementtyp
        if (card1 instanceof SpellCard || card2 instanceof SpellCard) {
            if (card1.getElementType() == ElementType.WATER && card2.getElementType() == ElementType.FIRE) {
                damageMultiplier = 2.0; // Wasser schlägt Feuer
            } else if (card1.getElementType() == ElementType.FIRE && card2.getElementType() == ElementType.NORMAL) {
                damageMultiplier = 2.0; // Feuer schlägt Normal
            } else if (card1.getElementType() == ElementType.NORMAL && card2.getElementType() == ElementType.WATER) {
                damageMultiplier = 2.0; // Normal schlägt Wasser
            }
        }

        return (int) (card1.getDamage() * damageMultiplier) - card2.getDamage();
    }

    private void determineWinner() {
        if (player1Deck.isEmpty() && player2Deck.isEmpty()) {
            battleLog.append("it's a draw!\n");
        } else if (player1Deck.isEmpty()) {
            battleLog.append(player2).append(" won the game!\n");
        } else {
            battleLog.append(player1).append(" won the game!\n");
        }
    }
}
