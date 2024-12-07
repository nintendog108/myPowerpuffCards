package thePowerpuffCards.core.services;

import thePowerpuffCards.core.models.cards.Card;
import thePowerpuffCards.core.models.cards.ElementType;
import thePowerpuffCards.core.models.cards.monster.MonsterCard;
import thePowerpuffCards.core.models.cards.spell.SpellCard;
import thePowerpuffCards.core.models.cards.monster.MonsterType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class BattleService {
    private List<Card> player1Deck;
    private List<Card> player2Deck;
    private String player1;
    private String player2;
    private StringBuilder battleLog;

    private static final int MAX_ROUNDS = 100;

    public BattleService(String player1, String player2, List<Card> player1Deck, List<Card> player2Deck) {
        this.player1Deck = new ArrayList<>(player1Deck);
        this.player2Deck = new ArrayList<>(player2Deck);
        this.player1 = player1;
        this.player2 = player2;
        this.battleLog = new StringBuilder();
    }

    public String startBattle() {
        int roundCounter = 1;
        Random random = new Random();

        while (roundCounter <= MAX_ROUNDS && !player1Deck.isEmpty() && !player2Deck.isEmpty()) {
            battleLog.append("\nRound: ").append(roundCounter).append(":\n");

            Card player1Card = player1Deck.get(random.nextInt(player1Deck.size()));
            Card player2Card = player2Deck.get(random.nextInt(player2Deck.size()));

            battleLog.append(player1).append(" plays: ").append(player1Card.getName()).append(" (")
                    .append(player1Card.getDamage()).append(" Damage, ").append(player1Card.getElementType()).append(")\n");
            battleLog.append(player2).append(" plays: ").append(player2Card.getName()).append(" (")
                    .append(player2Card.getDamage()).append(" Damage, ").append(player2Card.getElementType()).append(")\n");

            int roundResult = calculateRoundResult(player1Card, player2Card);

            if (roundResult > 0) {
                battleLog.append(player1).append(" won this round!\n");
                player2Deck.remove(player2Card);
                player1Deck.add(player2Card);
            } else if (roundResult < 0) {
                battleLog.append(player2).append(" won this round!\n");
                player1Deck.remove(player1Card);
                player2Deck.add(player1Card);
            } else {
                battleLog.append("It's a draw! No cards were moved.\n");
            }

            roundCounter++;
        }

        determineWinner();
        return battleLog.toString();
    }

    private int calculateRoundResult(Card player1Card, Card player2Card) {
        if (player1Card instanceof MonsterCard && player2Card instanceof MonsterCard) {
            return handleMonsterSpecialties((MonsterCard) player1Card, (MonsterCard) player2Card);
        }

        return calculateDamage(player1Card, player2Card);
    }

    private int handleMonsterSpecialties(MonsterCard card1, MonsterCard card2) {
        if (isGoblinAfraidOfDragon(card1, card2)) return -1;
        if (isGoblinAfraidOfDragon(card2, card1)) return 1;

        if (isWizardControllingOrk(card1, card2)) return 1;
        if (isWizardControllingOrk(card2, card1)) return -1;

        if (isKnightDrowning(card1, card2)) return -1;
        if (isKnightDrowning(card2, card1)) return 1;

        if (isKrakenImmuneToSpell(card1, card2)) return 1;
        if (isKrakenImmuneToSpell(card2, card1)) return -1;

        return Double.compare(card1.getDamage(), card2.getDamage());
    }

    private boolean isGoblinAfraidOfDragon(MonsterCard goblin, MonsterCard dragon) {
        return MonsterType.getMonsterType(goblin.getName()) == MonsterType.GOBLIN &&
                MonsterType.getMonsterType(dragon.getName()) == MonsterType.DRAGON;
    }

    private boolean isWizardControllingOrk(MonsterCard wizard, MonsterCard ork) {
        return MonsterType.getMonsterType(wizard.getName()) == MonsterType.WIZARD &&
                MonsterType.getMonsterType(ork.getName()) == MonsterType.ORK;
    }

    private boolean isKnightDrowning(MonsterCard knight, MonsterCard opponent) {
        return MonsterType.getMonsterType(knight.getName()) == MonsterType.KNIGHT &&
                opponent.getElementType() == ElementType.WATER;
    }

    private boolean isKrakenImmuneToSpell(MonsterCard kraken, Card opponent) {
        return MonsterType.getMonsterType(kraken.getName()) == MonsterType.KRAKEN && opponent instanceof SpellCard;
    }


    private int calculateDamage(Card card1, Card card2) {
        double damageMultiplier = 1.0;

        if (card1 instanceof SpellCard || card2 instanceof SpellCard) {
            if (card1.getElementType() == ElementType.WATER && card2.getElementType() == ElementType.FIRE) {
                damageMultiplier = 2.0;
            } else if (card1.getElementType() == ElementType.FIRE && card2.getElementType() == ElementType.NORMAL) {
                damageMultiplier = 2.0;
            } else if (card1.getElementType() == ElementType.NORMAL && card2.getElementType() == ElementType.WATER) {
                damageMultiplier = 2.0;
            } else if (card1.getElementType() == ElementType.FIRE && card2.getElementType() == ElementType.WATER) {
                damageMultiplier = 0.5;
            } else if (card1.getElementType() == ElementType.WATER && card2.getElementType() == ElementType.NORMAL) {
                damageMultiplier = 0.5;
            } else if (card1.getElementType() == ElementType.NORMAL && card2.getElementType() == ElementType.FIRE) {
                damageMultiplier = 0.5;
            }
        }

        return (int) ((card1.getDamage() * damageMultiplier) - card2.getDamage());
    }

    private void determineWinner() {
        if (player1Deck.isEmpty() && player2Deck.isEmpty()) {
            battleLog.append("It's a draw!\n");
        } else if (player1Deck.isEmpty()) {
            battleLog.append(player2).append(" won the game!\n");
        } else {
            battleLog.append(player1).append(" won the game!\n");
        }
    }
}
