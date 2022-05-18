import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

class Game {
    Map<Player, Token[]> playerHandMap = new HashMap<>();
    Player[] players;
    int currentPlayer = 0;
    List<Token> tokens = new ArrayList<>();
    Token joker = new Token(-1, -1);
    Token lastThrown = null;
    Player winner = null;
    BufferedReader reader;
    Output out;
    int maxWaitTime = 100;


    Game(Player[] players, Output out, Reader reader) {
        if (players.length < 2 || players.length > 4)
            throw new IllegalArgumentException("There is a minimum of two players and a maximum of four in this game.");
        this.reader = new BufferedReader(reader);
        this.players = players;
        this.out = out;
        init();
    }

    Game(Player[] players, Output out) {
        this(players, out, new BufferedReader(new InputStreamReader(System.in)));
    }

    Game(Player[] players) {
        this(players, new KonsoleOutput());
    }

    /**
     * Starts the game.
     * <p>
     * Since there is no decision in the first round, to minimize control flow I made an extra Method for the first
     * round.
     */
    public void start() throws IOException, InterruptedException {
        if (playFirst() == -1) return;
        play();
    }

    /**
     * This method initializes the game.
     * <p>
     * It fills the ArrayList of available tokens and shuffles it and determines a joker. It also distributes the
     * tokens.
     * <p>
     * Jokers have color -1 and number -1. For simplification I took out the pulling joker part for now.
     */
    void init() {
        createTokens();

//        joker = tokens.get((int) (Math.random() * tokens.size()));

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
            temp[14] = new Token(Token.HEAVY, -1);
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
    int playFirst() throws IOException, InterruptedException {
        out.println("Game starts!!");
//        out.println("The joker is " + joker + ".");
        showHand();
        out.println("Please throw the first Token, " + players[currentPlayer] + ".");
        if (thrownToken() < 0) return -1;
        currentPlayer = (currentPlayer + 1) % players.length;
        return 0;
    }

    /**
     * While there is no winner and no stopping condition the game is played.
     */
    void play() throws IOException, InterruptedException {
        while (winner == null) {
            out.println("It's " + players[currentPlayer] + "s turn.");
            showHand();
            out.println("Do you want to take the thrown Token {" + lastThrown + "} or get a new one?");
            if (giveToken() < 0) return;
            showHand();
            if (thrownToken() < 0) return;
            currentPlayer = (currentPlayer + 1) % players.length;
        }
    }

    /**
     * Gives a Token to the player depending on what he wants. Method waits for 20 seconds before ending the game,
     * because of missing response.
     *
     * @return 0 means player got his Token. -1 means player wants to exit or player didn't respond.
     */
    int giveToken() throws IOException, InterruptedException {
        int waitTime = 100;
        String s = waitForInput(waitTime);
        if (s == null) return -1;

        switch (s) {
            case "new" -> {
                players[currentPlayer].getNewToken(tokens.get(0));
                tokens.remove(0);
            }
            case "thrown" -> players[currentPlayer].getNewToken(lastThrown);
            default -> {
                out.println("Please write \"new\" for a new Token or \"thrown\" for the thrown Token.");
                return giveToken();
            }
        }
        return 0;
    }

    /**
     * Waits for Input.
     *
     * @param waitTime periodical wait time between each check
     * @return String which was read from Input
     * @field maxWaitTime determines maximum amount of seconds to wait
     */
    private String waitForInput(int waitTime) throws IOException, InterruptedException {
        String s;
        int i = 0;
        synchronized (TimeUnit.MILLISECONDS) {
            while ((s = reader.readLine()) == null) {
                if (i > maxWaitTime * 1000) break;
                TimeUnit.MILLISECONDS.wait(waitTime);
                i += waitTime;
            }
        }
        return "exit".equals(s) ? null : s;
    }

    /**
     * Tests if a player threw a Token that he has in his hand. If he did, his hand will be adjusted.
     *
     * @return 0 means the game finished with a winner. -1 means player wants to exitor player didn't respond.
     */
    int thrownToken() throws IOException, InterruptedException {
        int waitTime = 100;
        String s = waitForInput(waitTime);
        if (s == null) return -1;
        Token thrown = null;

        String[] parts = s.split(" ");
        try {
            if (parts[0].equals("Joker")) thrown = new Token(-1, -1);
            else if (parts.length == 2) thrown = new Token(parts[0], Integer.parseInt(parts[1]));
            else if (parts.length == 3 && parts[0].equals("win")) {
                thrown = new Token(parts[0], Integer.parseInt(parts[1]));
                if (currPlayerWon(thrown)) {
                    winner = players[currentPlayer];
                    return 0;
                } else {
                    out.println("If you throw the token {" + thrown + "} it isn't a win. Please throw another token.");
                    return thrownToken();
                }
            } else
                throw new IllegalArgumentException();
        } catch (IllegalArgumentException e) {
            out.println("Invalid Argument. Please write the color of your token and then the number e.g. \"Gelb 5\". For Joker, write \"Joker\".");
            thrownToken();
        }

        if (!isInCurrPlayersHand(thrown)) {
            out.println("The thrown Token is not in your Hand. Please throw a Token you have.");
            return thrownToken();
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
     * @param needle Token to remove from current Players Hand
     */
    void removeFromCurrPlayersHand(Token needle) {
        Token[] hand = playerHandMap.get(players[currentPlayer]);
        int i = getIndex(hand, needle);
        swap(hand, i, 14);
        hand[14] = new Token(Token.HEAVY, -1);
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
     * @param needle Token to look for in current players Hand
     * @return thrown in current players hand?
     */
    boolean isInCurrPlayersHand(Token needle) {
        Token[] temp = playerHandMap.get(players[currentPlayer]);
        for (Token token : temp)
            if (token.equals(needle))
                return true;
        return false;
    }

    /**
     * Displays the hand of the current player.
     */
    void showHand() {
        Arrays.sort(players[currentPlayer].hand);
        System.out.println(playerHandMap.entrySet().stream().filter(e -> e.getKey() == players[currentPlayer]).map(e -> e.getKey() + ":" + Arrays.toString(e.getValue()))
                .collect(Collectors.joining("|")));
    }

    /**
     * Tests if the current Player wins after removing a given token
     *
     * @param t token to remove
     * @return true if win, false if not.
     */
    boolean currPlayerWon(Token t) {
        if (isInCurrPlayersHand(t))
            removeFromCurrPlayersHand(t);
        int jokerCount = countJokersOfCurrentPlayer();

        Set<Token[]> combinations = new HashSet<>();

        combinations.addAll(getAllStraightsInCurrPlayersHand(jokerCount));
        combinations.addAll(getAllFlushesInCurrPlayersHand());

        Set<Set<Token[]>> powerSet = PowerSet.powerSetWithMaxSize(combinations, 4);
        Set<Token[]> cleanPowerSet = reduce(powerSet);

        return cleanPowerSet.size() - countNonWinningCombinations(cleanPowerSet, jokerCount) >= 1;
    }

    /**
     * Counts how many of the remaining combinations are not winning
     *
     * @param set PowerSet of all combinations
     */
    private int countNonWinningCombinations(Set<Token[]> set, int jokers) {
        int i = 0;
        for (Token[] t : set) {
            int availableJokers = jokers - countJokers(t);
            if (t.length + availableJokers != 14)
                i++;
        }
        return i;
    }

    /**
     * Reduces List<Set<Token[]>> to Set<Token[]>.
     *
     * @param powerSet powerSet within which we reduce
     */
    private Set<Token[]> reduce(Set<Set<Token[]>> powerSet) {
        Set<Token[]> res = new HashSet<>();
        for (Set<Token[]> set : powerSet) {
            int elements = 0, i = 0;
            for (Token[] t : set) elements += t.length;

            Token[] t = new Token[elements];
            for (Token[] tokens : set) for (Token token : tokens) t[i++] = token;

            res.add(t);
        }
        return res;
    }

    /**
     * Counts the amount of jokers the current player hand.
     *
     * @return amount of jokers the current player hand.
     */
    private int countJokersOfCurrentPlayer() {
        return countJokers(players[currentPlayer].hand);
    }

    /**
     * Counts the amount of jokers in the given Array.
     *
     * @return amount of jokers the given Array.
     */
    private int countJokers(Token[] t) {
        if (t.length < 3) return 0;

        Arrays.sort(t);

        if (t[1].getColor() == Token.JOKER) return 2;
        if (t[0].getColor() == Token.JOKER) return 1;
        return 0;
    }

    /**
     * Calculates all straights in the hand of the current player. A straight is an at least 3 token long sequence,
     * where the tokens have the same color and consecutive numbers.
     * <p>
     * Depending on the amount of jokers there might be an offset, where the joker would fit in.
     *
     * @param offset amount of possible skips
     * @return all straights in the hand of the current player
     */
    List<Token[]> getAllStraightsInCurrPlayersHand(int offset) {
        List<Token[]> res = new ArrayList<>();
        Token[] tokens = players[currentPlayer].hand;
        int tempOffset = offset, jokerInsert = -1;

        Arrays.sort(players[currentPlayer].hand);

        List<Token> partRes = new ArrayList<>();
        for (int i = 0; i < tokens.length - 1; i++) {       // -1 since last one is HEAVY
            partRes.add(tokens[i]);
            // Colors are not the same or numbers are not consecutive (even with all jokers the player has)
            if (tokens[i].getColor() != tokens[i + 1].getColor() || tokens[i].getNumber() + 1 + tempOffset < tokens[i + 1].getNumber()) {
                if (partRes.size() < 3) partRes.clear();
                else {
                    res.addAll(partition(partRes));
                    partRes.clear();
                    tempOffset = offset;
                    if (jokerInsert != -1)
                        i = jokerInsert;
                    jokerInsert = -1;
                }
            } else if (tokens[i].getNumber() + 2 == tokens[i + 1].getNumber() && tempOffset >= 1) {                     // 1 joker is needed to fill
                tempOffset--;
                jokerInsert = i + 1;
                partRes.add(joker);
            } else if (tokens[i].getNumber() + 3 == tokens[i + 1].getNumber() && tempOffset == 2) {                     // 2 jokers are needed to fill
                tempOffset--;
                tempOffset--;
                jokerInsert = i + 1;
                partRes.add(joker);
                partRes.add(joker);
            }
        }
        return res;
    }

    /**
     * Calculates all flushes in the hand of the current player. A flush is a 3 to 4 token sized set, whose tokens all
     * have the same number, big are different in color,
     *
     * @return all flushes in the hand of the current player
     */
    List<Token[]> getAllFlushesInCurrPlayersHand() {
        List<Token[]> res = new ArrayList<>();
        Token[] tokens = players[currentPlayer].hand;

        Arrays.sort(tokens);

        List<Token> partRes = new ArrayList<>();
        for (Token t : tokens) {
            partRes.add(t);
            for (Token t2 : tokens) {
                if (t == t2) continue;
                if (t.getNumber() == t2.getNumber() && t.getColor() != t2.getColor()) partRes.add(t2);
            }
            if (partRes.size() >= 3) res.addAll(partition(partRes));
            partRes.clear();
        }
        return res;
    }

    /**
     * Creates partitions of a list with minimum size of 3. All partitions have consecutive elements.
     *
     * @param list Set to partition
     * @return all partitions of a given list, that have consecutive elements
     */
    List<Token[]> partition(List<Token> list) {
        List<Token[]> res = new ArrayList<>();

        for (int i = 0; i < list.size() - 2; i++)                           // i is start of Token[]
            for (int j = 0; list.size() - j > i + 2; j++) {                 // j makes range of Token[] smaller (min 3)
                Token[] t = new Token[list.size() - i - j];
                for (int k = i; k < list.size() - j; k++) t[k - i] = list.get(k);
                res.add(t);
            }
        return res;
    }
}
