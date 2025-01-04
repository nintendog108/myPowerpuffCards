package thePowerpuffCards.core.models.cards;

import thePowerpuffCards.core.models.User;

import java.util.ArrayList;
import java.util.List;

public class Package {
    private int id;
    private List<Card> cards;
    private final int cardsinPckg = 5;

    public Package(List<Card> availableCards) {
        if (availableCards.size() >= cardsinPckg) {
            this.cards = new ArrayList<>(availableCards.subList(0, cardsinPckg));
        } else {
            throw new IllegalArgumentException("Nicht genügend Karten verfügbar, um ein Paket zu erstellen.");
        }
    }
    public Package() {
        this.cards = new ArrayList<>();
    }

    public Package(int id, List<Card> cards) {
        this.id = id;
        this.cards = cards;
    }

    public Package(int packageId) {
        this.id = packageId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Card> getCards() {
        return cards;
    }


}
