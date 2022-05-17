class Player {
    String name;
    Token[] hand;

    Player(String name) {
        this.name = name;
    }

    public void setHand(Token[] hand) {
        this.hand = hand;
    }

    public void getNewToken(Token t) {
        hand[14] = t;
    }

    public String getName() {
        return name;
    }
}
