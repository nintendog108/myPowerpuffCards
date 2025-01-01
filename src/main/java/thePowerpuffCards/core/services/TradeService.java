package thePowerpuffCards.core.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import thePowerpuffCards.core.models.cards.Card;
import thePowerpuffCards.core.models.cards.monster.MonsterCard;
import thePowerpuffCards.core.models.cards.spell.SpellCard;
import thePowerpuffCards.persistence.dao.CardDaoDb;
import thePowerpuffCards.persistence.dao.TradeDaoDb;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TradeService {
    private String tradeId;
    private Card offeredCard;
    private String requiredCardType;
    private int minDamage;
    private static String offeredByUser;


    public TradeService(String tradeId, Card offeredCard, String requiredCardType, int minDamage, String offeredByUser) {
        this.tradeId = tradeId;
        this.offeredCard = offeredCard;
        this.requiredCardType = requiredCardType;
        this.minDamage = minDamage;
        this.offeredByUser = offeredByUser;
    }

    public static TradeService fromJson(String json, String offeredByUser) throws IOException {
 //       System.out.println("*************   Parsing TradeService from JSON: " + json);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> tradeData = objectMapper.readValue(json, Map.class);

        String tradeId = (String) tradeData.get("Id");
        String cardId = (String) tradeData.get("CardToTrade");
        String type = (String) tradeData.get("Type");
        int minDamage = (int) tradeData.get("MinimumDamage");

     //   System.out.println("*************   Parsed TradeService: tradeId=" + tradeId + ", cardId=" + cardId);

        Card offeredCard = new CardDaoDb().getCardById(cardId);
        if (offeredCard == null) {
            throw new IllegalArgumentException("Card not found: " + cardId);
        }

        return new TradeService(tradeId, offeredCard, type, minDamage, offeredByUser);
    }

    public void setOfferedByUser(String offeredByUser) {
        this.offeredByUser = offeredByUser;
    }

    public static boolean validateCardForTrade(Card card, String requiredType, int minDamage) {
        if (card.getDamage() < minDamage) {
            return false; // Card has insufficient damage
        }

        if ("Monster".equalsIgnoreCase(requiredType) && !(card instanceof MonsterCard)) {
            return false;
        }

        if ("Spell".equalsIgnoreCase(requiredType) && !(card instanceof SpellCard)) {
            return false;
        }

        return true;
    }




    // Extend TradeService

    public static List<TradeService> fetchAllTrades(TradeDaoDb tradeDaoDb) {
        return tradeDaoDb.getAllTrades();
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
