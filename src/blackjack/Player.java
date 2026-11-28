package blackjack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 一名玩家（玩家自己或庄家）：保管手牌，按 21 点规则计算点数。
 */
public class Player {

    private static final int BLACKJACK_SCORE = 21;
    private static final int ACE_SOFT_DIFFERENCE = 10;

    private final String name;
    private final List<Card> hand = new ArrayList<>();

    public Player(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public void receive(Card card) {
        hand.add(card);
    }

    public void clearHand() {
        hand.clear();
    }

    public int handSize() {
        return hand.size();
    }

    public List<Card> hand() {
        return Collections.unmodifiableList(hand);
    }

    /**
     * 计算点数：A 先按 11 累加，超过 21 点时每张 A 再减 10（即当作 1）。
     */
    public int score() {
        int total = 0;
        int aceCount = 0;
        for (Card card : hand) {
            total += card.value();
            if (card.isAce()) {
                aceCount++;
            }
        }
        while (total > BLACKJACK_SCORE && aceCount > 0) {
            total -= ACE_SOFT_DIFFERENCE;
            aceCount--;
        }
        return total;
    }

    public boolean isBusted() {
        return score() > BLACKJACK_SCORE;
    }

    /** 起手两张即 21 点，才算 Blackjack（补牌补到 21 点不算）。 */
    public boolean isBlackjack() {
        return hand.size() == 2 && score() == BLACKJACK_SCORE;
    }

    /**
     * 手牌文本。
     *
     * @param hideSecondCard true 时第二张显示为 [暗牌]（庄家开局的暗牌）
     */
    public String describeHand(boolean hideSecondCard) {
        StringBuilder sb = new StringBuilder(name).append("的手牌：");
        for (int i = 0; i < hand.size(); i++) {
            if (i > 0) {
                sb.append("，");
            }
            if (i == 1 && hideSecondCard) {
                sb.append("[暗牌]");
            } else {
                sb.append(hand.get(i));
            }
        }
        return sb.toString();
    }
}
