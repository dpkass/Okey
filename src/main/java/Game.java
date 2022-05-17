import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Spiel {
    private Player[] spieler;
    private List<Token> tokens = new ArrayList<>();
    private Token joker;

    Spiel(Player[] spieler) {
        this.spieler = spieler;
        init();
    }

    /**
     * This method initializes the game.
     * <p>
     * It fills the ArrayList of available tokens and shuffels it and determines a joker. Fake jokers have color -1 and
     * number -1.
     */
    private void init() {
        for (int i = 0; i < 13; i++)
            for (int j = 0; j < 4; j++)
                tokens.add(new Token(j, i));

        joker = tokens.get((int) (Math.random() * tokens.size()));

        tokens.add(new Token(-1, -1));
        tokens.add(new Token(-1, -1));

        Collections.shuffle(tokens);
    }
}
