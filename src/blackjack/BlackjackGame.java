package blackjack;

import java.util.Scanner;


public class BlackjackGame {

    /** 庄家停牌线：达到 17 点即停。 */
    private static final int DEALER_STAND_SCORE = 17;
    private static final int BLACKJACK_SCORE = 21;
    private static final int INITIAL_CARDS = 2;

    private final Scanner scanner = new Scanner(System.in);
    private final Deck deck = new Deck();
    private final Player player = new Player("玩家");
    private final Player dealer = new Player("庄家");

    private int wins;
    private int losses;
    private int pushes;

    /** 玩家回合的三种结果。 */
    private enum Action {
        STAND,
        BUST,
        QUIT
    }

    public static void main(String[] args) {
        new BlackjackGame().run();
    }

    public void run() {
        printWelcome();
        while (true) {
            if (!playRound()) {
                break;
            }
            printScoreboard();
            if (!askPlayAgain()) {
                break;
            }
        }
        printSummary();
        scanner.close();
    }

    // ---------------------------------------------------------------- 一局流程

    /**
     * 打一局。
     *
     * @return false 表示玩家中途退出，不再继续
     */
    private boolean playRound() {
        player.clearHand();
        dealer.clearHand();
        deck.shuffle();

        System.out.println();
        System.out.println("── 新的一局 ──");
        dealInitialCards();

        // 起手 Blackjack：直接摊牌结算，玩家不能再要牌
        if (player.isBlackjack() || dealer.isBlackjack()) {
            System.out.println(player.describeHand(false));
            System.out.println(dealer.describeHand(false));
            settleBlackjack();
            return true;
        }

        System.out.println(player.describeHand(false) + "（当前 " + player.score() + " 点）");
        System.out.println(dealer.describeHand(true) + "（暗牌未翻开）");

        Action action = playerTurn();
        if (action == Action.QUIT) {
            System.out.println("你选择了退出。");
            return false;
        }
        if (action == Action.BUST) {
            System.out.println("你爆牌了，" + player.score() + " 点，本局判负。");
            losses++;
            return true;
        }

        dealerTurn();
        settleNormal();
        return true;
    }

    private void dealInitialCards() {
        System.out.println("发牌中……");
        for (int i = 0; i < INITIAL_CARDS; i++) {
            player.receive(deck.deal());
            dealer.receive(deck.deal());
        }
    }

    private Action playerTurn() {
        while (true) {
            System.out.println();
            String line = readLine("要牌(H) / 停牌(S) / 退出(Q)：");
            if (line == null) {
                return Action.QUIT;
            }
            if (line.isEmpty()) {
                System.out.println("输入不能为空，请输入 H、S 或 Q。");
                continue;
            }

            String choice = line.toUpperCase();
            if (choice.equals("Q")) {
                return Action.QUIT;
            }
            if (choice.equals("S")) {
                System.out.println("你停牌，当前 " + player.score() + " 点。");
                return Action.STAND;
            }
            if (choice.equals("H")) {
                Card card = deck.deal();
                player.receive(card);
                System.out.println("你抽到 " + card + "。");
                System.out.println(player.describeHand(false) + "（当前 " + player.score() + " 点）");

                if (player.isBusted()) {
                    return Action.BUST;
                }
                if (player.score() == BLACKJACK_SCORE) {
                    System.out.println("你已经 21 点，自动停牌。");
                    return Action.STAND;
                }
                continue;
            }
            System.out.println("无效输入，请输入 H、S 或 Q。");
        }
    }

    private void dealerTurn() {
        System.out.println();
        System.out.println("庄家翻开暗牌：" + dealer.describeHand(false) + "（" + dealer.score() + " 点）");

        while (dealer.score() < DEALER_STAND_SCORE) {
            Card card = deck.deal();
            dealer.receive(card);
            System.out.println("庄家要牌：" + card + "，当前 " + dealer.score() + " 点。");
        }

        if (dealer.isBusted()) {
            System.out.println("庄家爆牌，" + dealer.score() + " 点。");
        } else {
            System.out.println("庄家停牌，" + dealer.score() + " 点。");
        }
    }

    // ---------------------------------------------------------------- 结算

    private void settleBlackjack() {
        boolean playerBlackjack = player.isBlackjack();
        boolean dealerBlackjack = dealer.isBlackjack();

        if (playerBlackjack && dealerBlackjack) {
            pushes++;
            System.out.println("双方都是 Blackjack，平局。");
            return;
        }
        if (playerBlackjack) {
            wins++;
            System.out.println("玩家 Blackjack，获胜！");
            return;
        }
        losses++;
        System.out.println("庄家 Blackjack，玩家落败。");
    }

    private void settleNormal() {
        int playerScore = player.score();
        int dealerScore = dealer.score();

        System.out.println("你的点数 " + playerScore + "，庄家点数 " + dealerScore + "。");

        if (dealer.isBusted()) {
            wins++;
            System.out.println("结果：玩家获胜！");
        } else if (playerScore > dealerScore) {
            wins++;
            System.out.println("结果：玩家获胜！");
        } else if (playerScore < dealerScore) {
            losses++;
            System.out.println("结果：庄家获胜。");
        } else {
            pushes++;
            System.out.println("结果：平局。");
        }
    }

    // ---------------------------------------------------------------- 输入与输出

    /**
     * 读一行输入。输入流结束时返回 null，用于优雅退出而不是抛异常。
     */
    private String readLine(String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            System.out.println();
            return null;
        }
        return scanner.nextLine().trim();
    }

    private boolean askPlayAgain() {
        while (true) {
            String line = readLine("再来一局？(Y/N)：");
            if (line == null) {
                return false;
            }
            if (line.equalsIgnoreCase("Y")) {
                return true;
            }
            if (line.equalsIgnoreCase("N")) {
                return false;
            }
            System.out.println("请输入 Y 或 N。");
        }
    }

    private void printWelcome() {
        System.out.println("========================================");
        System.out.println("              21 点 · 控制台版");
        System.out.println("========================================");
        System.out.println("规则：A 记 1 或 11，J/Q/K 记 10 点；");
        System.out.println("      比庄家更接近 21 点即获胜，超过 21 点爆牌判负；");
        System.out.println("      庄家不足 17 点必须继续要牌。");
        System.out.println("操作：H 要牌，S 停牌，Q 退出。");
        System.out.println("========================================");
    }

    private void printScoreboard() {
        System.out.println("战绩：胜 " + wins + " 局，负 " + losses + " 局，平 " + pushes + " 局。");
    }

    private void printSummary() {
        int total = wins + losses + pushes;
        System.out.println();
        System.out.println("================ 最终战绩 ================");
        System.out.println("总局数：" + total);
        System.out.println("胜：" + wins + "　负：" + losses + "　平：" + pushes);
        if (total > 0) {
            double rate = wins * 100.0 / total;
            System.out.printf("胜率：%.1f%%%n", rate);
        }
        System.out.println("游戏结束，感谢游玩！");
    }
}
