package Game;

import Output.KonsoleOutput;
import Output.Output;
import SpecialSets.Sets;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    /**
     * Like the stream().distinct() but differentiates by a given key.
     *
     * @param keyExtractor Function to extract the key
     * @param <T>          Class of the stream.
     * @return I really don't know
     */
    @Contract (pure = true)
    public static <T> @NotNull Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
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
     * Jokers have color -1 and number -1. For simplification, I took out the choosing joker part for now.
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
     * @return 0 if the match finished with a winner. -1 if a player is not responding or exited the match.
     */
    private int playFirst() {
        out.println("Match starts!!");
        showHand();
        out.println("Please throw the first Token, " + players[curr] + ".");
        if (thrownToken() < 0) return -1;
        return 0;
    }

    /**
     * While there is no winner and no stopping condition is reached the match is played.
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

    /**
     * Checks if the thrown Token is in the players hand. If not, ask the player to throw another Token.
     *
     * @param thrown Thrown Token
     */
    private void testThrown(Token thrown) {
        if (!isInCurrPlayersHand(thrown)) {
            out.println("The thrown Token is not in your Hand. Please throw a Token you have.");
            thrownToken();
        } else {
            out.println("The thrown Token is {" + thrown + "}.");
            lastThrown = thrown;
            removeFromCurrPlayersHand(thrown);
        }
    }

    /**
     * determines the action to take next, depending on the input of the player
     *
     * @param parts input of the player
     * @return ID Code based on input. -1 on exit, 0 on thrown Token, 1 on show or wrong win and 2 on win.
     */
    private int throwAction(String @NotNull [] parts) {
        switch (parts[0]) {
            case "exit" -> {return -1;}

            case "Joker" -> testThrown(joker);

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
                    testThrown(new Token(parts[0], Integer.parseInt(parts[1])));
                else
                    throw new IllegalArgumentException();
            }
        }
        return 0;
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
                                   .toArray((int value) -> new Token[value]);
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
     * @return true if won, false if not.
     */
    boolean currPlayerWon() {
        int jokerCount = countJokersOfCurrPlayer();

        Set<Token[]> combinations = new HashSet<>();

        combinations.addAll(getAllStraightsInCurrPlayersHand());
        combinations.addAll(getAllFlushesInCurrPlayersHand());

        Set<Set<Token[]>> powerSet = new Sets<Token[]>().powerSetWithMaxSize(combinations, 4);
        Map<Token[], Set<Token[]>> flatToCombMap = reduce(powerSet);

        flatToCombMap = removeNonWinningCombinations(flatToCombMap, jokerCount);

        printCombination(flatToCombMap);

        return flatToCombMap.size() > 0;
    }

    private void printCombination(@NotNull Map<Token[], Set<Token[]>> map) {
        String s = "";
        if (map.size() > 0)
            s = map.get(map.keySet().iterator().next()).stream().map(Arrays::toString).collect(Collectors.joining());


        out.println(String.format("There %s %d winning combination%s.", map.size() == 1 ? "is" : "are",
                map.size(), map.size() == 1 ? "" : "s"));

        out.println((map.size() == 1 ? "This combination is " : "One of which is ") + s);
    }

    /**
     * Removes all the remaining combinations, that are not winning from map.
     *
     * @param map    map of all flat Arrays to combined arrays
     * @param jokers amount of jokers the player has
     */
    private Map<Token[], Set<Token[]>> removeNonWinningCombinations(@NotNull Map<Token[], Set<Token[]>> map, int jokers) {
        return map.keySet().stream()
                  .filter(v -> (v.length + jokers - countJokers(v) == 14))
                  .filter(this::hasNoDuplicates)
                  .filter(distinctByKey(Arrays::toString))
                  .collect(Collectors.toMap(Function.identity(), map::get));
    }

    /**
     * Checks if given Token[] contains duplicates.
     *
     * @param t Array to be checked
     * @return true if there are no duplicates in the array, else false.
     */
    private boolean hasNoDuplicates(Token[] t) {
        Arrays.sort(t);
        return IntStream.range(1, t.length).noneMatch(i -> t[i - 1] == t[i]);
    }

    /**
     * Reduces List<Set<Token[]>> to Set<Token[]>.
     *
     * @param powerSet powerSet within which we reduce
     */
    private @NotNull Map<Token[], Set<Token[]>> reduce(@NotNull Set<Set<Token[]>> powerSet) {
        Map<Token[], Set<Token[]>> res = new HashMap<>();
        for (Set<Token[]> set : powerSet) {
            Token[] temp = set.stream()
                              .flatMap(Arrays::stream)
                              .toArray((int value) -> new Token[value]);
            if (temp.length < 15 && temp.length > 11)
                res.put(temp, set);
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
        Token[] tokens = players[curr].hand;

        Arrays.sort(tokens);

        Map<Integer, List<Token>> colorMap =
                Arrays.stream(tokens)
                      .filter(t -> t.getNumber() >= 0)
                      .filter(t -> t.getColor() >= 0)
                      .collect(groupingBy(Token::getColor));

        return colorMap.values()
                       .stream()
                       .flatMap(list -> getAllStraightsInColorList(list, countJokersOfCurrPlayer()).stream())
                       .collect(Collectors.toList());
    }

    Set<Token[]> getAllStraightsInColorList(@NotNull List<Token> list, int jokerInt) {
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

            if (distanceToNext == 2 && tempJoker >= 1) {
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
                    res.addAll(new Sets<Token>().subsetsWithMinSize(Token.class, temp, 3));
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
        Token[] tokens = players[curr].hand;

        Map<Integer, List<Token>> numberMap =
                Arrays.stream(tokens)
                      .filter(t -> !t.equals(joker))
                      .filter(t -> !t.equals(heavy))
                      .collect(groupingBy(Token::getNumber));

        return numberMap.values()
                        .stream()
                        .flatMap(value -> getAllFlushesInNumberList(value).stream())
                        .collect(Collectors.toList());
    }

    Set<Token[]> getAllFlushesInNumberList(List<Token> value) {
        Set<Token[]> subsets = new Sets<Token>().subsetsWithMinSize(Token.class, value, 3);
        return subsets.stream().filter(this::hasNoDuplicates).collect(Collectors.toSet());
    }
}
