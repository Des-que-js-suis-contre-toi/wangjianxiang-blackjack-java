package blackjack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public final class Deck {

    private final List<Card> remaining = new ArrayList<>(52);
    private final List<Card> used = new ArrayList<>(52);
    private final Random random;

    public Deck() {
        this(new Random());
    }

    /** 允许传入随机源，便于复现某一局牌序。 */
    public Deck(Random random) {
        this.random = random;
        buildFullDeck();
    }

    private void buildFullDeck() {
        remaining.clear();
        used.clear();
        for (Card.Rank rank : Card.Rank.values()) {
            for (Card.Suit suit : Card.Suit.values()) {
                remaining.add(new Card(rank, suit));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(remaining, random);
    }

    /** 发一张牌；牌堆见底时自动回收已发出的牌并重洗。 */
    public Card deal() {
        if (remaining.isEmpty()) {
            remaining.addAll(used);
            used.clear();
            shuffle();
        }
        Card card = remaining.remove(remaining.size() - 1);
        used.add(card);
        return card;
    }

    public int remainingCount() {
        return remaining.size();
    }
}
