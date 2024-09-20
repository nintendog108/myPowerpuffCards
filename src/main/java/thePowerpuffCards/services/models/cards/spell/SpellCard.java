package thePowerpuffCards.services.models.cards.spell;

import thePowerpuffCards.services.models.cards.Card;
import thePowerpuffCards.services.models.cards.ElementType;

import java.util.ArrayList;
import java.util.List;
public class SpellCard extends Card {
    public SpellCard(String name, int damage, ElementType type) {
        super(name, damage, type);
    }
}
