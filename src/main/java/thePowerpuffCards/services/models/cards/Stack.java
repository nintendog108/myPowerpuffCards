package thePowerpuffCards.services.models.cards;

public class Stack extends CardPile {
    public Stack() {
        super();
    }


    public void showStack() {
        System.out.println("Current Stack:");
        for (Card card : cards) {
            System.out.println(card.getName() + " - Damage: " + card.getDamage() + " - Element: " + card.getElementType());
        }
    }

    public int countCardsByElement(ElementType elementType) {
        return (int) cards.stream().filter(card -> card.getElementType() == elementType).count();
    }
}
