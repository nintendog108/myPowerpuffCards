package thePowerpuffCards.core.models.cards;

public enum ElementType {
    FIRE,
    WATER,
    NORMAL;
    public static ElementType getType(String name) {
        name = name.toLowerCase();
        if (name.contains("fire")) {
            return FIRE;
        } else if (name.contains("water")) {
            return WATER;
        } else {
            return NORMAL;
        }
    }
}
