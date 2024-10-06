package thePowerpuffCards.services;

import thePowerpuffCards.services.models.cards.Card;
import thePowerpuffCards.services.models.cards.Stack;
import thePowerpuffCards.services.models.cards.monster.MonsterCard;
import thePowerpuffCards.services.models.cards.spell.SpellCard;

public class TradeService {
    private String tradeId;
    private Card offeredCard;
    private String requiredCardType;
    private int minDamage;
    private String offeredByUser;


    public TradeService(String tradeId, Card offeredCard, String requiredCardType, int minDamage, String offeredByUser) {
        this.tradeId = tradeId;
        this.offeredCard = offeredCard;
        this.requiredCardType = requiredCardType;
        this.minDamage = minDamage;
        this.offeredByUser = offeredByUser;
    }


    public boolean validateCardForTrade(Card card) {
        if (card.getDamage() < minDamage) {
            return false; // karte hat zu wenig schaden
        }

        if (requiredCardType.equalsIgnoreCase("Monster") && !(card instanceof MonsterCard)) {
            return false;
        }

        if (requiredCardType.equalsIgnoreCase("Spell") && !(card instanceof SpellCard)) {
            return false;
        }

        return true;
    }

    public boolean executeTrade(Card buyerCard, Stack buyerStack, Stack sellerStack) {
        if (!validateCardForTrade(buyerCard)) {
            System.out.println("trade failed, you have shitty cards");
            return false;
        }

        //weg vom Käuferstack und add to buyer  TODO: logic error here? hmmm
        buyerStack.removeCard(buyerCard);
        sellerStack.addCard(buyerCard);

        sellerStack.removeCard(offeredCard);
        buyerStack.addCard(offeredCard);

        System.out.println("trade successful!   " + offeredByUser + " traded " + offeredCard.getName() + " for " + buyerCard.getName());
        return true;
    }





    // easy getters
    public String getTradeId() {
        return tradeId;
    }

    public Card getOfferedCard() {
        return offeredCard;
    }

    public String getRequiredCardType() {
        return requiredCardType;
    }

    public int getMinDamage() {
        return minDamage;
    }

    public String getOfferedByUser() {
        return offeredByUser;
    }
}
