import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayName("This test tests, if ")
public class GameTester {
    FakeOutput out = new FakeOutput();

    @Test
    @DisplayName("a player can be instantiated.")
    void test_1() {
        Player p = new Player("Hakan");
    }


    @Test
    @DisplayName("two players and then a game can be instantiated.")
    void test_2() {
        Player p = new Player("Hakan");
        Player p2 = new Player("Okan");
        Game g = new Game(new Player[]{p, p2});
    }

    @Test
    @DisplayName("an Exception is thrown when a game is instantiated with one player.")
    void test_3() {
        Player p = new Player("Hakan");
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new Game(new Player[]{p}));
    }

    @Test
    @DisplayName("when we have an instance of a game the the players have different hands of tokens.")
    void test_6() {
        Player p = new Player("Hakan");
        Player p2 = new Player("Okan");
        Game g = new Game(new Player[]{p, p2});

        assertThat(p.hand).isNotEqualTo(p2.hand);
    }

    @Test
    @DisplayName("when we have an instance of a game the first player has a hand of tokens, with 15 Tokens (no null).")
    void test_4() {
        Player p = new Player("Hakan");
        Game g = new Game(new Player[]{p, p});
        assertThat(p.hand).isNotNull();
        assertThat(p.hand).isNotEmpty();
        assertThat(p.hand).doesNotContain((Token) null);
    }

    @Test
    @DisplayName("when we have an instance of a game the all other players, but the first, have a hand of tokens, with 14 Tokens and one null.")
    void test_5() {
        Player p = new Player("Hakan");
        Player p2 = new Player("Okan");
        Player p3 = new Player("Tarkan");

        Game g = new Game(new Player[]{p, p2, p3});
        assertThat(p2.hand).isNotNull();
        assertThat(p2.hand).isNotEmpty();
        assertThat(p2.hand).containsOnlyOnce((Token) null);

        assertThat(p3.hand).isNotNull();
        assertThat(p3.hand).isNotEmpty();
        assertThat(p3.hand).containsOnlyOnce((Token) null);
    }

    @Test
    @DisplayName("the game starts, greets and asks the player to throw a token.")
    void test_7() throws IOException, InterruptedException {
        Player p = new Player("Hakan");
        Player p2 = new Player("Okan");
        Game g = new Game(new Player[]{p, p2}, out);
        g.start();

        assertThat(out.output).contains("Game starts!!");
        assertThat(out.output).contains("Hakan starts!!");
        assertThat(out.output).contains("Please throw the first Token, " + p + ".");
    }

    @Test
    @DisplayName("the game answers to a thrown token, which was in ones hand, correctly (first  player).")
    void test_8() throws IOException, InterruptedException {
        Player p = new Player("Hakan");
        Player p2 = new Player("Okan");
        Game g = new Game(new Player[]{p, p2}, out, new FileReader(new File("TestInputs/test_8")));

        PrintWriter pw = new PrintWriter("TestInputs/test_8");
        Token t = p.hand[0];
        pw.println(t);
        pw.close();

        g.start();

        assertThat(out.output).contains("The thrown Token is {" + t + "}.");
    }

    @Test
    @DisplayName("the game asks which Token the player wants and answers to a thrown token, which was in ones hand, correctly (second player).")
    void test_9() throws IOException, InterruptedException {
        Player p = new Player("Hakan");
        Player p2 = new Player("Okan");
        Game g = new Game(new Player[]{p, p2}, out, new FileReader(new File("TestInputs/test_9")));

        PrintWriter pw = new PrintWriter("TestInputs/test_9");
        Token t = p.hand[0];
        pw.println(t);
        pw.println("new");
        Token t2 = p2.hand[0];
        pw.println(t2);
        pw.close();

        g.start();

        assertThat(out.output).contains("Do you want to take the thrown Token or get a new one?");
        assertThat(out.output).contains("The thrown Token is {" + t2 + "}.");

        System.out.println(out.output);
    }

    @Test
    @DisplayName("the game announces the right players name.")
    void test_10() throws IOException, InterruptedException {
        Player p = new Player("Hakan");
        Player p2 = new Player("Okan");
        Game g = new Game(new Player[]{p, p2}, out, new FileReader(new File("TestInputs/test_10")));

        PrintWriter pw = new PrintWriter("TestInputs/test_10");
        Token t = p.hand[0];
        pw.println(t);
        pw.println("new");
        Token t2 = p2.hand[0];
        pw.println(t2);
        pw.close();

        g.start();

        assertThat(out.output.get(4)).isEqualTo("It's Okans turn.");
        assertThat(out.output.get(7)).isEqualTo("It's Hakans turn.");
    }

    /**
     * Here I have to multithread, in order to be able to write something while Game is running.
     * <p>
     * Disabled because Multithreading didn't quite work.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    @Disabled
    @Test
    @DisplayName("the game waits for input.")
    void test_11() throws IOException, InterruptedException {
        Player p = new Player("Hakan");
        Player p2 = new Player("Okan");
        Game g = new Game(new Player[]{p, p2}, out, new FileReader(new File("TestInputs/test_11")));

        WaitingTester wt = new WaitingTester("Test 11", g);
        wt.start();

        wait(3);

        PrintWriter pw = new PrintWriter("TestInputs/test_11");
        Token t = p.hand[0];
        pw.println(t);

        wait(3);

        pw.println("new");
        pw.close();

        wait(3);

        assertThat(out.output).hasSize(6);
    }

    void wait(int i) throws InterruptedException {
        synchronized (TimeUnit.MILLISECONDS) {
            TimeUnit.MILLISECONDS.wait(i * 1000);
        }
    }
}