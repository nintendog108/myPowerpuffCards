package thePowerpuffCards.services.models.cards.monster;
import thePowerpuffCards.services.models.cards.Card;
import thePowerpuffCards.services.models.cards.ElementType;

public class MonsterCard extends Card {
    private final MonsterType monsterType;
    public MonsterCard(String name, int damage, ElementType type, String monsterType) {
        super(name, damage, type);
        this.monsterType=MonsterType.valueOf(monsterType);
    }

    public String getMonsterType() {
        return monsterType.toString();
    }
}