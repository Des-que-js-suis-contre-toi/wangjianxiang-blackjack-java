package blackjack;


public final class Card {

    /** 花色。控制台输出用中文，避免 Windows 控制台对 ♠♥♦♣ 显示乱码。 */
    public enum Suit {
        CLUBS("梅花"),
        DIAMONDS("方块"),
        HEARTS("红桃"),
        SPADES("黑桃");

        private final String label;

        Suit(String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }
    }

    /** 点数。J/Q/K 记 10 点，A 记 11 点（可降为 1）。 */
    public enum Rank {
        ACE("A", 11),
        TWO("2", 2),
        THREE("3", 3),
        FOUR("4", 4),
        FIVE("5", 5),
        SIX("6", 6),
        SEVEN("7", 7),
        EIGHT("8", 8),
        NINE("9", 9),
        TEN("10", 10),
        JACK("J", 10),
        QUEEN("Q", 10),
        KING("K", 10);

        private final String label;
        private final int value;

        Rank(String label, int value) {
            this.label = label;
            this.value = value;
        }

        public String label() {
            return label;
        }

        public int value() {
            return value;
        }
    }

    private final Rank rank;
    private final Suit suit;

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public Rank rank() {
        return rank;
    }

    public Suit suit() {
        return suit;
    }

    /** 点数基准值，A 返回 11。 */
    public int value() {
        return rank.value();
    }

    public boolean isAce() {
        return rank == Rank.ACE;
    }

    @Override
    public String toString() {
        return suit.label() + rank.label();
    }
}
