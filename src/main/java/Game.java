import Output.KonsoleOutput;
import Output.Output;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Game {
    final int maxWaitTime = 100;

    private Player[] players;
    private Map<Player, Integer> score = new HashMap<>();
    private BufferedReader reader;
    private Output out;
    private Match currentMatch;

    public Game(Player[] players) {
        this(players, new BufferedReader(new InputStreamReader(System.in)));
    }

    public Game(Player[] players, Reader reader) {
        this(players, new BufferedReader(new InputStreamReader(System.in)), new KonsoleOutput());
    }

    public Game(Player[] players, Reader reader, Output out) {
        this.players = players;
        this.reader = new BufferedReader(reader);
        this.out = out;
        if (init() < 0) newPlayers();
        currentMatch = new Match(players, out, this);
    }

    public void start() {
        while (noOneLost()) {
            Player winner = currentMatch.start();
            if (winner == null) return;
            endOfMatch(winner);
            Match currentMatch = new Match(players, out, this);
        }
    }

    /**
     * Waits for Input.
     *
     * @param waitTime periodical wait time between each check
     * @return String which was read from Input
     * @field maxWaitTime determines maximum amount of seconds to wait
     */
    String waitForInput(int waitTime) {
        String s = "exit";
        int i = 0;
        synchronized (TimeUnit.MILLISECONDS) {
            try {
                while ((s = reader.readLine()) == null && (i += waitTime) < 100 * 1000)
                    TimeUnit.MILLISECONDS.wait(waitTime);
            } catch (Exception e) {}
        }
        return "exit".equals(s) ? null : s;
    }

    private void newPlayers() {
        Scanner in = new Scanner(System.in);

        out.println("Players must not have the same name.");
        out.println("Please enter the new player names.");

        String s = waitForInput(100);

        setPlayers((Player[]) Arrays.stream(s.split(" ")).map(p -> new Player(p)).toArray());
        init();
    }

    private int init() {
        if (players.length < 2 || players.length > 4) {
            new KonsoleOutput().println("There is a minimum of two players and a maximum of four in this match.");
            newPlayers();
        }

        for (Player p : players)
            for (Player p2 : players) if (p != p2 && p.equals(p2)) return -1;

        for (Player p : players) score.put(p, 10);
        return 0;
    }

    private void endOfMatch(Player winner) {
        reducePoints(winner);

        if (noOneLost())
            out.println("The next match is about to start.\n\n\n\n\n");
        else {
            out.println("You won the game!!");
            out.println("Have a candy.\n\n");
            out.println("""
                         .-""-.      ___
                          \\  "-.  /      \\  .-"  /
                           > -=.\\/        \\/.=- <
                           > -='/\\        /\\'=- <
                          /__.-'  \\      /  '-.__\\
                                   '-..-'
                            ____
                          .' /  '.
                         /  (  .-'\\
                        |'.__\\/__  |
                        |    /\\  '.|
                         \\.-'  )  /
                           '.__/_.'
                                ____
                              .' /:::.
                             /  (:::-'\\
                            |:\\__\\/__  |
                            |::::/\\:::\\|
                             \\::'  )::/
                               '.__/::'
                        """);
            out.println("\n\nSee you next time.");
        }
    }

    private void reducePoints(Player winner) {
        for (Player p : players) if (!p.equals(winner)) score.put(p, score.get(p) - 2);
    }

    public boolean noOneLost() {
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

    public Match getCurrentMatch() {
        return currentMatch;
    }
}
