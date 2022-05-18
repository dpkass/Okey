import Output.KonsoleOutput;
import Output.Output;
import SpecialSets.Sets;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

class Match {
    Game game;
    Map<Player, Token[]> playerHandMap = new HashMap<>();
    Player[] players;
    int currentPlayer = 0;
    List<Token> tokens = new ArrayList<>();
    Token joker = new Token(-1, -1);
    Token lastThrown = null;
    Player winner = null;
    Output out;

    Match(Player[] players, Output out, Game game) {
        this.players = players;
        this.out = out;
        this.game = game;
        init();
    }

    Match(Player[] players, Game game) {
        this(players, new KonsoleOutput(), game);
    }

    public static Predicate<Token[]> distinctByKey(Function<? super Token[], ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    /**
     * Starts the match.
     * <p>
     * Since there is no decision in the first round, to minimize control flow I made an extra Method for the first
     * round.
     */
    public Player start() {
        if (playFirst() == -1) return null;
        play();
        return winner;
    }

    /**
     * This method initializes the match.
     * <p>
     * It fills the ArrayList of available tokens and shuffles it and determines a joker. It also distributes the
     * tokens.
     * <p>
     * Jokers have color -1 and number -1. For simplification I took out the pulling joker part for now.
     */
    private void init() {
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
     * The match saves the list of Tokens each person gets, in case someone tries to cheat.
     */
    private void distributeTokens() {
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
    private void createTokens() {
        for (int i = 0; i < 2; i++)                     // duplicate tokens
            for (int j = 1; j <= 13; j++)               // 13 numbers
                for (int k = 0; k < 4; k++)             // 4 colors
                    tokens.add(new Token(k, j));
    }

    /**
     * First round of the match is played here.
     *
     * @return 0 means the match finished with a winner. -1 means a player is not responding or exited the match.
     */
    private int playFirst() {
        out.println("Match starts!!");
        showHand();
        out.println("Please throw the first Token, " + players[currentPlayer] + ".");
        if (thrownToken() < 0) return -1;
        return 0;
    }

    /**
     * While there is no winner and no stopping condition the match is played.
     */
    private void play() {
        while (winner == null) {
            nextPlayer();
            if (giveToken() < 0) return;
            showHand();
            if (thrownToken() < 0) return;
        }
        out.println("The winner is " + winner + ". Congratulations!!");
    }

    /**
     * Gives the next Player the turn.
     */
    private void nextPlayer() {
        currentPlayer = (currentPlayer + 1) % players.length;
        out.println("It's " + players[currentPlayer] + "s turn.");
        showHand();
        out.println("Do you want to take the thrown Token {" + lastThrown + "} or get a new one?");
    }

    /**
     * Gives a Token to the player depending on what he wants. Method waits for 20 seconds before ending the match,
     * because of missing response.
     *
     * @return 0 means player got his Token. -1 means player wants to exit or player didn't respond.
     */
    private int giveToken() {
        String s = game.waitForInput(100);
        if (s == null) return -1;

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
     * @return 0 means the match finished with a winner. -1 means player wants to exit or player didn't respond.
     */
    private int thrownToken() {
        String s = game.waitForInput(100);
        if (s == null) return -1;
        Token thrown = null;

        try {
            if ((thrown = throwAction(s)) == null)
                if (winner != null) return 0;
                else return -1;
        } catch (IllegalArgumentException e) {
            out.println("Invalid Argument. Please write the color of your token and then the number e.g. \"Gelb 5\". For Joker, write \"Joker\".");
            return thrownToken();
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

    private Token throwAction(String s) {
        String[] parts = s.split(" ");
        if (parts[0].equals("exit")) return null;
        else if (parts[0].equals("Joker")) return new Token(-1, -1);
        else if (parts.length == 2) return new Token(parts[0], Integer.parseInt(parts[1]));
        else if (parts.length == 3 && parts[0].equals("win")) {
            Token thrown = new Token(parts[1], Integer.parseInt(parts[2]));
            if (currPlayerWon(thrown)) {
                winner = players[currentPlayer];
                return null;
            } else {
                out.println("If you throw the token {" + thrown + "} it isn't a win. Please throw another token.");
                players[currentPlayer].hand[14] = thrown;
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * Removes a given Token from the current players Hand (by moving it to the last spot).
     *
     * @param needle Token to remove from current Players Hand
     */
    private void removeFromCurrPlayersHand(Token needle) {
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
    private void swap(Token[] a, int i, int j) {
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
    private boolean isInCurrPlayersHand(Token needle) {
        Token[] temp = playerHandMap.get(players[currentPlayer]);
        for (Token token : temp)
            if (token.equals(needle))
                return true;
        return false;
    }

    /**
     * Displays the hand of the current player.
     */
    Map<Player, Token[]> showHand() {
        Arrays.sort(players[currentPlayer].hand);
        System.out.println(playerHandMap.entrySet().stream().filter(e -> e.getKey() == players[currentPlayer]).map(e -> e.getKey() + ":" + Arrays.toString(e.getValue()))
                                        .collect(Collectors.joining("|")));
        return playerHandMap;
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

        Set<Set<Token[]>> powerSet = new Sets<Token[]>().powerSetWithMaxSize(combinations, 4);
        Set<Token[]> cleanPowerSet = reduce(powerSet);

        cleanPowerSet = removeNonWinningCombinations(cleanPowerSet, jokerCount);

        printCombinations(cleanPowerSet);

        return cleanPowerSet.size() > 0;
    }

    private void printCombinations(Set<Token[]> cleanPowerSet) {
        out.println(String.format("There %s %d winning combination%s.", cleanPowerSet.size() == 1 ? "is" : "are",
                cleanPowerSet.size(), cleanPowerSet.size() == 1 ? "" : "s"));
        if (cleanPowerSet.size() == 1) out.println(String.format("This combination is %s",
                Arrays.deepToString(cleanPowerSet.toArray())));
        else out.println(String.format("These combinations are %s",
                Arrays.deepToString(cleanPowerSet.toArray())));
    }

    /**
     * Counts how many of the remaining combinations are not winning and removes them from the given set.
     *
     * @param set    PowerSet of all combinations
     * @param jokers amount of jokers the player has
     */
    private Set<Token[]> removeNonWinningCombinations(Set<Token[]> set, int jokers) {
        return set.stream().filter(v -> (v.length + jokers - countJokers(v) == 14)).filter(this::hasNoDuplicates).filter(distinctByKey(Arrays::toString)).collect(Collectors.toSet());         // remove arrays with too few or many token
    }

    private boolean hasNoDuplicates(Token[] t) {
        Arrays.sort(t);

        for (int i = 0; i < t.length - 1; i++)
            if (t[i] == t[i + 1]) return false;

        return true;
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
            for (Object[] t : set)
                elements += t.length;

            if (elements < 12 || elements > 14) continue;

            Token[] t = new Token[elements];
            for (Object[] tokens : set) for (Object token : tokens) t[i++] = (Token) token;

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
    private List<Token[]> getAllStraightsInCurrPlayersHand(int offset) {
        List<Token[]> res = new ArrayList<>();
        Token[] tokens = players[currentPlayer].hand;

        Arrays.sort(tokens);

        Map<Integer, List<Token>> colorMap =
                Arrays.stream(tokens).filter(t -> t.getNumber() >= 0).filter(t -> t.getColor() >= 0).collect(groupingBy(Token::getColor));

        for (List<Token> list : colorMap.values())
            res.addAll(getAllStraightsFromColorList(list, countJokersOfCurrentPlayer()));

        return res;
    }

    private Set<Token[]> getAllStraightsFromColorList(List<Token> list, int jokerInt) {
        Set<Token[]> res = new HashSet<>();
        List<Token> temp = new ArrayList<>();

        int tempJoker = jokerInt, jokerInsert = -1;

        for (int i = 0; i < list.size(); i++) {
            int distanceToNext = list.get((i + 1) % list.size()).getNumber() - list.get(i).getNumber();
            if (!temp.contains(list.get(i)))
                temp.add(list.get(i));

            if (distanceToNext < 0)
                if ((distanceToNext += 13) == 1)
                    temp.add(list.get(0));

            if (distanceToNext <= 1) ;
            else if (distanceToNext == 2 && tempJoker >= 1) {
                tempJoker--;
                jokerInsert = i + 1;
                temp.add(joker);
            } else if (distanceToNext == 3 && tempJoker == 2) {
                tempJoker--;
                tempJoker--;
                jokerInsert = i + 1;
                temp.add(joker);
                temp.add(joker);
            }

            if (distanceToNext > tempJoker + 1 || i == list.size() - 1) {
                if (temp.size() > 2) {
                    res.addAll(new Sets().subsetsWithMinSize(temp, 3));
                    i = jokerInsert == -1 ? i : jokerInsert;
                    jokerInsert = -1;
                    tempJoker = jokerInt;
                }
                temp.clear();
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
    private List<Token[]> getAllFlushesInCurrPlayersHand() {
        List<Token[]> res = new ArrayList<>();
        List<Token> used = new ArrayList<>();
        Token[] tokens = players[currentPlayer].hand;

        Arrays.sort(tokens);

        Set<Token> partRes = new HashSet<>();
        for (Token t : tokens) {
            partRes.add(t);
            for (Token t2 : tokens) {
                if (t == t2) continue;
                if (t.getNumber() == t2.getNumber() && t.getColor() != t2.getColor() && !used.contains(t2))
                    partRes.add(t2);
            }
            used.add(t);
            if (partRes.size() >= 3) res.addAll(new Sets<Token>().subsetsWithMinSize(partRes.stream().toList(), 3));
            partRes.clear();
        }
        return res;
    }
}
