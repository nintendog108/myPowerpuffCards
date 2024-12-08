package thePowerpuffCards.core.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import thePowerpuffCards.core.models.cards.Card;
import thePowerpuffCards.core.models.cards.Deck;
import thePowerpuffCards.core.models.cards.Stack;

import java.util.List;
import java.util.Objects;

public class User {
    @JsonProperty("id")
    private int id;  // Neue Eigenschaft für die Benutzer-ID
    @JsonProperty("Username")
    private String username;
    @JsonProperty("Password")
    private String password;
    private int coins;
    private Stack stack;
    private Deck deck;
    private int ELO;
    private String token;

    // Konstruktoren
    public User(String username, String password) {
        this.setUsername(username);
        this.password = password;
        this.coins = 20;
        this.token = "";
        this.stack = new Stack();
        this.deck = new Deck();
        this.ELO = 100;
    }

    public User() { // für Jackson
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Methoden zum Hinzufügen und Definieren des Decks
    public void addCardsToStack(List<Card> newCards) {
        for (Card card : newCards) {
            stack.addCard(card);
        }
        System.out.println(" Just added " + newCards.size() + " new cards to the stack.");
    }

    public void defineDeck(List<Card> selectedCards) {
        if (selectedCards.size() != 4) {
            throw new IllegalArgumentException("Deck must consist of exactly 4 cards.");
        }

        for (Card card : selectedCards) {
            if (!stack.getCards().contains(card)) {
                throw new IllegalArgumentException("Card " + card.getName() + " is not in the stack.");
            }
        }
        deck.defineDeck(selectedCards);
        System.out.println("Deck defined with 4 cards.");
    }

    // Automatische Deck-Auswahl
    public void autoSelectBestDeck() {
        deck.selectBestCardsFromCollection(stack.getCards());
        System.out.println("Deck magically defined with the 4 best cards.");
    }

    // Getter und Setter für andere Eigenschaften
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
        this.coins = 20;
    }

    public int getELO() {
        return ELO;
    }

    public void setELO(int ELO) {
        this.ELO = ELO;
    }

    @JsonProperty("Password")
    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    @JsonProperty("Username")
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
