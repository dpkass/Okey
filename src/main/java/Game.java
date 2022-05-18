import Output.KonsoleOutput;
import Output.Output;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Game {
    private Player[] players;
    private Map<Player, Integer> score = new HashMap<>();

    public Game(Player[] players) {
        this.players = players;
        if (init() < 0) newPlayers();
        start();
    }

    private void newPlayers() {
        Output out = new KonsoleOutput();
        Scanner in = new Scanner(System.in);

        out.println("Players must not have the same name.");
        out.println("Please enter the new player names.");

        String s = in.nextLine();
        in.close();

        setPlayers((Player[]) Arrays.stream(s.split(" ")).map(p -> new Player(p)).toArray());
        init();
    }


    private int init() {
        for (Player p : players)
            for (Player p2 : players) if (p.equals(p2)) return -1;

        for (Player p : players) score.put(p, 10);
        return 0;
    }

    public void start() {
        while (noOneLost()) {
            Match m = new Match(players, this);
            Player winner = m.start();
            reducePoints(winner);
        }
    }

    private void reducePoints(Player winner) {
        for (Player p : players) if (!p.equals(winner)) score.put(p, score.get(p) - 2);
    }

    private boolean noOneLost() {
        return score.entrySet().stream().filter(v -> v.getValue() == 0).count() == 0;
    }

    public String getScore() {
        String s = "";
        for (Player p :
                players) {
            s += p.toString() + getScoreOf(p);
        }
        return s;
    }

    public int getScoreOf(Player p) {
        return score.get(p);
    }

    public int getScoreOf(String s) {
        return score.get(getPlayer(s));
    }

    private Player getPlayer(String s) {
        for (Player p : players) if (p.equals(s)) return p;
        return null;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }
}
