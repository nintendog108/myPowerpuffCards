package thePowerpuffCards.core.models.cards;

public enum ElementType {
    FIRE,
    WATER,
    NORMAL;
    public static ElementType getType(String Name){
        if(Name.contains("Fire")){
            return FIRE;

        } else if(Name.contains("Water")) {
            return WATER;
        } else {
            return NORMAL;
        }
        // weiter - normal steht regular
    }
}
