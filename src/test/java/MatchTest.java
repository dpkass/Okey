import Output.FakeOutput;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName ("This test tests, if ")
public class MatchTest {
    private static final Token HEAVY = new Token(Token.HEAVY, -1);
    FakeOutput out = new FakeOutput();

    @Test
    @DisplayName ("two players and then a match can be instantiated.")
    void test_2() {
        Player p = new Player("Hakan");
        Player p2 = new Player("Okan");
        Match m = new Match(new Player[] { p, p2 }, null);

        assertThat(p).isInstanceOf(Player.class);
        assertThat(p2).isInstanceOf(Player.class);
        assertThat(m).isInstanceOf(Match.class);
    }

    @Test
    @DisplayName ("when we have an instance of a match the the players have different hands of tokens.")
    void test_6() {
        Player p = new Player("Hakan");
        Player p2 = new Player("Okan");
        new Match(new Player[] { p, p2 }, null);

        assertThat(p.hand).isNotEqualTo(p2.hand);
    }

    @Test
    @DisplayName ("when we have an instance of a match the first player has a hand of tokens, with 15 Tokens (no null).")
    void test_4() {
        Player p = new Player("Hakan");
        new Match(new Player[] { p, p }, null);
        assertThat(p.hand).isNotNull();
        assertThat(p.hand).isNotEmpty();
        assertThat(p.hand).doesNotContain((Token) null);
    }

    @Test
    @DisplayName ("when we have an instance of a match the all other players, but the first, have a hand of tokens, with 14 Tokens and one heavy.")
    void test_5() {
        Player p = new Player("Hakan");
        Player p2 = new Player("Okan");
        Player p3 = new Player("Tarkan");

        new Match(new Player[] { p, p2, p3 }, null);
        assertThat(p2.hand).isNotNull();
        assertThat(p2.hand).isNotEmpty();
        assertThat(p2.hand[14]).usingComparator(Token::compareToStatic).isEqualTo(HEAVY);

        assertThat(p3.hand).isNotNull();
        assertThat(p3.hand).isNotEmpty();
        assertThat(p3.hand[14]).usingComparator(Token::compareToStatic).isEqualTo(HEAVY);
    }

    @Test
    @DisplayName ("the match starts, greets and asks the player to throw a token.")
    void test_7() throws IOException, InterruptedException {
        Player p = new Player("Hakan");
        Player p2 = new Player("Okan");
        Game m = new Game(new Player[] { p, p2 }, new StringReader("exit"), out);

        m.start();

        assertThat(out.output).contains("Match starts!!");
        assertThat(out.output).contains("Please throw the first Token, " + p + ".");
    }

    @Test
    @DisplayName ("the match answers to a thrown token, which was in ones hand, correctly (first  player).")
    void test_8() throws IOException, InterruptedException {
        Player p = new Player("Hakan");
        Player p2 = new Player("Okan");
        Game m = new Game(new Player[] { p, p2 }, new FileReader("TestInputs/test_8"), out);

        Token t;

        PrintWriter pw = new PrintWriter("TestInputs/test_8");
        pw.println(String.format("%s\nexit", t = p.hand[0]));
        pw.close();

        m.start();

        assertThat(out.output).contains("The thrown Token is {" + t + "}.");
    }

    @Test
    @DisplayName ("the match asks which Token the player wants and answers to a thrown token, which was in ones hand, correctly (second player).")
    void test_9() throws IOException, InterruptedException {
        Player p = new Player("Hakan");
        Player p2 = new Player("Okan");
        Game m = new Game(new Player[] { p, p2 }, new FileReader("TestInputs/test_9"), out);

        Token t, t2;

        PrintWriter pw = new PrintWriter("TestInputs/test_9");
        pw.println(String.format("%s\nnew\n%s\nexit", t = p.hand[0], t2 = p2.hand[0]));
        pw.close();

        m.start();

        assertThat(out.output).contains("Do you want to take the thrown Token {" + t + "} or get a new one?");
        assertThat(out.output).contains("The thrown Token is {" + t2 + "}.");
    }

