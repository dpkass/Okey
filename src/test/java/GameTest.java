import Output.FakeOutput;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName ("This test tests, if ")
public class GameTest {
    FakeOutput out = new FakeOutput();

    @Test
    @DisplayName ("a game can be instantiated.")
    void test_1() {
        Game g = new Game(new Player[] { new Player("Hakan"), new Player("Okan") });

        assertThat(g).isInstanceOf(Game.class);
    }

    @Test
    @DisplayName ("a game can't have only one player.")
    void test_2() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter("TestInputs/test_3");
        pw.println("exit");
        pw.close();

        new Game(new Player[] { new Player("Hakan") }, new FileReader("TestInputs/test_3"), out);

        assertThat(out.output).contains("There is a minimum of two players and a maximum of four in this match.");
    }

    @Test
    @DisplayName ("players can't have the same name.")
    void test_3() throws FileNotFoundException {
        Player p = new Player("Hakan");
        Player p2 = new Player("Hakan");

        PrintWriter pw = new PrintWriter("TestInputs/test_3");
        pw.println("exit");
        pw.close();

        new Game(new Player[] { p, p2 }, new FileReader("TestInputs/test_3"), out);

        System.out.println(out.output);

        assertThat(out.output).contains("Players must not have the same name.");
    }

    @Test
    @DisplayName ("the game starts, greets and asks the player to throw a token.")
    void test_7() {
        Player p = new Player("Hakan");
        Player p2 = new Player("Okan");
        Game m = new Game(new Player[] { p, p2 }, new StringReader("exit"), out);

        m.start();

        assertThat(out.output).contains("Match starts!!");
        assertThat(out.output).contains("Please throw the first Token, " + p + ".");
    }

    @Test
    @DisplayName ("the game answers to a thrown token, which was in ones hand, correctly (first  player).")
    void test_8() throws IOException {
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
    @DisplayName ("the game asks which Token the player wants and answers to a thrown token, which was in ones hand, " +
            "correctly (second player).")
    void test_9() throws IOException {
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
    @DisplayName ("the game announces the right players name.")
    void test_10() throws IOException {
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
    @DisplayName ("the game waits for input.")
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
    @DisplayName ("the match will terminate after someone won the game.")
    void test_20() throws FileNotFoundException {
        Player p = new Player("Hakan");
        Player p2 = new Player("Okan");

        Token[] tokens = new Token[15];
        tokens[0] = new Token(1, 11);
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
        pw.println(String.format("%s\nthrown\nwin %s\nexit", p1Throw, p1Throw));
        pw.close();

        p2.hand = tokens;

        g.start();

        String s1 = "Match starts!!";
        String s2 = "Please throw the first Token, Hakan.";
        String s3 = "The thrown Token is {" + p1Throw + "}.";
        String s4 = "It's Okans turn.";
        String s5 = "Do you want to take the thrown Token {" + p1Throw + "} or get a new one?";
        String s6 = "The thrown Token is {" + p1Throw + "}.";
        String s7 = "There is 1 winning combination.";
        String s8 = out.output.get(7);
        String s9 = "The winner is Okan. Congratulations!!";
        String s10 = "The next match is about to start.\n\n\n\n\n";
        String s11 = out.output.get(11);
        String s12 = "A player left or exited the game.";

        assertThat(out.output).containsExactly(s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s1, s11, s12);
    }
}
