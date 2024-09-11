package thePowerpuffCards.services.models;

import thePowerpuffCards.services.models.cards.Card;
import thePowerpuffCards.services.models.cards.Deck;
import thePowerpuffCards.services.models.cards.Stack;

public class User {
    private final Stack cardCollection;
    private final Deck deck;
    private String username;
    private String password;
    private int coins;
    // trading

    public User(String username, String password, int coins, Stack cardCollection, Deck deck) {
        this.username = username;
        this.password = password;
        this.deck = deck;
        this.coins = 10;
        this.cardCollection = new Stack();
    }

    public void register(Card card) {
// TODO: register
    }

    public void login(String username, String password) {
// TODO: login
    }


}
