package thePowerpuffCards.core.models.cards.monster;

public enum MonsterType {
    GOBLIN(10),
    DRAGON(50),
    WIZARD(30),
    ORK(25),
    KNIGHT(40),
    KRAKEN(60);

    private final int baseDamage;

    MonsterType(int baseDamage) {
        this.baseDamage = baseDamage;
    }

    public int getBaseDamage() {
        return baseDamage;
    }

    public static MonsterType getMonsterType(String name) {
        name = name.toLowerCase();
        for (MonsterType type : values()) {
            if (name.contains(type.name().toLowerCase())) {
                return type;
            }
        }
        return null;
    }
}


