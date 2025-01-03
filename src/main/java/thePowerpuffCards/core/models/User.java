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
    private int coins = 20;
    private Stack stack;
    private Deck deck;
    private String token;

    // Konstruktoren
    public User(String username, String password) {
        this.setUsername(username);
        this.password = password;
        this.coins = 20;
        this.token = "";
        this.stack = new Stack();
        this.deck = new Deck();
    }

    public User() { // für Jackson
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        this.coins = coins;
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
