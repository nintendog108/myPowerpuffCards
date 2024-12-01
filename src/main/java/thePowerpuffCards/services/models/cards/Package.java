package thePowerpuffCards.services.models.cards;

import thePowerpuffCards.services.models.User;

import java.util.ArrayList;
import java.util.List;

public class Package {
    private long id; // Database-assigned ID
    private List<Card> cards;
    private final int pckgCost = 5;
    private final int cardsinPckg = 5;

    public Package(List<Card> availableCards) {
        if (availableCards.size() >= cardsinPckg) {
            this.cards = new ArrayList<>(availableCards.subList(0, cardsinPckg));
        } else {
            throw new IllegalArgumentException("Nicht genügend Karten verfügbar, um ein Paket zu erstellen.");
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    // Diese Methode wird benötigt
    public List<Card> getCards() {
        return cards;
    }

    public boolean purchasePackage(User user) {
        if (user.getCoins() >= pckgCost) {
            user.setCoins(user.getCoins() - pckgCost);
            user.getStack().addCards(cards);
            System.out.println("Paket erfolgreich gekauft. " + pckgCost + " Münzen abgezogen.");
            return true;
        } else {
            System.out.println("Nicht genügend Münzen, um das Paket zu kaufen.");
            return false;
        }
    }

    public int getPackageCost() {
        return pckgCost;
    }

    public int getCardsInPackage() {
        return cardsinPckg;
    }
}
