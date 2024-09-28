package thePowerpuffCards.services.models.cards;

public abstract class Card {
    private final ElementType type;
    private final String name;
    private final int damage;

    public String getName() {

        return name;
    }

    public int getDamage() {

        return damage;
    }

    public ElementType getElementType() {

        return type;
    }

    public Card(String name, int damage, ElementType type) {
        this.name = name;
        this.damage = damage;
        this.type = type;
    } // ctor
}
