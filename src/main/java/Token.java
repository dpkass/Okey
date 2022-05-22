public class Token implements Comparable {
    final static int JOKER = -1;
    final static int GELB = 0;
    final static int ROT = 1;
    final static int BLAU = 2;
    final static int SCHWARZ = 3;
    /**
     * In the first round we would have a null element and with that problems with sorting. We use this to fill the last
     * space.
     */
    final static int HEAVY = 4;

    private int color;
    private int number;

    Token(int color, int number) {
        this.color = color;
        this.number = number;
    }

    Token(String color, int number) {
        this.number = number;
        switch (color) {
            case "Gelb" -> this.color = GELB;
            case "Rot" -> this.color = ROT;
            case "Blau" -> this.color = BLAU;
            case "Schwarz" -> this.color = SCHWARZ;
            default -> throw new IllegalArgumentException();
        }
    }

    public static int compareToStatic(Object o1, Object o2) {
        return ((Token) o1).compareTo((Token) o2);
    }

    public static String toString2(Token t) {
        return Token.class.getName() + "@" + Integer.toHexString(t.hashCode());
    }

    int getColor() {
        return color;
    }

    int getNumber() {
        return number;
    }

    boolean equals(Token s) {
        return (s.getColor() == color && s.getNumber() == number);
    }

    Token successor() {
        return new Token(this.color, this.number + 1 == 14 ? 1 : this.number + 1);
    }

    Token predecessor() {
        return new Token(this.color, this.number - 1 == 0 ? 1 : this.number - 1);
    }

    @Override
    public String toString() {
        String s = "";
        switch (color) {
            case GELB -> s += "Gelb ";
            case ROT -> s += "Rot ";
            case BLAU -> s += "Blau ";
            case SCHWARZ -> s += "Schwarz ";
            case JOKER -> {return "Joker";}
            case HEAVY -> {return "Heavy";}
        }
        return s + number;
    }

    @Override
    public int compareTo(Object o) {
        Token t = (Token) o;
        int i = this.color - t.getColor();
        return i == 0 ? (this.number - t.getNumber()) : i;
    }

    public int compareToByNumber(Object o) {
        Token t = (Token) o;
        int i = this.number - t.getNumber();
        return i == 0 ? (this.color - t.getColor()) : i;
    }
}
