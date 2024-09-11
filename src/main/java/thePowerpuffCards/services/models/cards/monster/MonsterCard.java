package thePowerpuffCards.services.models.cards.monster;
import thePowerpuffCards.services.models.cards.ElementType;

public class MonsterCard {
    private final String monsterType;
    public MonsterCard(String name, int damage, ElementType type, String monsterType) {
        super(); // noch nicht fertig TODO
        this.monsterType = monsterType;
    }

    public String getMonsterType() {
        return monsterType;
    }

}
