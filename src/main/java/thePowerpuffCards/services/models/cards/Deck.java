package thePowerpuffCards.services.models.cards;
import thePowerpuffCards.services.models.cards.Card;

import java.util.List;


public class Deck {
    private List<Card> deckCards;

    public Deck(List<Card> deckCards) {
        this.deckCards = deckCards;
    }

    public void addCard(Card card){
        deckCards.add(card);
    }
    public void RemoveCard(){
        deckCards.removeLast();
    }
    public Card getCards(){

        return null;
    }
}
