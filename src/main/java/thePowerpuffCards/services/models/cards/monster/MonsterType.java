package thePowerpuffCards.services.models.cards.monster;

public enum MonsterType {
    GOBLIN(10),
    DRAGON(50),
    WIZARD(30),
    ORK(25),
    KNIGHT(40),
    KRAKEN(60),
    BUBBLES(35);


    private final int damage;

    MonsterType(int damage) {

        this.damage = damage;
    }

    public int getDamage() {

        return damage;
    }

}
