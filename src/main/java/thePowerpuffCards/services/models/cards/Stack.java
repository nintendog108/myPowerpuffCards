package thePowerpuffCards.services.models.cards;

public class Stack extends CardPile {
    public Stack() {
        super();
    }

    /**
     * Methode, um alle Karten im Stack anzuzeigen.
     */
    public void showStack() {
        System.out.println("Current Stack:");
        for (Card card : cards) {
            System.out.println(card.getName() + " - Damage: " + card.getDamage() + " - Element: " + card.getElementType());
        }
    }

    /**
     * Methode, um den Stack nach bestimmten Kriterien zu durchsuchen.
     * @param elementType Das Element, nach dem gefiltert werden soll.
     * @return Anzahl der Karten, die das gewünschte Element haben.
     */
    public int countCardsByElement(ElementType elementType) {
        return (int) cards.stream().filter(card -> card.getElementType() == elementType).count();
    }
}