    @Test
    @DisplayName ("the match announces the right players name.")
    void test_10() throws IOException, InterruptedException {
        Player p = new Player("Hakan");
        Player p2 = new Player("Okan");
        Game m = new Game(new Player[] { p, p2 }, new FileReader("TestInputs/test_10"), out);

        PrintWriter pw = new PrintWriter("TestInputs/test_10");
        pw.println(String.format("%s\nnew\n%s\nexit", p.hand[0], p2.hand[0]));
        pw.close();

        m.start();

        assertThat(out.output.get(3)).isEqualTo("It's Okans turn.");
        assertThat(out.output.get(6)).isEqualTo("It's Hakans turn.");
    }

    /**
     * Here I have to multithread, in order to be able to write something while Match is running.
     * <p>
     * Disabled because Multithreading didn't quite work.
     */
    @Disabled
    @Test
    @DisplayName ("the match waits for input.")
    void test_11() throws IOException, InterruptedException {
        Player p = new Player("Hakan");
        Player p2 = new Player("Okan");
        Game m = new Game(new Player[] { p, p2 }, new FileReader("TestInputs/test_11"), out);

        WaitingTester wt = new WaitingTester("Test 11", m);
        wt.start();

        wait(3);

        PrintWriter pw = new PrintWriter("TestInputs/test_11");
        Token t = p.hand[0];
        pw.println(t);

        wait(3);

        pw.println("new");
        pw.println("exit");
        pw.close();

        wait(3);

        assertThat(out.output).hasSize(6);
    }

    @Test
    @DisplayName ("a player with a winning hand is declared the winner (Only Straight). 1 Straight of 8, 2 of 3.")
    void test_13() {
        Player p = new Player("Hakan");
        Player p2 = new Player("Okan");
        Match m = new Match(new Player[] { p, p2 }, null);

        Token[] tokens = new Token[15];
        Token t = new Token(1, 1);
        tokens[0] = t;
        tokens[1] = new Token(1, 2);
        tokens[2] = new Token(1, 3);
        tokens[3] = new Token(1, 4);
        tokens[4] = new Token(1, 5);
        tokens[5] = new Token(1, 6);
        tokens[6] = new Token(1, 7);
        tokens[7] = new Token(1, 8);
        tokens[8] = new Token(1, 9);
        tokens[9] = new Token(2, 1);
        tokens[10] = new Token(2, 2);
        tokens[11] = new Token(2, 3);
        tokens[12] = new Token(3, 4);
        tokens[13] = new Token(3, 5);
        tokens[14] = new Token(3, 6);

        p.hand = tokens;
        m.playerHandMap.put(p, tokens);

        assertThat(m.currPlayerWon(t)).isTrue();

        System.out.println(out.output);
    }

    @Test
    @DisplayName ("a player with a winning hand is declared the winner (Only Flush). 2 flushes of 4, 2 of 3.")
    void test_14() {
        Player p = new Player("Hakan");
        Player p2 = new Player("Okan");
        Match m = new Match(new Player[] { p, p2 }, null);

        Token[] tokens = new Token[15];
        Token t = new Token(1, 1);
        tokens[0] = t;
        tokens[1] = new Token(1, 2);
        tokens[2] = new Token(2, 2);
        tokens[3] = new Token(3, 2);
        tokens[4] = new Token(0, 2);
        tokens[5] = new Token(1, 6);
        tokens[6] = new Token(2, 6);
        tokens[7] = new Token(3, 6);
        tokens[8] = new Token(0, 6);
        tokens[9] = new Token(1, 1);
        tokens[10] = new Token(2, 1);
        tokens[11] = new Token(3, 1);
        tokens[12] = new Token(1, 4);
        tokens[13] = new Token(2, 4);
        tokens[14] = new Token(3, 4);

        p.hand = tokens;
        m.playerHandMap.put(p, tokens);

        assertThat(m.currPlayerWon(t)).isTrue();
    }


