package thePowerpuffCards.core.models.cards;

import java.util.ArrayList;
import java.util.List;

public abstract class CardPile {
    protected List<Card> cards;

    public CardPile() {
        this.cards = new ArrayList<>();
    }

    public List<Card> getCards() {
        return cards;
    }

    public int size() {
        return cards.size();
    }
}
