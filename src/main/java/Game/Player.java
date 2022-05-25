package Game;

public class Player {
    String name;
    Token[] hand;

    public Player(String name) {
        this.name = name;
    }

    public void setHand(Token[] hand) {
        this.hand = hand;
    }

    public void getNewToken(Token t) {
        hand[14] = t;
    }

    @Override
    public String toString() {
        return name;
    }
}
