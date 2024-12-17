package thePowerpuffCards.core.models.cards.spell;

import thePowerpuffCards.core.models.cards.Card;
import thePowerpuffCards.core.models.cards.ElementType;

public class SpellCard extends Card {
    public SpellCard(String Id, String name, Double damage, ElementType elementType) {
        super(Id, name, damage, elementType);
    }
}
