package thePowerpuffCards.services.models;

import thePowerpuffCards.services.models.cards.Card;
import thePowerpuffCards.services.models.cards.Deck;
import thePowerpuffCards.services.models.cards.Stack;

import java.util.List;

public class User {
    private final Stack cardCollection;
    private final List<Deck> deck;
    private String username;
    private String password;
    private int coins;
    // trading

    public User(String username, String password, int coins, Stack cardCollection, Deck deck, List<Deck> deck1) {
        this.username = username;
        this.password = password;
        this.deck = deck1;
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
