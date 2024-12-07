package thePowerpuffCards.core.models.cards.monster;
import thePowerpuffCards.core.models.cards.Card;
import thePowerpuffCards.core.models.cards.ElementType;

public class MonsterCard extends Card {
    private final MonsterType monsterType;
    public MonsterCard(String Id, String name, double damage, ElementType type, MonsterType monsterType) {
        super(Id, name, damage, type);
        this.monsterType = monsterType;
    }


}