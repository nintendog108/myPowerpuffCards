package thePowerpuffCards.services.models.cards;

import java.util.ArrayList;
import java.util.List;

public abstract class CardPile {
    protected List<Card> cards;

    public CardPile() {
        this.cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void removeCard(Card card) {
        cards.remove(card);
    }

    public List<Card> getCards() {
        return cards;
    }

    public int size() {
        return cards.size();
    }
}
