package thePowerpuffCards.services.models.cards;

import java.util.List;

public class Deck extends CardPile {

    public Deck() {
        super();
    }

    public void defineDeck(List<Card> selectedCards) {
        if (selectedCards.size() != 4) {
            throw new IllegalArgumentException("a deck must consist of exactly 4 cards.");
        }
        this.cards = selectedCards;
    }


    public boolean isValidDeck() {
        return this.cards.size() == 4;
    }

    public void selectBestCardsFromCollection(List<Card> collection) {
        if (collection.size() < 4) {
            throw new IllegalArgumentException("not enough cards in the collction to build a deck.");
        }

        // höchsten schaden zuerst
        collection.sort((card1, card2) -> Integer.compare(card2.getDamage(), card1.getDamage()));
        //the best four
        this.cards = collection.subList(0, 4);
    }

    @Override
    public String toString() {
        StringBuilder deckInfo = new StringBuilder("Deck: \n");
        for (Card card : cards) {
            deckInfo.append(card.getName())
                    .append(" & Damage: ")
                    .append(card.getDamage())
                    .append(" & Element: ")
                    .append(card.getElementType())
                    .append("\n");
        }
        return deckInfo.toString();
    }
}