    @Test
    @DisplayName ("a player with a winning hand is declared the winner (No Joker). 1 straight of 6. 2 flushes of 4")
    void test_15() {
        Player p = new Player("Hakan");
        Player p2 = new Player("Okan");
        Match m = new Match(new Player[] { p, p2 }, null);

        Token[] tokens = new Token[15];
        Token t = new Token(1, 1);
        tokens[0] = t;
        tokens[1] = new Token(1, 2);
        tokens[2] = new Token(1, 3);
        tokens[3] = new Token(1, 4);
        tokens[4] = new Token(1, 5);
        tokens[5] = new Token(1, 6);
        tokens[6] = new Token(1, 7);
        tokens[7] = new Token(0, 9);
        tokens[8] = new Token(1, 9);
        tokens[9] = new Token(2, 9);
        tokens[10] = new Token(3, 9);
        tokens[11] = new Token(0, 10);
        tokens[12] = new Token(1, 10);
        tokens[13] = new Token(2, 10);
        tokens[14] = new Token(3, 10);

        p.hand = tokens;
        m.playerHandMap.put(p, tokens);

        assertThat(m.currPlayerWon(t)).isTrue();
    }

    @Test
    @DisplayName ("a player with a winning hand is declared the winner (one Joker). 1 straight of 5. 2 flushes of 4")
    void test_16() {
        Player p = new Player("Hakan");
        Player p2 = new Player("Okan");
        Match m = new Match(new Player[] { p, p2 }, null);

        Token[] tokens = new Token[15];
        Token t = new Token(1, 1);
        tokens[0] = t;
        tokens[1] = new Token(1, 2);
        tokens[2] = new Token(1, 3);
        tokens[3] = new Token(1, 4);
        tokens[4] = new Token(1, 5);
        tokens[5] = new Token(1, 6);
        tokens[6] = new Token(-1, -1);
        tokens[7] = new Token(0, 9);
        tokens[8] = new Token(1, 9);
        tokens[9] = new Token(2, 9);
        tokens[10] = new Token(3, 9);
        tokens[11] = new Token(0, 10);
        tokens[12] = new Token(1, 10);
        tokens[13] = new Token(2, 10);
        tokens[14] = new Token(3, 10);

        p.hand = tokens;
        m.playerHandMap.put(p, tokens);

        assertThat(m.currPlayerWon(t)).isTrue();
    }

    @Test
    @DisplayName ("a player with a winning hand is declared the winner (two Joker). 1 straight of 8. 2 flushes of 2")
    void test_17() {
        Player p = new Player("Hakan");
        Player p2 = new Player("Okan");
        Match m = new Match(new Player[] { p, p2 }, null);

        Token[] tokens = new Token[15];
        Token t = new Token(1, 1);
        tokens[0] = t;
        tokens[1] = new Token(1, 2);
        tokens[2] = new Token(1, 3);
        tokens[3] = new Token(1, 4);
        tokens[4] = new Token(1, 5);
        tokens[5] = new Token(1, 6);
        tokens[6] = new Token(-1, -1);
        tokens[7] = new Token(0, 9);
        tokens[8] = new Token(1, 9);
        tokens[9] = new Token(0, 10);
        tokens[10] = new Token(1, 10);
        tokens[11] = new Token(-1, -1);
        tokens[12] = new Token(1, 7);
        tokens[13] = new Token(1, 8);
        tokens[14] = new Token(1, 9);

        p.hand = tokens;
        m.playerHandMap.put(p, tokens);

        assertThat(m.currPlayerWon(t)).isTrue();
    }

    @Test
    @DisplayName ("a player without winning hand is told not to have won.")
    void test_18() {
        Player p = new Player("Hakan");
        Player p2 = new Player("Okan");
        Match m = new Match(new Player[] { p, p2 }, out, null);

        Token[] tokens = new Token[15];
        Token t = new Token(1, 1);
        tokens[0] = t;
        tokens[1] = new Token(1, 2);
        tokens[2] = new Token(1, 3);
        tokens[3] = new Token(0, 4);
        tokens[4] = new Token(1, 5);
        tokens[5] = new Token(1, 6);
        tokens[6] = new Token(1, 7);
        tokens[7] = new Token(0, 9);
        tokens[8] = new Token(1, 9);
        tokens[9] = new Token(2, 9);
        tokens[10] = new Token(3, 9);
        tokens[11] = new Token(0, 10);
        tokens[12] = new Token(1, 10);
        tokens[13] = new Token(2, 10);
        tokens[14] = new Token(3, 10);

        p.hand = tokens;
        m.playerHandMap.put(p, tokens);

        assertThat(m.currPlayerWon(t)).isFalse();
    }

