import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("This test tests, if ")
public class GameTest {

    @Test
    @DisplayName("a game can be instantiated.")
    void test_1() {
        Game g = new Game(new Player[]{new Player("Hakan"), new Player("Okan")});

        assertThat(g).isInstanceOf(Game.class);
    }

    @Test
    @DisplayName("a game can't have only one player.")
    void test_2() {
        Player p = new Player("Hakan");

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new Game(new Player[]{p}));
    }

    @Test
    @DisplayName("players can't have the same name.")
    void test_3() {
        Player p = new Player("Hakan");
        Player p2 = new Player("Hakan");

        new Game(new Player[]{p, p2});
    }
}
