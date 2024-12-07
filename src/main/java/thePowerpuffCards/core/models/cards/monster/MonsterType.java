package thePowerpuffCards.core.models.cards.monster;

public enum MonsterType {
    GOBLIN(10),
    DRAGON(50),
    WIZARD(30),
    ORK(25),
    KNIGHT(40),
    KRAKEN(60);
//wie beim spell


    private final double damage;

    MonsterType(double damage) {

        this.damage = damage;
    }

    public double getDamage() {

        return damage;
    }
/*
    public static MonsterType getMonsterType(String Name) {
        if (Name.contains("GOBLIN")) {
            return GOBLIN;
        } else if (Name.contains("DRAGON")) {
            return DRAGON;
        } else if (Name.contains("WIZARD")) {
            return WIZARD;
        } else if (Name.contains("ORK")) {
            return ORK;
        } else if (Name.contains("KNIGHT")) {
            return KNIGHT;
        } else if (Name.contains("KRAKEN")) {
            return KRAKEN;
        }
        return null;
    }
*/
    public static MonsterType getMonsterType(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        if (name.toLowerCase().contains("dragon")) {
            return DRAGON;
        } else if (name.toLowerCase().contains("goblin")) {
            return GOBLIN;
        } else if (name.toLowerCase().contains("orc")) {
            return ORK;
        } else if (name.toLowerCase().contains("knight")) {
            return KNIGHT;
        } else if (name.toLowerCase().contains("kraken")) {
            return KRAKEN;
        } else if (name.toLowerCase().contains("wizard")) {
            return WIZARD;
        }
        return null;
    }
}