    @Test
    @DisplayName ("a player with a winning hand is declared the winner. Flush warp around (12->13->1).")
    void test_19() {
        Player p = new Player("Hakan");
        Player p2 = new Player("Okan");
        Match m = new Match(new Player[] { p, p2 }, null);

        Token[] tokens = new Token[15];
        Token t = new Token(1, 1);
        tokens[0] = t;
        tokens[1] = new Token(1, 2);
        tokens[2] = new Token(1, 3);
        tokens[3] = new Token(1, 4);
        tokens[4] = new Token(1, 12);
        tokens[5] = new Token(1, 13);
        tokens[6] = new Token(1, 1);
        tokens[7] = new Token(0, 9);
        tokens[8] = new Token(1, 9);
        tokens[9] = new Token(2, 9);
        tokens[10] = new Token(3, 9);
        tokens[11] = new Token(0, 10);
        tokens[12] = new Token(1, 10);
        tokens[13] = new Token(2, 10);
        tokens[14] = new Token(3, 10);

        p.hand = tokens;
        m.playerHandMap.put(p, tokens);

        assertThat(m.currPlayerWon(t)).isTrue();
    }

    @Test
    @DisplayName ("the match will terminate after someone won the game.")
    void test_20() throws FileNotFoundException {
        Player p = new Player("Hakan");
        Player p2 = new Player("Okan");

        Token[] tokens = new Token[15];
        Token t = new Token(1, 1);
        tokens[0] = t;
        tokens[1] = new Token(1, 2);
        tokens[2] = new Token(1, 3);
        tokens[3] = new Token(1, 4);
        tokens[4] = new Token(1, 12);
        tokens[5] = new Token(1, 13);
        tokens[6] = new Token(1, 1);
        tokens[7] = new Token(0, 9);
        tokens[8] = new Token(1, 9);
        tokens[9] = new Token(2, 9);
        tokens[10] = new Token(3, 9);
        tokens[11] = new Token(0, 10);
        tokens[12] = new Token(1, 10);
        tokens[13] = new Token(2, 10);
        tokens[14] = new Token(3, 10);

        Game g = new Game(new Player[] { p, p2 }, new FileReader("TestInputs/test_20"), out);

        Token p1Throw = p.hand[0];

        PrintWriter pw = new PrintWriter("TestInputs/test_20");
        pw.println(String.format("%s\nnew\nwin %s\nexit", p1Throw, p2.hand[0]));
        pw.close();

        p2.hand = tokens;
        g.getCurrentMatch().playerHandMap.put(p2, tokens);

        g.start();

        System.out.println(out.output);

        String s1 = "Match starts!!";
        String s2 = "Please throw the first Token, Hakan.";
        String s3 = "The thrown Token is {" + p1Throw + "}.";
        String s4 = "It's Okans turn.";
        String s5 = "Do you want to take the thrown Token { + p1Throw + } or get a new one?";
        String s6 = "There is 1 winning combination.";
        String s7 = "This combination is [[Gelb 9, Gelb 10, Rot 1, Rot 1, Rot 2, Rot 3, Rot 4, Rot 9, Rot 10, Rot " +
                "12, Rot " +
                "13, Blau 9, Blau 10, Schwarz 9]]";
        String s8 = "Do you want to take the thrown Token { + p1Throw + } or get a new one?";

        assertThat(out.output).contains("");
    }

    void wait(int i) throws InterruptedException {
        synchronized (TimeUnit.MILLISECONDS) {
            TimeUnit.MILLISECONDS.wait(i * 1000L);
        }
    }
}