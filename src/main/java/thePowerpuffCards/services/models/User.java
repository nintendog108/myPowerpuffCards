package thePowerpuffCards.services.models;

import thePowerpuffCards.services.models.cards.Card;
import thePowerpuffCards.services.models.cards.Deck;
import thePowerpuffCards.services.models.cards.Stack;

import java.util.List;

public class User {
    private String username;
    private String password;
    private int coins;
    private Stack stack;
    private Deck deck;
    private int ELO;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.coins = 20;
        this.stack = new Stack();
        this.deck = new Deck();
        this.ELO = 100;
    }
    public void addCardsToStack(List<Card> newCards) {
        for (Card card : newCards) {
            stack.addCard(card);
        }
        System.out.println(" just added " + newCards.size() + " new cards to the stack.");
    }

    public void defineDeck(List<Card> selectedCards) {
        if (selectedCards.size() != 4) {
            throw new IllegalArgumentException("deck must consist of exactly 4 cards.");
        }

        for (Card card : selectedCards) { //checken if alle karten vorhanden sind
            if (!stack.getCards().contains(card)) {
                throw new IllegalArgumentException("Card " + card.getName() + " is not in the stack.");
            }  //i love this illegal argument lol
        }
        deck.defineDeck(selectedCards);  // die selected cards werden als deck gesetzt
        System.out.println("Deck defined with 4 cards.");
    }

    // beste karten auswählen und das deck setzen
    public void autoSelectBestDeck() {
        deck.selectBestCardsFromCollection(stack.getCards());
        System.out.println("deck magically defined with the 4 best cards.");
    }

    public void showDeck() {
        if (!deck.isValidDeck()) {
            System.out.println("deck is not valid. Please select 4 cards.");
        } else {
            System.out.println(deck.toString()); //deck anzeigen
        }
    }
    public Stack getStack() {
        return stack;
    }

    public Deck getDeck() {
        return deck;
    }

    public String getUsername() {
        return username;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getELO() {
        return ELO;
    }

    public void setELO(int ELO) {
        this.ELO = ELO;
    }
}