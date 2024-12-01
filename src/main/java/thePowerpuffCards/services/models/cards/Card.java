package thePowerpuffCards.services.models.cards;

public abstract class Card {
    private int id;
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

    public void setId(int id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
