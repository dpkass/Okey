import java.io.*;
import java.util.*;

class Game {
    Map<Player, Token[]> playerHandMap = new HashMap<>();
    Player[] players;
    int currentPlayer = 0;
    List<Token> tokens = new ArrayList<>();
    List<Token> trashed = new ArrayList<>();
    Token joker;
    Token lastThrown = null;
    int roundnumber = 0;
    Player winner = null;
    BufferedReader reader;
    Output out;


    Game(Player[] players, Output out, Reader reader) {
        if (players.length < 2 || players.length > 4)
            throw new IllegalArgumentException("There is a minimum of two players and a maximum of four in this game.");
        this.reader = new BufferedReader(reader);
        this.players = players;
        this.out = out;
        init();
    }

    Game(Player[] players, Output out) {
        if (players.length < 2 || players.length > 4)
            throw new IllegalArgumentException("There is a minimum of two players and a maximum of four in this game.");
        this.reader = new BufferedReader(new InputStreamReader(System.in));
        this.players = players;
        this.out = out;
        init();
    }

    Game(Player[] players) {
        new Game(players, new KonsoleOutput());
    }

    /**
     * Starts the game.
     * <p>
     * Since there is no decision in the first round, to minimize control flow I made an extra Method for the first
     * round.
     */
    public void start() throws IOException {
        if (playFirst() == -1) return;
        play();
    }


    /**
     * This method initializes the game.
     * <p>
     * It fills the ArrayList of available tokens and shuffels it and determines a joker. It also distributes the
     * tokens.
     * <p>
     * Fake jokers have color -1 and number -1.
     */
    void init() {
        createTokens();

        joker = tokens.get((int) (Math.random() * tokens.size()));

        tokens.add(new Token(-1, -1));
        tokens.add(new Token(-1, -1));

        Collections.shuffle(tokens);

        distributeTokens();
    }


    /**
     * This method distributes all the tokens. One player gets 15.
     * <p>
     * The game saves the list of Tokens each person gets, in case someone tries to cheat.
     */
    void distributeTokens() {
        for (Player p : players) {
            Token[] temp = new Token[15];
            for (int i = 0; i < 14; i++) {
                temp[i] = tokens.get(i);
                tokens.remove(i);
            }
            p.setHand(temp);
            playerHandMap.put(p, temp);
        }
        players[0].getNewToken(tokens.get(0));
        tokens.remove(0);
    }

    /**
     * This method creates all the tokens.
     */
    void createTokens() {
        for (int i = 0; i < 2; i++)                     // duplicate tokens
            for (int j = 1; j <= 13; j++)               // 13 numbers
                for (int k = 0; k < 4; k++)             // 4 colors
                    tokens.add(new Token(k, j));
    }

    /**
     * First round of the game is played here.
     *
     * @return 0 means the game finished with a winner. -1 means a player is not responding or exited the game.
     */
    int playFirst() throws IOException {
        out.println("Game starts!!");
        out.println("Please throw the first Token, " + players[currentPlayer].getName() + ".");
        if (thrownToken() < 0) return -1;
        currentPlayer = (currentPlayer + 1) % players.length;
        return 0;
    }

    /**
     * While there is no winner and no stopping condition the game is played.
     *
     * @return 0 means the game finished with a winner. -1 means a player is not responding or exited the game.
     */
    int play() throws IOException {
        while (winner == null) {
            out.println("It's " + players[currentPlayer].getName() + "s turn.");
            out.println("Do you want to take the thrown Token or get a new one?");
            if (giveToken() < 0) return -1;
            if (thrownToken() < 0) return -1;
            currentPlayer = (currentPlayer + 1) % players.length;
        }
        return 0;
    }

    /**
     * Gives a Token to the player depending on what he wants.
     *
     * @return 0 means player got his Token. -1 means player wants to exit. -2 means player didn't respond.
     */
    int giveToken() throws IOException {
        String s = reader.readLine();
        if (s == null) return -2;

        switch (s) {
            case "new" -> {
                players[currentPlayer].getNewToken(tokens.get(0));
                tokens.remove(0);
            }
            case "thrown" -> players[currentPlayer].getNewToken(lastThrown);
            case "exit" -> {return -1;}
            default -> {
                out.println("Please write \"new\" for a new Token or \"thrown\" for the thrown Token.");
                return giveToken();
            }
        }
        return 0;
    }

    /**
     * Tests if a player threw a Token that he has in his hand. If he did, his hand will be adjusted.
     *
     * @return 0 means the game finished with a winner. -1 means player wants to exit. -2 means player didn't respond.
     */
    int thrownToken() throws IOException {
        String s = reader.readLine();
        if (s == null) return -2;
        Token thrown = null;

        String[] parts = s.split(" ");
        try {
            if (parts[0].equals("Joker")) thrown = new Token(-1, -1);
            else if (parts.length == 2) thrown = new Token(parts[0], Integer.parseInt(parts[1]));
            else if (parts[0].equals("exit")) return -1;
        } catch (Exception e) {
            out.println("Invalid input. Please put throw a Token.");
            thrownToken();
        }

        if (!isInCurrPlayersHand(thrown)) {
            out.println("The thrown Token is not in your Hand. Please throw a Token you have.");
            thrownToken();
        } else {
            out.println("The thrown Token is {" + thrown + "}.");
            lastThrown = thrown;
            removeFromCurrPlayersHand(thrown);
        }
        return 0;
    }

    /**
     * Removes a given Token from the current players Hand (by moving it to the last spot).
     *
     * @param thrown
     */
    void removeFromCurrPlayersHand(Token thrown) {
        Token[] hand = playerHandMap.get(players[currentPlayer]);
        int i = getIndex(hand, thrown);
        swap(hand, i, 14);
    }

    /**
     * Gives the index of a given needle
     *
     * @param hand   Array to search in
     * @param needle Token to look for
     * @return index of needle in Array
     */
    private int getIndex(Token[] hand, Token needle) {
        for (int i = 0; i < hand.length; i++)
            if (hand[i].equals(needle)) return i;
        return -1;
    }

    /**
     * Swaps the elements in the array at the two given indices.
     *
     * @param a Array to swap in
     * @param i First index to swap
     * @param j Second index to swap
     */
    void swap(Token[] a, int i, int j) {
        Token t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    /**
     * Checks if the thrown Token is in the hand of the current player.
     *
     * @param thrown
     * @return thrown in current players hand?
     */
    boolean isInCurrPlayersHand(Token thrown) {
        Token[] temp = playerHandMap.get(players[currentPlayer]);
        for (int i = 0; i < temp.length; i++)
            if (temp[i].equals(thrown))
                return true;
        return false;
    }
}
