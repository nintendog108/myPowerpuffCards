package thePowerpuffCards.core.models.cards;

import thePowerpuffCards.persistence.dao.UsersDaoDb;

import java.util.List;

public class Deck extends CardPile {

    public Deck() {
        super();
    }

    public void defineDeck(List<Card> selectedCards, String username, UsersDaoDb usersDao) {
        if (selectedCards.size() != 4) {
            throw new IllegalArgumentException("A deck must consist of exactly 4 cards.");
        }

        this.cards = selectedCards;
        usersDao.clearDeck(username);
        usersDao.saveDeck(username, selectedCards);
    }


    public void selectBestCardsFromCollection(List<Card> collection) {
        if (collection.size() < 4) {
            throw new IllegalArgumentException("Not enough cards in the collection to build a deck.");
        }

        // höchsten schaden zuerst
        collection.sort((card1, card2) -> Double.compare(card2.getDamage(), card1.getDamage()));
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
