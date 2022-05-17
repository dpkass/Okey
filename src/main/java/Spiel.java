import java.util.ArrayList;
import java.util.List;

public class Spiel {
    private Spieler[] spieler;
    private List<Spielstein> steine = new ArrayList<>();
    private Spielstein joker;

    public Spiel(Spieler[] spieler) {
        this.spieler = spieler;
        init();
    }

    private void init() {
        for (int i = 0; i < 13; i++)
            for (int j = 0; j < 4; j++)
                steine.add(new Spielstein(j, i));
        steine.add(new Spielstein(-1, -1));
        steine.add(new Spielstein(-1, -1));
    }
}
