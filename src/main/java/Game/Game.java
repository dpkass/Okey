package Game;

import Output.KonsoleOutput;
import Output.Output;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Game {
    final int maxWaitTime = 100;
    private final Output out;
    private final BufferedReader reader;
    private final Map<Player, Integer> score = new HashMap<>();
    private Player[] players;
    private Match currentMatch;

    public Game(Player[] players) {
        this(players, new InputStreamReader(System.in));
    }

    public Game(Player[] players, Reader reader) {
        this(players, reader, new KonsoleOutput());
    }

    public Game(Player[] players, Reader reader, Output out) {
        this.players = players;
        this.reader = new BufferedReader(reader);
        this.out = out;
        if (init() < 0) newPlayers();
        currentMatch = new Match(this.players, out, this);
    }

    public Game() {
        this(null);
    }

    /**
     * Starts the game. As long as no one lost or exited it will keep running.
     */
    public void start() {
        while (playersLeft()) {
            Player winner = currentMatch.start();
            if (winner == null) {
                out.println("A player left or exited the game.");
                return;
            }
            endOfMatch(winner);
            currentMatch = new Match(players, out, this);
        }
    }

    /**
     * Removes all losers from the game.
     */
    private void removeLosers() {
        Set<Player> stillInGame =
                score.keySet()
                     .stream()
                     .filter(k -> score.get(k) != 0)
                     .collect(Collectors.toCollection(HashSet::new));
        players = stillInGame.toArray((int value) -> new Player[value]);
    }

    /**
     * Initialized player scores. If there are not 2 to 4 players, or they have the same name, it will ask for the
     * players to type in their names first.
     *
     * @return -1 if a naming condition is broken, else 0.
     */
    private int init() {
        if (players == null) return -1;

        if (players.length < 2 || players.length > 4) {
            out.println("There is a minimum of two players and a maximum of four in this match.");
            return -1;
        }

        for (Player p : players)
            for (Player p2 : players)
                if (p != p2 &&
                        p.toString().equals(p2.toString())) {
                    out.println("Players must not have the same name.");
                    return -1;
                }

        for (Player p : players) score.put(p, 10);
        return 0;
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
            } catch (Exception ignored) {}
        }
        return s;
    }

    /**
     * Asks for players to enter their names.
     */
    private void newPlayers() {
        out.println("Please enter the player names.");

        String s = waitForInput(100);

        setPlayers(Arrays.stream(s.split(" ")).map(Player::new).toArray((int value) -> new Player[value]));
        init();
    }

    /**
     * Prints relevant infos at the end of each match and reduces the losers points.
     *
     * @param winner the winner of the last match
     */
    private void endOfMatch(Player winner) {
        reducePoints(winner);
        removeLosers();

        out.println("The winner is " + winner + ". Congratulations!!");
        if (playersLeft())
            out.println("The next match is about to start.\n\n\n\n\n");
        else {
            out.println(winner + " won the game!!");
            printCandy();
            out.println("\n\nSee you next time.");
        }
    }

    /**
     * Print some sweets for the players
     */
    private void printCandy() {
        out.println("Have some candy.\n\n");
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
    }

    /**
     * Reduces the points of the losers of the last match.
     *
     * @param winner winner of the last game
     */
    private void reducePoints(Player winner) {
        for (Player p : players) if (!p.equals(winner)) score.put(p, score.get(p) - 2);
    }

    /**
     * Finds out if there are at least two players left
     *
     * @return true if at least two players, else false.
     */
    public boolean playersLeft() {
        return players.length >= 2;
    }

    /**
     * Gives a string, with the scores of all players
     *
     * @return the score of all players
     */
    public String getScore() {
        StringBuilder s = new StringBuilder();
        for (Player p : players)
            s.append(p.toString()).append(": ").append(getScoreOf(p)).append('\n');
        return s.toString();
    }

    /**
     * Gives the score of a given player
     *
     * @param p Game.Player to look up the score of
     * @return the score of the player
     */
    public int getScoreOf(Player p) {
        return score.get(p);
    }

    /**
     * Gives the score of a given player
     *
     * @param s Name of player to look up the score of
     * @return the score of the player
     */
    public int getScoreOf(String s) {
        return score.get(getPlayerByName(s));
    }

    /**
     * Gives the player object with the given name
     *
     * @param s Name of player to get the object of
     * @return the score of the player
     */
    private Player getPlayerByName(String s) {
        for (Player p : players) if (p.toString().equals(s)) return p;
        return null;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }
}
