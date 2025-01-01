package thePowerpuffCards.core.models.cards;

public abstract class Card {
    private String Id;
    private final ElementType type;
    private final String name;
    private double damage;

    public String getName() {

        return name;
    }
    public void setDamage(double damage) {
        this.damage = damage;
    }

    public double getDamage() {

        return damage;
    }

    public ElementType getElementType() {

        return type;
    }

    public Card(String Id, String name, Double damage, ElementType type) {
        this.Id = Id;
        this.name = name;
        this.damage = damage;
        this.type = type;
    }

    public void setId(String id) {
        this.Id = id;
    }

    public String getId() {
        return Id;
    }
}
