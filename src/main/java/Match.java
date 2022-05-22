import Output.KonsoleOutput;
import Output.Output;
import SpecialSets.Sets;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

class Match {
    Game game;
    Player[] players;
    int curr = 0;
    List<Token> tokens = new ArrayList<>();
    Token joker = new Token(-1, -1);
    Token heavy = new Token(Token.HEAVY, -1);
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

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
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
        out.println("Please throw the first Token, " + players[curr] + ".");
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
    }

    /**
     * Gives the next Player the turn.
     */
    private void nextPlayer() {
        curr = (curr + 1) % players.length;
        out.println("It's " + players[curr] + "s turn.");
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
                players[curr].getNewToken(tokens.get(0));
                tokens.remove(0);
            }
            case "thrown" -> players[curr].getNewToken(lastThrown);
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
     * @return 0 on token was thrown (or winner), -1 on player wants to exit or didn't respond.
     */
    private int thrownToken() {
        String s = game.waitForInput(100);
        if (s == null) return -1;
        String[] parts = s.split(" ");

        try {
            switch (throwAction(parts)) {
                case -1 -> {return -1;}
                case 1 -> {return thrownToken();}
                case 2 -> {return 0;}
            }
        } catch (IllegalArgumentException e) {
            out.println("Invalid Argument. Please write the color of your token and then the number e.g. \"Gelb 5\". For Joker, write \"Joker\".");
            return thrownToken();
        }
        return 0;
    }

    private int testThrown(Token thrown) {
        if (!isInCurrPlayersHand(thrown)) {
            out.println("The thrown Token is not in your Hand. Please throw a Token you have.");
            return thrownToken();
        }
        out.println("The thrown Token is {" + thrown + "}.");
        lastThrown = thrown;
        removeFromCurrPlayersHand(thrown);
        return 0;
    }

    /**
     * determines the action to take next, depending on the input of the player
     *
     * @param parts input of the player
     * @return ID Code based on input. -1 on exit, 0 on thrown Token, 1 on show or wrong win and 2 on win.
     */
    private int throwAction(String[] parts) {
        switch (parts[0]) {
            case "exit" -> {return -1;}

            case "Joker" -> {return testThrown(joker);}

            case "show" -> {
                showHand();
                return 1;
            }

            case "win" -> {
                Token t = parts.length == 3 ? new Token(parts[1], Integer.parseInt(parts[2])) : joker;
                testThrown(t);
                if (currPlayerWon()) {
                    winner = players[curr];
                    return 2;
                } else {
                    out.println("If you throw the token {" + t + "} it isn't a win. Please throw another token.");
                    players[curr].hand[14] = t;
                    return 1;
                }
            }

            default -> {
                if (parts.length == 2)
                    return testThrown(new Token(parts[0], Integer.parseInt(parts[1])));
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
        AtomicBoolean b = new AtomicBoolean(false);     // so it only deletes one token if there are duplicate ones
        players[curr].hand = Arrays.stream(players[curr].hand)
                                   .map(o -> {
                                       if (o.equals(needle) && !b.get()) {
                                           b.set(true);
                                           return heavy;
                                       }
                                       return o;
                                   })
                                   .toArray(Token[]::new);
    }

    /**
     * Checks if the thrown Token is in the hand of the current player.
     *
     * @param needle Token to look for in current players Hand
     * @return thrown in current players hand?
     */
    private boolean isInCurrPlayersHand(Token needle) {
        return Arrays.stream(players[curr].hand).anyMatch(t -> t.equals(needle));
    }

    /**
     * Displays the hand of the current player.
     */
    Player[] showHand() {
        Arrays.sort(players[curr].hand);
        System.out.println(Arrays.toString(players[curr].hand));
        return players;
    }

    /**
     * Tests if the current Player wins after removing a given token
     *
     * @return true if win, false if not.
     */
    boolean currPlayerWon() {
        int jokerCount = countJokersOfCurrPlayer();

        Set<Token[]> combinations = new HashSet<>();

        combinations.addAll(getAllStraightsInCurrPlayersHand());
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
        return set.stream()
                  .filter(v -> (v.length + jokers - countJokers(v) == 14))
                  .filter(this::hasNoDuplicates)
                  .filter(distinctByKey(Arrays::toString))
                  .collect(Collectors.toSet());// remove arrays with too few or many token
    }

    /**
     * Checks if given Token[] contains duplicates. Since jokers are made from the same instance, reduce by one if there
     * are two jokers.
     *
     * @param t Array to be checked
     * @return true if there are no duplicates in the array, else false.
     */
    private boolean hasNoDuplicates(Token[] t) {
        Arrays.sort(t);
        for (int i = 1; i < t.length; i++) if (t[i - 1] == t[i]) return false;
        return true;

//        int i = countJokers(t);
//        i = i == 2 ? 1 : i;
//        return Arrays.stream(t).filter(distinctByKey(Token::toString2)).count() == t.length - i; //
    }

    /**
     * Reduces List<Set<Token[]>> to Set<Token[]>.
     *
     * @param powerSet powerSet within which we reduce
     */
    private Set<Token[]> reduce(Set<Set<Token[]>> powerSet) {
        Set<Token[]> res = new HashSet<>();
        for (Set<Token[]> set : powerSet) {
            List<Token> list = new ArrayList<>();
            for (Object[] tokens1 : set) {
                for (Object token : tokens1) {
                    list.add((Token) token);
                }
            }
            var temp = list.toArray(new Token[0]);
            if (temp.length < 15 && temp.length > 11)
                res.add(temp);
        }
        return res;
    }

    /**
     * Counts the amount of jokers the current player hand.
     *
     * @return amount of jokers the current player hand.
     */
    private int countJokersOfCurrPlayer() {
        return countJokers(players[curr].hand);
    }

    /**
     * Counts the amount of jokers in the given Array.
     *
     * @param tokens Arrays to look for jokers in
     * @return amount of jokers the given Array.
     */
    private int countJokers(Token[] tokens) {
        return (int) Arrays.stream(tokens).filter(t -> t.equals(joker)).count();
    }

    /**
     * Calculates all straights in the hand of the current player. A straight is an at least 3 token long sequence,
     * where the tokens have the same color and consecutive numbers.
     * <p>
     * Depending on the amount of jokers there might be an offset, where the joker would fit in.
     *
     * @return all straights in the hand of the current player
     */
    private List<Token[]> getAllStraightsInCurrPlayersHand() {
        List<Token[]> res = new ArrayList<>();
        Token[] tokens = players[curr].hand;

        Arrays.sort(tokens);

        Map<Integer, List<Token>> colorMap =
                Arrays.stream(tokens)
                      .filter(t -> t.getNumber() >= 0)
                      .filter(t -> t.getColor() >= 0)
                      .collect(groupingBy(Token::getColor));

        for (List<Token> list : colorMap.values())
            res.addAll(getAllStraightsFromColorList(list, countJokersOfCurrPlayer()));

        return res;
    }

    private Set<Token[]> getAllStraightsFromColorList(List<Token> list, int jokerInt) {
        Set<Token[]> res = new HashSet<>();
        List<Token> temp = new ArrayList<>();

        int tempJoker = jokerInt, jokerInsert = -1;

        for (int i = 0; i < list.size(); i++) {
            if (i > 0 && list.get(i).equals(list.get(i - 1)))
                continue;

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
        Token[] tokens = players[curr].hand;

        Arrays.sort(tokens);

        Set<Token> partRes = new HashSet<>();
        for (Token t : tokens) {
            partRes.add(t);
            Arrays.stream(tokens)
                  .filter(t2 -> t.getNumber() == t2.getNumber())
                  .filter(t2 -> t.getColor() != t2.getColor())
                  .filter(t2 -> !used.contains(t2))
                  .forEach(partRes::add);
            used.add(t);
            if (partRes.size() >= 3) res.addAll(new Sets<Token>().subsetsWithMinSize(partRes.stream().toList(), 3));
            partRes.clear();
        }
        return res;
    }
}
