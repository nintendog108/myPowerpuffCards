package thePowerpuffCards.services;

import thePowerpuffCards.services.models.cards.Card;

import thePowerpuffCards.services.models.cards.monster.MonsterCard;
import thePowerpuffCards.services.models.cards.monster.MonsterType;
import thePowerpuffCards.services.models.cards.spell.SpellCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Battle {
    private List<Card> player1Deck;
    private List<Card> player2Deck;
    private String player1;
    private String player2;

    private static final int MAX_ROUNDS = 100;

    public Battle(String player1, String player2, List<Card> player1Deck, List<Card> player2Deck) {
        this.player1Deck = new ArrayList<>(player1Deck); // deck kopie, so könenn wir die änderungen simulieren
        this.player2Deck = new ArrayList<>(player2Deck);
        this.player1 = player1;
        this.player2 = player2;
    }

    public void startBattle() {
        int roundCounter = 1;
        Random random = new Random();

        while (roundCounter < MAX_ROUNDS && !player1Deck.isEmpty() && !player2Deck.isEmpty()) {
            System.out.println("\n round " + (roundCounter) + ":");

            //  wir wählen eine zufällige Karte aus
            Card player1Card = player1Deck.get(random.nextInt(player1Deck.size()));
            Card player2Card = player2Deck.get(random.nextInt(player2Deck.size()));

            System.out.println(player1 + " plays: " + player1Card.getName() + " (" + player1Card.getDamage() + " damage, " + player1Card.getElementType() + ")");
            System.out.println(player2 + " plays: " + player2Card.getName() + " (" + player2Card.getDamage() + " damage, " + player2Card.getElementType() + ")");

            int roundResult = calculateRoundResult(player1Card, player2Card);

            if (roundResult > 0) {
                // Player 1 gewinnt
                System.out.println(player1 + " wins the round!");
                player2Deck.remove(player2Card); // remove the loooser card
            } else if (roundResult < 0) {
                // Player 2 gewinnt
                System.out.println(player2 + " wins the round!");
                player1Deck.remove(player1Card); //again
            } else {
                System.out.println("ot's a draw!");
            }

            roundCounter++;
        }

        determineWinner();
    }

    private int calculateRoundResult(Card player1Card, Card player2Card) {
        //TODO: calculate the results
        return 0;
    }

    private int calculateDamage(Card attackingCard, Card defendingCard) {
    return 0;   //TODO: calculate damage
    }

    private void determineWinner() {
        if (player1Deck.isEmpty() && player2Deck.isEmpty()) {
            System.out.println("it's a draw");
        } else if (player1Deck.isEmpty()) {
            System.out.println(player2 + " wins the battle!");
        } else {
            System.out.println(player1 + " wins the battle!");
        }
    }
}
