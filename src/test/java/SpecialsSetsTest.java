import SpecialSets.Sets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("This test tests, if ")
public class SpecialsSetsTest {

    @Test
    @DisplayName("the method subsetsWithMinSize works.")
    void test_1() {
        Token t = new Token(1, 1);
        Token t2 = new Token(1, 2);
        Token t3 = new Token(1, 3);
        Token t4 = new Token(1, 4);
        Token t5 = new Token(1, 5);

        Set<Token[]> set = new Sets<Token>().subsetsWithMinSize(List.of(t, t2, t3, t4, t5), 3);

        assertThat(set).contains(new Token[]{t, t2, t3});
        assertThat(set).contains(new Token[]{t, t2, t3, t4});
        assertThat(set).contains(new Token[]{t, t2, t3, t4, t5});
        assertThat(set).contains(new Token[]{t2, t3, t4});
        assertThat(set).contains(new Token[]{t2, t3, t4, t5});
        assertThat(set).contains(new Token[]{t3, t4, t5});
    }

    private int tokenArrComparator(Token[] tokens, Token[] tokens1) {
        List<Token> list1 = Arrays.stream(tokens).toList();
        List<Token> list2 = Arrays.stream(tokens1).toList();

        for (Token t : list1) for (Token t2 : list2) if (t.equals(t2)) return 0;
        return -1;
    }

    @Test
    @DisplayName("the method powerSetWithMaxSize works.")
    void test_2() {
        Token t = new Token(1, 1);
        Token t2 = new Token(1, 2);
        Token t3 = new Token(1, 3);
        Token t4 = new Token(1, 4);
        Token t5 = new Token(1, 5);

        Set<Set<Token>> set = new Sets<Token>().powerSetWithMaxSize(Set.of(t, t2, t3), 3);

        assertThat(set).contains(Set.of());
        assertThat(set).contains(Set.of(t));
        assertThat(set).contains(Set.of(t2));
        assertThat(set).contains(Set.of(t3));
        assertThat(set).contains(Set.of(t, t2));
        assertThat(set).contains(Set.of(t, t3));
        assertThat(set).contains(Set.of(t, t2, t3));
    }
}