import Output.FakeOutput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("This test tests, if ")
public class GameTest {
    FakeOutput out = new FakeOutput();

    @Test
    @DisplayName("a game can be instantiated.")
    void test_1() {
        Game g = new Game(new Player[]{new Player("Hakan"), new Player("Okan")});

        assertThat(g).isInstanceOf(Game.class);
    }

    @Test
    @DisplayName("a game can't have only one player.")
    void test_2() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter("TestInputs/test_3");
        pw.println("exit");
        pw.close();

        new Game(new Player[]{new Player("Hakan")}, new FileReader("TestInputs/test_3"), out);

        assertThat(out.output).contains("There is a minimum of two players and a maximum of four in this match.");
    }

    @Test
    @DisplayName("players can't have the same name.")
    void test_3() throws FileNotFoundException {
        Player p = new Player("Hakan");
        Player p2 = new Player("Hakan");

        PrintWriter pw = new PrintWriter("TestInputs/test_3");
        pw.println("exit");
        pw.close();

        new Game(new Player[]{p, p2}, new FileReader("TestInputs/test_3"), out);

        System.out.println(out.output);

        assertThat(out.output).contains("Players must not have the same name.");
    }
}
